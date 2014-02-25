package net.aetherteam.aether.launcher.version;

public class VersionSyncInfo {

	private final Version localVersion;

	private final Version remoteVersion;

	private final boolean isInstalled;

	private final boolean isUpToDate;

	private final boolean isTesting;

	public VersionSyncInfo(Version localVersion, Version remoteVersion, boolean installed, boolean upToDate, boolean isTesting) {
		this.localVersion = localVersion;
		this.remoteVersion = remoteVersion;
		this.isInstalled = installed;
		this.isUpToDate = upToDate;
		this.isTesting = isTesting;
	}

	public Version getLocalVersion() {
		return this.localVersion;
	}

	public Version getRemoteVersion() {
		return this.remoteVersion;
	}

	public Version getLatestVersion() {
		VersionSource latestSource = this.getLatestSource();
		if (latestSource == VersionSource.REMOTE || latestSource == VersionSource.REMOTE_TESTING) {
			return this.remoteVersion;
		}
		return this.localVersion;
	}

	public VersionSource getLatestSource() {
		if (this.getLocalVersion() == null) {
			return this.isTesting ? VersionSource.REMOTE_TESTING : VersionSource.REMOTE;
		}
		if (this.getRemoteVersion() == null) {
			return VersionSource.LOCAL;
		}
		if (this.getRemoteVersion().getUpdatedTime().after(this.getLocalVersion().getUpdatedTime())) {
			return this.isTesting ? VersionSource.REMOTE_TESTING : VersionSource.REMOTE;
		}
		return VersionSource.LOCAL;
	}

	public boolean isInstalled() {
		return this.isInstalled;
	}

	public boolean isOnRemote() {
		return this.remoteVersion != null;
	}

	public boolean isUpToDate() {
		return this.isUpToDate;
	}

	@Override
	public String toString() {
		return "VersionSyncInfo{localVersion=" + this.localVersion + ", remoteVersion=" + this.remoteVersion + ", isInstalled=" + this.isInstalled + ", isUpToDate=" + this.isUpToDate + '}';
	}

	public static enum VersionSource {
		REMOTE, LOCAL, REMOTE_TESTING;
	}
}
