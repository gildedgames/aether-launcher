package net.aetherteam.aether.launcher.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import net.aetherteam.aether.launcher.utils.UnpackUtil;

public class CompressedDownloadable extends ChecksummedDownloadable {

	public CompressedDownloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload) {
		super(proxy, remoteFile, localFile, forceDownload);
	}

	@Override
	public String download() throws IOException {
		byte[] buffer = new byte[8 * 1024];

		URLConnection urlConnect = this.getUrl().openConnection();
		urlConnect.setDoInput(true);
		urlConnect.setDoOutput(true);
		
		InputStream input = urlConnect.getInputStream();
		try {
		  OutputStream output = new FileOutputStream(this.getTarget().getAbsolutePath() + ".pack.xz");
		  try {
		    int bytesRead;
		    while ((bytesRead = input.read(buffer)) != -1) {
		      output.write(buffer, 0, bytesRead);
		    }
		  } finally {
		    output.close();
		  }
		} finally {
		  input.close();
		}
		UnpackUtil.unpackLibrary(this.getTarget(), Files.readAllBytes(new File(this.getTarget().getAbsolutePath() + ".pack.xz").toPath()));
		
		return "hmmm";
	}
}
