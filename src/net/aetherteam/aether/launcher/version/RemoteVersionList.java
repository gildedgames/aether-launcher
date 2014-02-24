package net.aetherteam.aether.launcher.version;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import net.aetherteam.aether.launcher.OperatingSystem;

public class RemoteVersionList extends VersionList {

	private final Proxy proxy;

	public RemoteVersionList(Proxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public boolean hasAllFiles(CompleteVersion version, OperatingSystem os) {
		return true;
	}

	@Override
	protected String getUrl(String uri) throws IOException {
		return Http.performGet(new URL("http://aether.craftnode.me/launcher/" + uri), this.proxy);
	}

	public Proxy getProxy() {
		return this.proxy;
	}

}
