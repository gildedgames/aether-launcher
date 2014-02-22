package net.aetherteam.aether.launcher.version.assets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import net.aetherteam.aether.launcher.download.Downloadable;
import net.aetherteam.aether.launcher.download.MonitoringInputStream;

public class AssetDownloadable extends Downloadable {

	private final String expectedHash;

	private final long expectedFilesize;

	public AssetDownloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload, String expectedHash, long expectedFilesize) {
		super(proxy, remoteFile, localFile, forceDownload);
		this.expectedHash = expectedHash;
		this.expectedFilesize = expectedFilesize;
	}

	@Override
	public String download() throws IOException {
		this.numAttempts += 1;
		this.ensureFileWritable();

		if ((this.getTarget().isFile()) && (this.getTarget().length() == this.expectedFilesize)) {
			return "Have local file and it's the same size; assuming it's okay!";
		}
		try {
			HttpURLConnection connection = this.makeConnection(this.getUrl());
			int status = connection.getResponseCode();

			if ((status / 100) == 2) {
				this.updateExpectedSize(connection);

				InputStream inputStream = new MonitoringInputStream(connection.getInputStream(), this.getMonitor());
				FileOutputStream outputStream = new FileOutputStream(this.getTarget());
				String hash = copyAndDigest(inputStream, outputStream, "SHA", 40);

				if (hash.equalsIgnoreCase(this.expectedHash)) {
					return "Downloaded successfully and hash matched";
				}
				throw new RuntimeException(String.format("Hash did not match downloaded file (Expected %s, downloaded %s)", new Object[] { this.expectedHash, hash }));
			}
			if (this.getTarget().isFile()) {
				return "Couldn't connect to server (responded with " + status + ") but have local file, assuming it's good";
			}
			throw new RuntimeException("Server responded with " + status);
		} catch (IOException e) {
			if (this.getTarget().isFile()) {
				return "Couldn't connect to server (" + e.getClass().getSimpleName() + ": '" + e.getMessage() + "') but have local file, assuming it's good";
			}
			throw e;
		}
	}
}