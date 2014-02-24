package net.aetherteam.aether.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.aetherteam.aether.launcher.authentication.AuthenticationService;
import net.aetherteam.aether.launcher.download.DownloadJob;
import net.aetherteam.aether.launcher.download.DownloadListener;
import net.aetherteam.aether.launcher.download.Downloadable;
import net.aetherteam.aether.launcher.gui.LauncherDisplay;
import net.aetherteam.aether.launcher.gui.forms.LoadingForm;
import net.aetherteam.aether.launcher.process.JavaProcess;
import net.aetherteam.aether.launcher.process.JavaProcessLauncher;
import net.aetherteam.aether.launcher.process.JavaProcessRunnable;
import net.aetherteam.aether.launcher.utils.FileUtils;
import net.aetherteam.aether.launcher.utils.StrSubstitutor;
import net.aetherteam.aether.launcher.version.CompleteVersion;
import net.aetherteam.aether.launcher.version.ExtractRules;
import net.aetherteam.aether.launcher.version.Library;
import net.aetherteam.aether.launcher.version.LocalVersionList;
import net.aetherteam.aether.launcher.version.Mod;
import net.aetherteam.aether.launcher.version.VersionList;
import net.aetherteam.aether.launcher.version.VersionSyncInfo;
import net.aetherteam.aether.launcher.version.assets.AssetIndex;

import com.google.gson.Gson;
import com.google.gson.internal.bind.DateTypeAdapter;

public class GameLauncher implements DownloadListener, JavaProcessRunnable, Runnable {

	private CompleteVersion version;

	private File nativeDir;

	private final Gson gson = new Gson();

	private final DateTypeAdapter dateAdapter = new DateTypeAdapter();

	public void playGame() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		Launcher.getInstance().println("Getting syncinfo for selected version");

		String selectedVersion = Launcher.getInstance().getProfileManager().getAuthenticationService().getSelectedVersion();

		if (selectedVersion == null) {
			selectedVersion = Launcher.getInstance().getVersionManager().getVersions()[0];
		}

		VersionSyncInfo syncInfo = Launcher.getInstance().getVersionManager().getVersionSyncInfo(selectedVersion);

		Launcher.getInstance().println("Queueing library & version downloads");
		try {
			this.version = Launcher.getInstance().getVersionManager().getLatestCompleteVersion(syncInfo);
		} catch (IOException e) {
			Launcher.getInstance().println("Couldn't get complete version info for " + syncInfo.getLatestVersion(), e);
			return;
		}

		if (!this.version.appliesToCurrentEnvironment()) {
			String reason = this.version.getIncompatibilityReason();
			if (reason == null) {
				reason = "This version is incompatible with your computer. Please try another one by going into Edit Profile and selecting one through the dropdown. Sorry!";
			}
			Launcher.getInstance().println("Version " + this.version.getId() + " is incompatible with current environment: " + reason);

			return;
		}

		if (this.version.getMinimumLauncherVersion() > 5) {
			Launcher.getInstance().println("An update to your launcher is available and is required to play " + this.version.getId() + ". Please restart your launcher.");

			return;
		}

		if (!syncInfo.isInstalled()) {
			try {
				VersionList localVersionList = Launcher.getInstance().getVersionManager().getLocalVersionList();
				((LocalVersionList) localVersionList).saveVersion(this.version);
				Launcher.getInstance().println("Installed " + syncInfo.getLatestVersion());
			} catch (IOException e) {
				Launcher.getInstance().println("Couldn't save version info to install " + syncInfo.getLatestVersion(), e);
				return;
			}
		}

