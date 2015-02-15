package net.aetherteam.aether.launcher.version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.aetherteam.aether.launcher.OperatingSystem;
import net.aetherteam.aether.launcher.download.ChecksummedDownloadable;
import net.aetherteam.aether.launcher.download.CompressedDownloadable;
import net.aetherteam.aether.launcher.download.Downloadable;

public class CompleteVersion implements Version {

	private String id;

	private Date time;

	private Date releaseTime;

	private List<String> changelog;

	private String minecraftVersion;

	private String minecraftArguments;

	private List<Library> libraries;

	private List<Mod> mods;

	private String mainClass;

	private int minimumLauncherVersion;

	private String incompatibilityReason;

	private List<Rule> rules;
	
	private String libraryZipHack;

	private boolean isTestVersion = false;

	public CompleteVersion() {
	}

	public CompleteVersion(String id, Date releaseTime, List<String> changelog, Date updateTime, String mainClass, String minecraftArguments) {
		if ((id == null) || (id.length() == 0)) {
			throw new IllegalArgumentException("ID cannot be null or empty");
		}

		if (releaseTime == null) {
			throw new IllegalArgumentException("Release time cannot be null");
		}

		if (updateTime == null) {
			throw new IllegalArgumentException("Update time cannot be null");
		}

		if ((mainClass == null) || (mainClass.length() == 0)) {
			throw new IllegalArgumentException("Main class cannot be null or empty");
		}

		if (minecraftArguments == null) {
			throw new IllegalArgumentException("Process arguments cannot be null or empty");
		}

		this.id = id;
		this.releaseTime = releaseTime;
		this.changelog = changelog;
		this.time = updateTime;
		this.mainClass = mainClass;
		this.libraries = new ArrayList<Library>();
		this.mods = new ArrayList<Mod>();
		this.minecraftArguments = minecraftArguments;
	}

	public CompleteVersion(CompleteVersion version) {
		this(version.getId(), version.getReleaseTime(), version.getChangelog(), version.getUpdatedTime(), version.getMainClass(), version.getMinecraftArguments());
	}

	public CompleteVersion(Version version, String mainClass, String minecraftArguments) {
		this(version.getId(), version.getReleaseTime(), version.getChangelog(), version.getUpdatedTime(), mainClass, minecraftArguments);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Date getUpdatedTime() {
		return this.time;
	}

	@Override
	public Date getReleaseTime() {
		return this.releaseTime;
	}

	@Override
	public List<String> getChangelog() {
		return this.changelog;
	}

	public String getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public Collection<Library> getLibraries() {
		return this.libraries;
	}

	public Collection<Mod> getMods() {
		return this.mods;
	}

	public String getMainClass() {
		return this.mainClass;
	}

	@Override
	public void setUpdatedTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("Time cannot be null");
		}

