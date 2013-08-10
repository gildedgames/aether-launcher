package net.aetherteam.aether.launcher.download;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Downloadable {

	private final URL url;

	private final File target;

	private final boolean forceDownload;

	private final Proxy proxy;

	private final ProgressContainer monitor;

	private int numAttempts;

	private long expectedSize;

	public Downloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload) {
		this.proxy = proxy;
		this.url = remoteFile;
		this.target = localFile;
		this.forceDownload = forceDownload;
		this.monitor = new ProgressContainer();
	}

	public ProgressContainer getMonitor() {
		return this.monitor;
	}

	public long getExpectedSize() {
		return this.expectedSize;
	}

	public void setExpectedSize(long expectedSize) {
		this.expectedSize = expectedSize;
	}

	public String download() throws IOException {
		String localMd5 = null;
		this.numAttempts += 1;

		if ((this.target.getParentFile() != null) && (!this.target.getParentFile().isDirectory())) {
			this.target.getParentFile().mkdirs();
		}
		if ((!this.forceDownload) && (this.target.isFile())) {
			localMd5 = getMD5(this.target);
		}

		if ((this.target.isFile()) && (!this.target.canWrite())) {
			throw new RuntimeException("Do not have write permissions for " + this.target + " - aborting!");
		}
		try {
			HttpURLConnection connection = this.makeConnection(localMd5);
			int status = connection.getResponseCode();

			if (status == 304) {
				return "Used own copy as it matched etag";
			}
			if ((status / 100) == 2) {
				if (this.expectedSize == 0L) {
					this.monitor.setTotal(connection.getContentLength());
				} else {
					this.monitor.setTotal(this.expectedSize);
				}

				InputStream inputStream = new MonitoringInputStream(connection.getInputStream(), this.monitor);
				FileOutputStream outputStream = new FileOutputStream(this.target);
				String md5 = copyAndDigest(inputStream, outputStream);
				String etag = getEtag(connection);

				if (etag.contains("-")) {
					return "Didn't have etag so assuming our copy is good";
				}
				if (etag.equalsIgnoreCase(md5)) {
					return "Downloaded successfully and etag matched";
				}
				throw new RuntimeException(String.format("E-tag did not match downloaded MD5 (ETag was %s, downloaded %s)", new Object[] { etag, md5 }));
			}
			if (this.target.isFile()) {
				return "Couldn't connect to server (responded with " + status + ") but have local file, assuming it's good";
			}
			throw new RuntimeException("Server responded with " + status);
		} catch (IOException e) {
			if (this.target.isFile()) {
				return "Couldn't connect to server (" + e.getClass().getSimpleName() + ": '" + e.getMessage() + "') but have local file, assuming it's good";
			}
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Missing Digest.MD5", e);
		}
	}

	protected HttpURLConnection makeConnection(String localMd5) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) this.url.openConnection(this.proxy);

		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
		connection.setRequestProperty("Expires", "0");
		connection.setRequestProperty("Pragma", "no-cache");
		if (localMd5 != null) {
			connection.setRequestProperty("If-None-Match", localMd5);
		}

		connection.connect();

		return connection;
	}

	public URL getUrl() {
		return this.url;
	}

	public File getTarget() {
		return this.target;
	}

	public boolean shouldIgnoreLocal() {
		return this.forceDownload;
	}

	public int getNumAttempts() {
		return this.numAttempts;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public static String getMD5(File file) {
		DigestInputStream stream = null;

		Object read;
		try {
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] ignored = new byte[65536];

			for (int read1 = stream.read(ignored); read1 >= 1; read1 = stream.read(ignored)) {
				;
			}

			return String.format("%1$032x", new Object[] { new BigInteger(1, stream.getMessageDigest().digest()) });
		} catch (Exception var7) {
			read = null;
		} finally {
			closeSilently(stream);
		}

		return (String) read;
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException localIOException) {
			}
		}
	}

	public static String copyAndDigest(InputStream inputStream, OutputStream outputStream) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[65536];
		try {
			int read = inputStream.read(buffer);
			while (read >= 1) {
				digest.update(buffer, 0, read);
				outputStream.write(buffer, 0, read);
				read = inputStream.read(buffer);
			}
		} finally {
			closeSilently(inputStream);
			closeSilently(outputStream);
		}

		return String.format("%1$032x", new Object[] { new BigInteger(1, digest.digest()) });
	}

	public static String getEtag(HttpURLConnection connection) {
		return getEtag(connection.getHeaderField("ETag"));
	}

	public static String getEtag(String etag) {
		if (etag == null) {
			etag = "-";
		} else if ((etag.startsWith("\"")) && (etag.endsWith("\""))) {
			etag = etag.substring(1, etag.length() - 1);
		}

		return etag;
	}
}