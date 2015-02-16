package net.aetherteam.aether.launcher.start;

import net.aetherteam.aether.launcher.gui.LauncherDisplay;

public class DevStart {
	public void start(String librarypath) {
		System.setProperty("org.lwjgl.librarypath", librarypath);

		new LauncherDisplay();
	}

	public static void main(String[] args) {
		if ((args.length % 2) != 0)
		{
			throw new RuntimeException("Invalid amount of arguments. Must be divisble by 2.");
		}

		String librarypath = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equalsIgnoreCase("--natives"))
			{
				librarypath = args[i + 1];
			}
		}
		
		if (librarypath == null)
		{
			throw new RuntimeException("Missing argument: --natives");
		}
		
		new DevStart().start(librarypath);
	}

}