package net.aetherteam.aether.launcher.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;

import net.aetherteam.aether.launcher.OperatingSystem;

public class RemoteVersionList extends VersionList {

	private final Proxy proxy;

	public RemoteVersionList(Proxy proxy) {
		this.proxy = proxy;
	}

	public boolean hasAllFiles(CompleteVersion version, OperatingSystem os) {
		return true;
	}

	protected String getUrl(String uri) throws IOException {
		// return Http.performGet(new URL("https://s3.amazonaws.com/Minecraft.Download/" + uri), this.proxy);

		InputStream inputStream = new FileInputStream(new File("/Users/cafaxo/documents/launcher/remote/", uri));
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder result = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			if (result.length() > 0) {
				result.append("\n");
			}
			result.append(line);
		}

		reader.close();

		return result.toString();
	}

	public Proxy getProxy() {
		return this.proxy;
	}

}
