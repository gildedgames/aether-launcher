package net.aetherteam.aether.launcher.start;

import java.io.File;

public class Util {

	public static final String APPLICATION_NAME = "minecraft";

	public static OS getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return OS.WINDOWS;
		}
		if (osName.contains("mac")) {
			return OS.MACOS;
		}
		if (osName.contains("linux")) {
			return OS.LINUX;
		}
		if (osName.contains("unix")) {
			return OS.LINUX;
		}
		return OS.UNKNOWN;
	}

	public static File getWorkingDirectory() {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (Util.getPlatform()) {
		case SOLARIS:
		case LINUX:
			workingDirectory = new File(userHome, ".aether/");
			break;
		case WINDOWS:
			String applicationData = System.getenv("APPDATA");
			String folder = applicationData != null ? applicationData : userHome;

			workingDirectory = new File(folder, ".aether/");
			break;
		case MACOS:
			workingDirectory = new File(userHome, "Library/Application Support/aether");
			break;
		default:
			workingDirectory = new File(userHome, "aether/");
		}

		return workingDirectory;
	}

	public static enum OS {
		WINDOWS, MACOS, SOLARIS, LINUX, UNKNOWN;
	}
}