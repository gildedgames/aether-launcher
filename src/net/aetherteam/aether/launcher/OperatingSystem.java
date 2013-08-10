package net.aetherteam.aether.launcher;

import java.io.File;
import java.net.URI;

public enum OperatingSystem {
	LINUX("linux", new String[] { "linux", "unix" }), WINDOWS("windows", new String[] { "win" }), OSX("osx", new String[] { "mac" }), UNKNOWN("unknown", new String[0]);

	private final String name;

	private final String[] aliases;

	private OperatingSystem(String name, String[] aliases) {
		this.name = name;
		this.aliases = (aliases == null ? new String[0] : aliases);
	}

	public String getName() {
		return this.name;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public boolean isSupported() {
		return this != UNKNOWN;
	}

	public String getJavaDir() {
		String separator = System.getProperty("file.separator");
		String path = System.getProperty("java.home") + separator + "bin" + separator;

		if ((getCurrentPlatform() == WINDOWS) && (new File(path + "javaw.exe").isFile())) {
			return path + "javaw.exe";
		}

		return path + "java";
	}

	public static OperatingSystem getCurrentPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();

		for (OperatingSystem os : values()) {
			for (String alias : os.getAliases()) {
				if (osName.contains(alias)) {
					return os;
				}
			}
		}

		return UNKNOWN;
	}

	public static void openLink(URI link) {
		try {
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			desktopClass.getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { link });
		} catch (Throwable e) {
			Launcher.getInstance().println("Failed to open link " + link.toString(), e);
		}
	}
}