		try {
			DownloadJob job = new DownloadJob("Version & Libraries", false, this);
			Launcher.getInstance().getVersionManager().downloadVersion(syncInfo, job);
			job.addDownloadables(Launcher.getInstance().getVersionManager().getResourceFiles(Launcher.getInstance().getProxy(), Launcher.getInstance().getBaseDirectory()));
			job.startDownloading(Launcher.getInstance().getVersionManager().getExecutorService());
		} catch (IOException e) {
			Launcher.getInstance().println("Couldn't get version info for " + syncInfo.getLatestVersion(), e);
			return;
		}
	}

	protected void launchGame() {
		Launcher.getInstance().println("Copying mods");

		for (Mod mod : this.version.getMods()) {
			File source = new File(Launcher.instance.getBaseDirectory(), mod.getVersionPath(this.version));
			File target = new File(Launcher.instance.getBaseDirectory(), mod.getPath());

			try {
				target.getParentFile().mkdirs();
				target.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			Launcher.getInstance().println("Copying " + source.toString() + " to " + target.toString());

			try {
				Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Launcher.getInstance().println("Launching game");

		if (this.version == null) {
			Launcher.getInstance().println("Aborting launch; version is null?");
			return;
		}

		this.nativeDir = new File(Launcher.getInstance().getBaseDirectory(), "versions/" + this.version.getMinecraftVersion() + "/" + this.version.getMinecraftVersion() + "-natives");
		if (!this.nativeDir.isDirectory()) {
			this.nativeDir.mkdirs();
		}
		Launcher.getInstance().println("Unpacking natives to " + this.nativeDir);
		try {
			this.unpackNatives(this.version, this.nativeDir);
		} catch (IOException e) {
			Launcher.getInstance().println("Couldn't unpack natives!", e);
			return;
		}

		File assetsDir;
		try {
			assetsDir = this.reconstructAssets();
		} catch (IOException e) {
			e.printStackTrace();
			Launcher.getInstance().println("Couldn't unpack natives!", e);
			return;
		}

		File gameDirectory = Launcher.getInstance().getBaseDirectory();
		Launcher.getInstance().println("Launching in " + gameDirectory);

		if (!gameDirectory.exists()) {
			if (!gameDirectory.mkdirs()) {
				Launcher.getInstance().println("Aborting launch; couldn't create game directory");
			}
		} else if (!gameDirectory.isDirectory()) {
			Launcher.getInstance().println("Aborting launch; game directory is not actually a directory");
			return;
		}

		JavaProcessLauncher processLauncher = new JavaProcessLauncher(null, new String[0]);
		processLauncher.directory(gameDirectory);

		if (OperatingSystem.getCurrentPlatform().equals(OperatingSystem.OSX)) {
			processLauncher.addCommands(new String[] { "-Xdock:icon=" + new File(assetsDir, "icons/minecraft.icns").getAbsolutePath(), "-Xdock:name=Minecraft" });
		}

		boolean is32Bit = "32".equals(System.getProperty("sun.arch.data.model"));
		String defaultArgument = is32Bit ? "-Xmx512M" : "-Xmx1G";
		processLauncher.addSplitCommands(defaultArgument);

		processLauncher.addCommands(new String[] { "-Djava.library.path=" + this.nativeDir.getAbsolutePath() });
		processLauncher.addCommands(new String[] { "-cp", this.constructClassPath(this.version) });
		processLauncher.addCommands(new String[] { this.version.getMainClass() });

		AuthenticationService auth = Launcher.getInstance().getProfileManager().getAuthenticationService();

		String[] args = this.getMinecraftArguments(this.version, auth.getSelectedProfile().getName(), gameDirectory, assetsDir, auth);

		processLauncher.addCommands(args);

		if ((auth == null) || (auth.getSelectedProfile() == null)) {
			processLauncher.addCommands(new String[] { "--demo" });
		}
		try {
			List<String> parts = processLauncher.getFullCommands();
			StringBuilder full = new StringBuilder();
			boolean first = true;

			for (String part : parts) {
				if (!first) {
					full.append(" ");
				}
				full.append(part);
				first = false;
			}

			Launcher.getInstance().println("Running " + full.toString());
			JavaProcess process = processLauncher.start();
			process.safeSetExitRunnable(this);
		}

		catch (IOException e) {
			Launcher.getInstance().println("Couldn't launch game", e);
			return;
		}

		Launcher.instance.getProfileManager().saveProfile();
		LauncherDisplay.instance.terminate();
	}

	private String[] getMinecraftArguments(CompleteVersion version, String player, File gameDirectory, File assetsDirectory, AuthenticationService authentication) {
		if (version.getMinecraftArguments() == null) {
			Launcher.getInstance().println("Can't run version, missing minecraftArguments");
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		StrSubstitutor substitutor = new StrSubstitutor(map);
		String[] split = version.getMinecraftArguments().split(" ");

		map.put("auth_username", authentication.getUsername());
		map.put("auth_session", (authentication.getSessionToken() == null) && (authentication.canPlayOnline()) ? "-" : authentication.getSessionToken());

		if (authentication.getSelectedProfile() != null) {
			map.put("auth_player_name", authentication.getSelectedProfile().getName());
			map.put("auth_uuid", authentication.getSelectedProfile().getId());
		} else {
			map.put("auth_player_name", "Player");
			map.put("auth_uuid", new UUID(0L, 0L).toString());
		}

		map.put("profile_name", player);
		map.put("version_name", version.getMinecraftVersion());

		map.put("game_directory", gameDirectory.getAbsolutePath());
		map.put("game_assets", assetsDirectory.getAbsolutePath());

		for (int i = 0; i < split.length; i++) {
			split[i] = substitutor.replace(split[i]);
		}

		return split;
	}

	private File reconstructAssets() throws IOException {
		File assetsDir = new File(Launcher.instance.getBaseDirectory(), "assets");
		File indexDir = new File(assetsDir, "indexes");
		File objectDir = new File(assetsDir, "objects");
		String assetVersion = "legacy";//this.version.getAssets() == null ? "legacy" : this.version.getAssets();
		File indexFile = new File(indexDir, assetVersion + ".json");
		File virtualRoot = new File(new File(assetsDir, "virtual"), assetVersion);

		if (!virtualRoot.exists()) {
			assetsDir.mkdirs();
		}

		if (!indexFile.isFile()) {
			Launcher.getInstance().println("No assets index file " + virtualRoot + "; can't reconstruct assets");
			return virtualRoot;
		}

		AssetIndex index = this.gson.fromJson(FileUtils.readFileToString(indexFile), AssetIndex.class);

		if (index.isVirtual()) {
			Launcher.getInstance().println("Reconstructing virtual assets folder at " + virtualRoot);
			for (Map.Entry entry : index.getFileMap().entrySet()) {
				File target = new File(virtualRoot, (String) entry.getKey());
				File original = new File(new File(objectDir, ((AssetIndex.AssetObject) entry.getValue()).getHash().substring(0, 2)), ((AssetIndex.AssetObject) entry.getValue()).getHash());

				if (!target.isFile()) {
					target.getParentFile().mkdirs();
					target.createNewFile();
					Files.copy(original.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}

			//FileUtils.writeStringToFile(new File(virtualRoot, ".lastused"), this.dateAdapter.serializeToString(new Date()));
		}

		return virtualRoot;
	}

	private String constructClassPath(CompleteVersion version) {
		StringBuilder result = new StringBuilder();
		Collection<File> classPath = version.getClassPath(OperatingSystem.getCurrentPlatform(), Launcher.getInstance().getBaseDirectory());
		String separator = System.getProperty("path.separator");

		for (File file : classPath) {
			if (!file.isFile()) {
				throw new RuntimeException("Classpath file not found: " + file);
			}
			if (result.length() > 0) {
				result.append(separator);
			}
			result.append(file.getAbsolutePath());
		}

		return result.toString();
	}

	private void unpackNatives(CompleteVersion version, File targetDir) throws IOException {
		OperatingSystem os = OperatingSystem.getCurrentPlatform();
		Collection<Library> libraries = version.getRelevantLibraries();

		for (Library library : libraries) {
			Map<OperatingSystem, String> nativesPerOs = library.getNatives();

			if ((nativesPerOs != null) && (nativesPerOs.get(os) != null)) {
				File file = new File(Launcher.getInstance().getBaseDirectory(), "libraries/" + library.getArtifactPath(nativesPerOs.get(os)));
				ZipFile zip = new ZipFile(file);
				ExtractRules extractRules = library.getExtractRules();
				try {
					Enumeration<? extends ZipEntry> entries = zip.entries();

					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();

						if ((extractRules == null) || (extractRules.shouldExtract(entry.getName()))) {
							File targetFile = new File(targetDir, entry.getName());
							if (targetFile.getParentFile() != null) {
								targetFile.getParentFile().mkdirs();
							}

							if (!entry.isDirectory()) {
								BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));

								byte[] buffer = new byte[2048];
								FileOutputStream outputStream = new FileOutputStream(targetFile);
								BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
								try {
									int length;
									while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
										bufferedOutputStream.write(buffer, 0, length);
									}
								} finally {
									Downloadable.closeSilently(bufferedOutputStream);
									Downloadable.closeSilently(outputStream);
									Downloadable.closeSilently(inputStream);
								}
							}
						}
					}
				} finally {
					zip.close();
				}
			}
		}
	}

	@Override
	public void onDownloadJobProgressChanged(DownloadJob paramDownloadJob) {
		LoadingForm.instance.getProgressbar().setProgress(paramDownloadJob.getProgress());
	}

	@Override
	public void onJavaProcessEnded(JavaProcess paramJavaProcess) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownloadJobFinished(DownloadJob job) {
		LoadingForm.instance.getProgressbar().setProgress(1.0f);

		if (job.getFailures() > 0) {
			Launcher.getInstance().println("Job '" + job.getName() + "' finished with " + job.getFailures() + " failure(s)!");
		} else {
			Launcher.getInstance().println("Job '" + job.getName() + "' finished successfully");

			try {
				this.launchGame();
			} catch (Throwable ex) {
				Launcher.getInstance().println("Fatal error launching game. Report this to http://mojang.atlassian.net please!", ex);
			}
		}
	}

}
