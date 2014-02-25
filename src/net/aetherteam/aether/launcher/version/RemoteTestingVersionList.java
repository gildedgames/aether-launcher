package net.aetherteam.aether.launcher.version;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteTestingVersionList extends RemoteVersionList {

	public RemoteTestingVersionList(Proxy proxy) {
		super(proxy);
	}

	public void refreshVersions() throws IOException {
		this.clearCache();

		RawVersionList versionList = this.gson.fromJson(this.getUrl("testversions/versions.json"), RawVersionList.class);

		for (Version version : versionList.getVersions()) {
			this.versions.add(version);
			this.versionsByName.put(version.getId(), version);
		}
	}

	private static class RawVersionList {

		private List<PartialVersion> versions = new ArrayList<PartialVersion>();

		public List<PartialVersion> getVersions() {
			return this.versions;
		}
	}

	@Override
	public CompleteVersion getCompleteVersion(Version version) throws IOException {
		if ((version instanceof CompleteVersion)) {
			return (CompleteVersion) version;
		}

		if (version == null) {
			throw new IllegalArgumentException("Version cannot be null");
		}

		CompleteVersion complete = this.gson.fromJson(this.getUrl("testversions/" + version.getId() + "/" + version.getId() + ".json"), CompleteVersion.class);

		Collections.replaceAll(this.versions, version, complete);
		this.versionsByName.put(version.getId(), complete);

		return complete;
	}

}
