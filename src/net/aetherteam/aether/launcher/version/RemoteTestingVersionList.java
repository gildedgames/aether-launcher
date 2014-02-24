package net.aetherteam.aether.launcher.version;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class RemoteTestingVersionList extends RemoteVersionList{

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

}
