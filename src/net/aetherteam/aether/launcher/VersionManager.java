package net.aetherteam.aether.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
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
import net.aetherteam.aether.launcher.utils.StringUtils;
import net.aetherteam.aether.launcher.version.CompleteVersion;
import net.aetherteam.aether.launcher.version.Http;
import net.aetherteam.aether.launcher.version.LocalVersionList;
import net.aetherteam.aether.launcher.version.RemoteTestingVersionList;
import net.aetherteam.aether.launcher.version.RemoteVersionList;
import net.aetherteam.aether.launcher.version.Version;
import net.aetherteam.aether.launcher.version.VersionList;
import net.aetherteam.aether.launcher.version.VersionSyncInfo;
import net.aetherteam.aether.launcher.version.VersionSyncInfo.VersionSource;
import net.aetherteam.aether.launcher.version.assets.AssetDownloadable;
import net.aetherteam.aether.launcher.version.assets.AssetIndex;

import com.google.gson.Gson;

public class VersionManager {

	private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	private LocalVersionList localVersionList;

	private RemoteVersionList remoteVersionList;

	private RemoteTestingVersionList remoteTestingVersionList;

	private final Gson gson = new Gson();

	private String selectedProfile;

	public VersionManager(LocalVersionList localVersionList, RemoteVersionList remoteVersionList, RemoteTestingVersionList remoteTestingVersionList) {
		this.localVersionList = localVersionList;
		this.remoteVersionList = remoteVersionList;
		this.remoteTestingVersionList = remoteTestingVersionList;
	}

	public ThreadPoolExecutor getExecutorService() {
		return this.executorService;
	}

	public void refreshVersions(String selectedProfile) throws IOException {
		this.selectedProfile = selectedProfile;
		this.localVersionList.refreshVersions();
		this.remoteVersionList.refreshVersions();
		this.remoteTestingVersionList.refreshVersions();

		boolean isDonator = StringUtils.isNotBlank(selectedProfile) ? this.isBetaTester(selectedProfile) : false;

		if ((this.localVersionList instanceof LocalVersionList)) {
			for (Version version : this.remoteVersionList.getVersions()) {
				String id = version.getId();

				if (this.localVersionList.getVersion(id) != null) {
					this.localVersionList.removeVersion(id);
					this.localVersionList.addVersion(this.remoteVersionList.getCompleteVersion(id));
					this.localVersionList.saveVersion(this.localVersionList.getCompleteVersion(id));
				}
			}

			if (isDonator) {
				for (Version version : this.remoteTestingVersionList.getVersions()) {
					String id = version.getId();
					version.setIsTestVersion(true);

					if (this.localVersionList.getVersion(id) != null) {
						this.localVersionList.removeVersion(id);
						this.localVersionList.addVersion(this.remoteTestingVersionList.getCompleteVersion(id));
						this.localVersionList.saveVersion(this.localVersionList.getCompleteVersion(id));
					}
				}
			}
		}

		this.localVersionList.saveVersionList();
	}

	public String[] getVersions() {

		String[] versionIds;
		if (StringUtils.isNotBlank(selectedProfile) && this.isBetaTester(this.selectedProfile) && !this.remoteVersionList.getVersions().isEmpty()) {
			VersionList versionList = this.remoteVersionList;
			int remoteLength = versionList.getVersions().size();
			int length = remoteLength + remoteTestingVersionList.getVersions().size();
			versionIds = new String[length];
			int i;
			for (i = 0; i < remoteLength; ++i) {
				versionIds[i] = versionList.getVersions().get(i).getId();
			}
			for (; i < length; ++i) {
				Version version = remoteTestingVersionList.getVersions().get(i - remoteLength);
				versionIds[i] = version.getId();
				version.setIsTestVersion(true);
			}
		} else {
			VersionList versionList = this.remoteVersionList.getVersions().isEmpty() ? this.localVersionList : this.remoteVersionList;
			versionIds = new String[versionList.getVersions().size()];

			for (int i = 0; i < versionList.getVersions().size(); ++i) {
				versionIds[i] = versionList.getVersions().get(i).getId();
			}
		}

		return versionIds;
	}

	public VersionSyncInfo getVersionSyncInfo(Version version) {
		return this.getVersionSyncInfo(version.getId());
	}

	public VersionSyncInfo getVersionSyncInfo(String name) {
		Version remoteVersion = this.remoteVersionList.getVersion(name);
		boolean testingVersion = false;
		if (remoteVersion == null) {
			remoteVersion = this.remoteTestingVersionList.getVersion(name);
			testingVersion = true;
		}
		return this.getVersionSyncInfo(this.localVersionList.getVersion(name), remoteVersion, testingVersion);
	}

