package net.aetherteam.aether.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

public class BackupManager {

	private int[] backups;

	private File backupDir;

	private File savesDir;

	public BackupManager() {

	}

	public void refresh() {
		this.backupDir = new File(Launcher.getInstance().baseDirectory, "backups/");

		File[] backupFiles = this.backupDir.listFiles();
		this.backups = new int[backupFiles.length];

		for (int i = 0; i < backupFiles.length; ++i) {
			this.backups[i] = Integer.valueOf(backupFiles[i].getName().substring(6));
		}

		this.savesDir = new File(Launcher.getInstance().baseDirectory, "saves/");
	}

	public void makeBackup() {
		int unixTimestamp = (int) ((new Date()).getTime() / 1000);
		File target = new File(this.backupDir, "saves-" + unixTimestamp);

		try {
			Files.copy(this.savesDir.toPath(), target.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			Launcher.getInstance().println("Couldn't make backup.");
		}

		this.refresh();
	}

	public void loadBackup(int unixTimestamp) {
		File backup = new File(this.backupDir, "saves-" + unixTimestamp);

		if (backup.exists()) {
			try {
				Files.delete(this.savesDir.toPath()); // TODO: warn the user... hehehe
				this.savesDir = Files.createDirectory(this.savesDir.toPath()).toFile();
				Files.copy(backup.toPath(), this.savesDir.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
