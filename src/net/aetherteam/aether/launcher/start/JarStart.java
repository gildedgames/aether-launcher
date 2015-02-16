package net.aetherteam.aether.launcher.start;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JarStart {

	@SuppressWarnings("resource")
	public void start() {
		File workingDir = Util.getWorkingDirectory();

		File launcherJar = new File(workingDir, "launcher.jar");
		File launcherNativesDir = new File(workingDir, "launcher-natives");

		System.setProperty("org.lwjgl.librarypath", launcherNativesDir.getAbsolutePath());

		try {
			Class<?> aClass = new URLClassLoader(new URL[]{launcherJar.toURI().toURL()}).loadClass("net.aetherteam.aether.launcher.gui.LauncherDisplay");
			aClass.newInstance();
		} catch (Exception e) {
			System.out.println("Unable to start: " + e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		JarStart start = new JarStart();
		start.start();
	}

}