		this.time = time;
	}

	@Override
	public void setReleaseTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("Time cannot be null");
		}

		this.releaseTime = time;
	}

	public void setMainClass(String mainClass) {
		if ((mainClass == null) || (mainClass.length() == 0)) {
			throw new IllegalArgumentException("Main class cannot be null or empty");
		}

		this.mainClass = mainClass;
	}

	public Collection<Library> getRelevantLibraries() {
		List<Library> result = new ArrayList<Library>();

		for (Library library : this.libraries) {
			if (library.appliesToCurrentEnvironment()) {
				result.add(library);
			}
		}

		return result;
	}

	public Collection<File> getClassPath(OperatingSystem os, File base) {
		Collection<Library> libraries = this.getRelevantLibraries();
		Collection<File> result = new ArrayList<File>();

		for (Library library : libraries) {
			if (library.getNatives() == null) {
				result.add(new File(base, "libraries/" + library.getArtifactPath()));
			}
		}

		result.add(new File(base, "versions/" + this.getMinecraftVersion() + "/" + this.getMinecraftVersion() + ".jar"));

		return result;
	}

	public Collection<String> getExtractFiles(OperatingSystem os) {
		Collection<Library> libraries = this.getRelevantLibraries();
		Collection<String> result = new ArrayList<String>();

		for (Library library : libraries) {
			Map<OperatingSystem, String> natives = library.getNatives();

			if ((natives != null) && (natives.containsKey(os))) {
				result.add("libraries/" + library.getArtifactPath(natives.get(os)));
			}
		}

		return result;
	}

	public Set<String> getRequiredFiles(OperatingSystem os) {
		Set<String> neededFiles = new HashSet<String>();

		for (Library library : this.getRelevantLibraries()) {
			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);

				if (natives != null) {
					neededFiles.add("libraries/" + library.getArtifactPath(natives));
				}
			} else {
				neededFiles.add("libraries/" + library.getArtifactPath());
			}

		}

		for (Mod mod : this.getMods()) {
			neededFiles.add(mod.getPath());
		}

		return neededFiles;
	}

	public Set<Downloadable> getRequiredDownloadables(OperatingSystem os, Proxy proxy, File targetDirectory, boolean ignoreLocalFiles) throws MalformedURLException {
		Set<Downloadable> neededFiles = new HashSet<Downloadable>();

		for (Library library : this.getRelevantLibraries()) {
			String file = null;

			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);
				if (natives != null) {
					file = library.getArtifactPath(natives);
				}
			} else {
				file = library.getArtifactPath();
			}

			if (file != null) {
				URL url = new URL(library.getDownloadUrl() + file);
				File local = new File(targetDirectory, "libraries/" + file);

				if ((!local.isFile()) || (!library.hasCustomUrl())) {
					if (library.packFormat.equals(".jar.pack.xz"))
					{
						neededFiles.add(new CompressedDownloadable(proxy, url, local, ignoreLocalFiles));
					}
					else
					{
						neededFiles.add(new ChecksummedDownloadable(proxy, url, local, ignoreLocalFiles));
					}
				}
			}
		}

		for (Mod mod : this.getMods()) {
			URL url = new URL(mod.getUrl());
			File local = new File(targetDirectory, mod.getVersionPath(this));

			if ((!local.isFile())) {
				neededFiles.add(new ChecksummedDownloadable(proxy, url, local, ignoreLocalFiles));
			}
		}

		return neededFiles;
	}

	@Override
	public String toString() {
		return "CompleteVersion{id='" + this.id + '\'' + ", time=" + this.time + ", libraries=" + this.libraries + ", mainClass='" + this.mainClass + '\'' + ", minimumLauncherVersion=" + this.minimumLauncherVersion + '}';
	}

	public String getMinecraftArguments() {
		return this.minecraftArguments;
	}

	public void setMinecraftArguments(String minecraftArguments) {
		if (minecraftArguments == null) {
			throw new IllegalArgumentException("Process arguments cannot be null or empty");
		}

		this.minecraftArguments = minecraftArguments;
	}

	public int getMinimumLauncherVersion() {
		return this.minimumLauncherVersion;
	}

	public void setMinimumLauncherVersion(int minimumLauncherVersion) {
		this.minimumLauncherVersion = minimumLauncherVersion;
	}

	public boolean appliesToCurrentEnvironment() {
		if (this.rules == null) {
			return true;
		}

		Rule.Action lastAction = Rule.Action.DISALLOW;

		for (Rule rule : this.rules) {
			Rule.Action action = rule.getAppliedAction();

			if (action != null) {
				lastAction = action;
			}
		}

		return lastAction == Rule.Action.ALLOW;
	}

	public String getIncompatibilityReason() {
		return this.incompatibilityReason;
	}

	@Override
	public boolean isTestVersion() {
		return this.isTestVersion;
	}

	@Override
	public void setIsTestVersion(boolean b) {
		this.isTestVersion = b;
	}
}