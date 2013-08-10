package net.aetherteam.aether.launcher;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.aetherteam.aether.launcher.download.DownloadJob;
import net.aetherteam.aether.launcher.download.Downloadable;
import net.aetherteam.aether.launcher.version.CompleteVersion;
import net.aetherteam.aether.launcher.version.LocalVersionList;
import net.aetherteam.aether.launcher.version.RemoteVersionList;
import net.aetherteam.aether.launcher.version.Version;
import net.aetherteam.aether.launcher.version.VersionList;
import net.aetherteam.aether.launcher.version.VersionSyncInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VersionManager {

	private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	private LocalVersionList localVersionList;

	private RemoteVersionList remoteVersionList;

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

		String jarFile = "versions/" + version.getId() + "/" + version.getId() + ".jar";
		job.addDownloadables(new Downloadable[] { new Downloadable(proxy, new URL("https://s3.amazonaws.com/Minecraft.Download/" + jarFile), new File(baseDirectory, jarFile), false) });

		return job;
	}

	public Set<Downloadable> getResourceFiles(Proxy proxy, File baseDirectory) {
		Set<Downloadable> result = new HashSet<Downloadable>();

		try {
			URL resourceUrl = new URL("https://s3.amazonaws.com/Minecraft.Resources/");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(resourceUrl.openStream());
			NodeList nodeLst = doc.getElementsByTagName("Contents");

			long start = System.nanoTime();

			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node node = nodeLst.item(i);

				if (node.getNodeType() == 1) {
					Element element = (Element) node;
					String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
					String etag = element.getElementsByTagName("ETag") != null ? element.getElementsByTagName("ETag").item(0).getChildNodes().item(0).getNodeValue() : "-";

					long size = Long.parseLong(element.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());

					if (size > 0L) {
						File file = new File(baseDirectory, "assets/" + key);

						if (etag.length() > 1) {
							etag = Downloadable.getEtag(etag);

							if ((file.isFile()) && (file.length() == size)) {
								String localMd5 = Downloadable.getMD5(file);

								if (localMd5.equals(etag)) {
									continue;
								}
							}
						}

						Downloadable downloadable = new Downloadable(proxy, new URL("https://s3.amazonaws.com/Minecraft.Resources/" + key), file, false);
						downloadable.setExpectedSize(size);

						result.add(downloadable);
					}
				}
			}

			long end = System.nanoTime();
			long delta = end - start;
			Launcher.getInstance().println("Delta time to compare resources: " + (delta / 1000000L) + " ms ");
		} catch (Exception ex) {
			Launcher.getInstance().println("Couldn't download resources", ex);
		}

		return result;
	}

}
