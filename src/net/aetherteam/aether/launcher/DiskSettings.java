package net.aetherteam.aether.launcher;

import java.io.File;
import net.aetherteam.aether.launcher.utils.FileUtils;

import com.google.gson.Gson;

public class DiskSettings {	
	private static final Gson gson = new Gson();
	
	public boolean isMusicMuted = false;
	
	public void save()
	{
		try {
			FileUtils.writeStringToFile(new File(Launcher.getInstance().baseDirectory, "settings.json"), gson.toJson(this));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save settings.json.");
		}
	}
	
	public static DiskSettings load()
	{
		File file = new File(Launcher.getInstance().baseDirectory, "settings.json");
		DiskSettings settings = null;

		try {
			if (file.exists())
			{
				settings = gson.fromJson(FileUtils.readFileToString(file), DiskSettings.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (settings == null) {
			settings = new DiskSettings();
		}
		
		return settings;
	}
}
