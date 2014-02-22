package net.aetherteam.aether.launcher.start;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Bootstrap {

	public int getLocalVersion() {
		File workingDir = Util.getWorkingDirectory();
		File launcherVersionFile = new File(workingDir, "launcher-version.txt");
		int localLauncherVersion = 0;

		if (launcherVersionFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(launcherVersionFile));
				localLauncherVersion = Integer.parseInt(br.readLine());
				br.close();
			} catch (FileNotFoundException e) {
			} catch (NumberFormatException e) {
			} catch (IOException e) {
			}
		}

		return localLauncherVersion;
	}

	public int getRemoteVersion() {
		URL website;
		int remoteLauncherVersion = 0;

		try {
			website = new URL("http://aether.craftnode.me/launcher/launcher-version.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream(), "UTF-8"));
			remoteLauncherVersion = Integer.parseInt(br.readLine());
			br.close();
		} catch (MalformedURLException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return remoteLauncherVersion;
	}

	private void downloadFile(String url, File target) {
		URL website;
		try {
			website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(target);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateLocalVersion(int version) {
		File workingDir = Util.getWorkingDirectory();
		File launcherVersionFile = new File(workingDir, "launcher-version.txt");

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(launcherVersionFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
			bw.write(((Integer) version).toString());
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void startLauncher() {
		File workingDir = Util.getWorkingDirectory();

		workingDir.mkdirs();

		File launcherNatives = new File(workingDir, "launcher-natives.zip");
		File launcherJar = new File(workingDir, "launcher.jar");
		File launcherNativesDir = new File(workingDir, "launcher-natives");

		try {
			launcherJar.createNewFile();
		} catch (IOException e1) {
		}

		int remoteLauncherVersion = this.getRemoteVersion();
		int localLauncherVersion = this.getLocalVersion();

		if (localLauncherVersion < remoteLauncherVersion) {
			this.downloadFile("http://aether.craftnode.me/launcher/launcher-natives.zip", launcherNatives);

			UnZip unzip = new UnZip();
			unzip.unZipIt(launcherNatives, launcherNativesDir);

			this.downloadFile("http://aether.craftnode.me/launcher/launcher.jar", launcherJar);
			this.updateLocalVersion(remoteLauncherVersion);
		}

		System.out.println("Starting launcher.");

		System.setProperty("org.lwjgl.librarypath", launcherNativesDir.getAbsolutePath());

		try {
			Class<?> aClass = new URLClassLoader(new URL[] { launcherJar.toURI().toURL() }).loadClass("net.aetherteam.aether.launcher.gui.LauncherDisplay");
			aClass.newInstance();
		} catch (Exception e) {
			System.out.println("Unable to start: " + e);
		}
	}

	public static void main(String[] args) {
		Bootstrap boostrap = new Bootstrap();
		boostrap.startLauncher();
	}

}