	public VersionSyncInfo getVersionSyncInfo(Version localVersion, Version remoteVersion, boolean testingVersion) {
		boolean installed = localVersion != null;
		boolean upToDate = installed;

		if ((installed) && (remoteVersion != null)) {
			upToDate = !remoteVersion.getUpdatedTime().after(localVersion.getUpdatedTime());
		}
		if ((localVersion instanceof CompleteVersion)) {
			upToDate &= this.localVersionList.hasAllFiles((CompleteVersion) localVersion, OperatingSystem.getCurrentPlatform());
		}

		return new VersionSyncInfo(localVersion, remoteVersion, installed, upToDate, testingVersion);
	}

	public List<VersionSyncInfo> getInstalledVersions() {
		List<VersionSyncInfo> result = new ArrayList<VersionSyncInfo>();

		for (Version version : this.localVersionList.getVersions()) {
			if (version.getUpdatedTime() != null) {
				VersionSyncInfo syncInfo = this.getVersionSyncInfo(version, this.remoteVersionList.getVersion(version.getId()), false);
				if (syncInfo == null) {
					syncInfo = this.getVersionSyncInfo(version, this.remoteTestingVersionList.getVersion(version.getId()), true);
				}
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

	public VersionList getRemoteTestingVersionList() {
		return this.remoteTestingVersionList;
	}

	public void setSelectedProfile(String profile) {
		this.selectedProfile = profile;
	}

	public CompleteVersion getLatestCompleteVersion(VersionSyncInfo syncInfo) throws IOException {
		VersionSource latestSource = syncInfo.getLatestSource();
		if (latestSource != VersionSource.LOCAL) {
			CompleteVersion result = null;
			IOException exception = null;
			try {
				result = latestSource == VersionSource.REMOTE ? this.remoteVersionList.getCompleteVersion(syncInfo.getLatestVersion()) : this.remoteTestingVersionList.getCompleteVersion(syncInfo.getLatestVersion());
			} catch (IOException e) {
				exception = e;
				try {
					result = this.localVersionList.getCompleteVersion(syncInfo.getLatestVersion());
				} catch (IOException localIOException1) {
				}
			}
			if (result != null) {
				result.setIsTestVersion(latestSource == VersionSource.REMOTE_TESTING);
				return result;
			}
			throw exception;
		}

		CompleteVersion version = this.localVersionList.getCompleteVersion(syncInfo.getLatestVersion());
		if (version != null) {
			version.setIsTestVersion(latestSource == VersionSource.REMOTE_TESTING);
		}
		return version;
	}

	public DownloadJob downloadVersion(VersionSyncInfo syncInfo, DownloadJob job) throws IOException {
		CompleteVersion version = this.getLatestCompleteVersion(syncInfo);
		File baseDirectory = this.localVersionList.getBaseDirectory();
		Proxy proxy = this.remoteVersionList.getProxy();
		// TODO check this shit bro
		job.addDownloadables(version.getRequiredDownloadables(OperatingSystem.getCurrentPlatform(), proxy, baseDirectory, false));

		String jarFile = "versions/" + version.getMinecraftVersion() + "/" + version.getMinecraftVersion() + ".jar";
		job.addDownloadables(new Downloadable[]{new EtagDownloadable(proxy, new URL("https://s3.amazonaws.com/Minecraft.Download/" + jarFile), new File(baseDirectory, jarFile), false)});

		return job;
	}

	public Set<Downloadable> getResourceFiles(Proxy proxy, File baseDirectory) {
		Set result = new HashSet();
		InputStream inputStream = null;
		File assets = new File(baseDirectory, "assets");
		File objectsFolder = new File(assets, "objects");
		File indexesFolder = new File(assets, "indexes");
		String indexName = null;// version.getAssets();
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

	public boolean isDonator(String username) {
		URL website;

		try {
			website = new URL("http://www.gilded-games.com/aether/signature.php?name=" + username);
			URLConnection connection = website.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			if (!br.readLine().equals("false")) {
				return true;
			}

			br.close();
		} catch (MalformedURLException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return false;
	}

	public boolean isBetaTester(String username) {
		URL website;

		try {
			website = new URL("http://aether.craftnode.me/launcher/beta_testers.php?name=" + username);
			URLConnection connection = website.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			if (!br.readLine().equals("false ")) {
				return true;
			}

			br.close();
		} catch (MalformedURLException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return false;
	}

}
