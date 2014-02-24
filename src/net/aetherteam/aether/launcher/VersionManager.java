package net.aetherteam.aether.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.aetherteam.aether.launcher.download.DownloadJob;
import net.aetherteam.aether.launcher.download.Downloadable;
import net.aetherteam.aether.launcher.download.EtagDownloadable;
import net.aetherteam.aether.launcher.utils.FileUtils;
import net.aetherteam.aether.launcher.version.CompleteVersion;
import net.aetherteam.aether.launcher.version.Http;
import net.aetherteam.aether.launcher.version.LocalVersionList;
import net.aetherteam.aether.launcher.version.RemoteVersionList;
import net.aetherteam.aether.launcher.version.Version;
import net.aetherteam.aether.launcher.version.VersionList;
import net.aetherteam.aether.launcher.version.VersionSyncInfo;
import net.aetherteam.aether.launcher.version.assets.AssetDownloadable;
import net.aetherteam.aether.launcher.version.assets.AssetIndex;

import com.google.gson.Gson;

public class VersionManager {

	private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	private LocalVersionList localVersionList;

	private RemoteVersionList remoteVersionList;

	private final Gson gson = new Gson();

	public VersionManager(LocalVersionList localVersionList, RemoteVersionList remoteVersionList) {
		this.localVersionList = localVersionList;
		this.remoteVersionList = remoteVersionList;
	}

	public ThreadPoolExecutor getExecutorService() {
		return this.executorService;
	}

	public void refreshVersions() throws IOException {
		this.localVersionList.refreshVersions();
		this.remoteVersionList.refreshVersions();

		if ((this.localVersionList instanceof LocalVersionList)) {
			for (Version version : this.remoteVersionList.getVersions()) {
				String id = version.getId();

				if (this.localVersionList.getVersion(id) != null) {
					this.localVersionList.removeVersion(id);
					this.localVersionList.addVersion(this.remoteVersionList.getCompleteVersion(id));
					this.localVersionList.saveVersion(this.localVersionList.getCompleteVersion(id));
				}
			}
		}

		this.localVersionList.saveVersionList();
	}

	public String[] getVersions() {
		VersionList versionList = this.remoteVersionList.getVersions().isEmpty() ? this.localVersionList : this.remoteVersionList;

		String[] versionIds = new String[versionList.getVersions().size()];

		for (int i = 0; i < versionList.getVersions().size(); ++i) {
			versionIds[i] = versionList.getVersions().get(i).getId();
		}

		return versionIds;
	}

	public VersionSyncInfo getVersionSyncInfo(Version version) {
		return this.getVersionSyncInfo(version.getId());
	}

	public VersionSyncInfo getVersionSyncInfo(String name) {
		return this.getVersionSyncInfo(this.localVersionList.getVersion(name), this.remoteVersionList.getVersion(name));
	}

	public VersionSyncInfo getVersionSyncInfo(Version localVersion, Version remoteVersion) {
		boolean installed = localVersion != null;
		boolean upToDate = installed;

		if ((installed) && (remoteVersion != null)) {
			upToDate = !remoteVersion.getUpdatedTime().after(localVersion.getUpdatedTime());
		}
		if ((localVersion instanceof CompleteVersion)) {
			upToDate &= this.localVersionList.hasAllFiles((CompleteVersion) localVersion, OperatingSystem.getCurrentPlatform());
		}

		return new VersionSyncInfo(localVersion, remoteVersion, installed, upToDate);
	}

	public List<VersionSyncInfo> getInstalledVersions() {
		List<VersionSyncInfo> result = new ArrayList<VersionSyncInfo>();

		for (Version version : this.localVersionList.getVersions()) {
			if (version.getUpdatedTime() != null) {
				VersionSyncInfo syncInfo = this.getVersionSyncInfo(version, this.remoteVersionList.getVersion(version.getId()));
				result.add(syncInfo);
			}
		}
		return result;
	}

	public VersionList getRemoteVersionList() {
		return this.remoteVersionList;
	}

	public VersionList getLocalVersionList() {
		return this.localVersionList;
	}

	public CompleteVersion getLatestCompleteVersion(VersionSyncInfo syncInfo) throws IOException {
		if (syncInfo.getLatestSource() == VersionSyncInfo.VersionSource.REMOTE) {
			CompleteVersion result = null;
			IOException exception = null;
			try {
				result = this.remoteVersionList.getCompleteVersion(syncInfo.getLatestVersion());
			} catch (IOException e) {
				exception = e;
				try {
					result = this.localVersionList.getCompleteVersion(syncInfo.getLatestVersion());
				} catch (IOException localIOException1) {
				}
			}
			if (result != null) {
				return result;
			}
			throw exception;
		}

		return this.localVersionList.getCompleteVersion(syncInfo.getLatestVersion());
	}

	public DownloadJob downloadVersion(VersionSyncInfo syncInfo, DownloadJob job) throws IOException {
		CompleteVersion version = this.getLatestCompleteVersion(syncInfo);
		File baseDirectory = this.localVersionList.getBaseDirectory();
		Proxy proxy = this.remoteVersionList.getProxy();

		job.addDownloadables(version.getRequiredDownloadables(OperatingSystem.getCurrentPlatform(), proxy, baseDirectory, false));

		String jarFile = "versions/" + version.getMinecraftVersion() + "/" + version.getMinecraftVersion() + ".jar";
		job.addDownloadables(new Downloadable[] { new EtagDownloadable(proxy, new URL("https://s3.amazonaws.com/Minecraft.Download/" + jarFile), new File(baseDirectory, jarFile), false) });

		return job;
	}

	public Set<Downloadable> getResourceFiles(Proxy proxy, File baseDirectory) {
		Set result = new HashSet();
		InputStream inputStream = null;
		File assets = new File(baseDirectory, "assets");
		File objectsFolder = new File(assets, "objects");
		File indexesFolder = new File(assets, "indexes");
		String indexName = null;//version.getAssets();
		long start = System.nanoTime();

		if (indexName == null) {
			indexName = "legacy";
		}
		File indexFile = new File(indexesFolder, indexName + ".json");
		try {
			String json = Http.performGet(new URL("https://s3.amazonaws.com/Minecraft.Download/indexes/" + indexName + ".json"), proxy);
			FileUtils.writeStringToFile(indexFile, json);
			AssetIndex index = this.gson.fromJson(json, AssetIndex.class);

			for (AssetIndex.AssetObject object : index.getUniqueObjects()) {
				String filename = object.getHash().substring(0, 2) + "/" + object.getHash();
				File file = new File(objectsFolder, filename);
				if ((!file.isFile()) || (file.length() != object.getSize())) {
					Downloadable downloadable = new AssetDownloadable(proxy, new URL("http://resources.download.minecraft.net/" + filename), file, false, object.getHash(), object.getSize());
					downloadable.setExpectedSize(object.getSize());
					result.add(downloadable);
				}
			}

			long end = System.nanoTime();
			long delta = end - start;
			Launcher.getInstance().println("Delta time to compare resources: " + (delta / 1000000L) + " ms ");
		} catch (Exception ex) {
			Launcher.getInstance().println("Couldn't download resources", ex);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

}
