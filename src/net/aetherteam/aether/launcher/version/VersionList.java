package net.aetherteam.aether.launcher.version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.aetherteam.aether.launcher.OperatingSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class VersionList {

	protected final Gson gson;

	protected final Map<String, Version> versionsByName = new HashMap<String, Version>();

	protected final List<Version> versions = new ArrayList<Version>();

	public VersionList() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
		builder.enableComplexMapKeySerialization();
		builder.setPrettyPrinting();
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

		this.gson = builder.create();
	}

	public List<Version> getVersions() {
		return this.versions;
	}

	public Version getVersion(String name) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}

		return this.versionsByName.get(name);
	}

	public CompleteVersion getCompleteVersion(String name) throws IOException {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}

		Version version = this.getVersion(name);

		if (version == null) {
			throw new IllegalArgumentException("Unknown version - cannot get complete version of null");
		}

		return this.getCompleteVersion(version);
	}

	public CompleteVersion getCompleteVersion(Version version) throws IOException {
		if ((version instanceof CompleteVersion)) {
			return (CompleteVersion) version;
		}

		if (version == null) {
			throw new IllegalArgumentException("Version cannot be null");
		}

		CompleteVersion complete = this.gson.fromJson(this.getUrl("versions/" + version.getId() + "/" + version.getId() + ".json"), CompleteVersion.class);

		Collections.replaceAll(this.versions, version, complete);
		this.versionsByName.put(version.getId(), complete);

		return complete;
	}

	protected void clearCache() {
		this.versionsByName.clear();
		this.versions.clear();
	}

	public void refreshVersions() throws IOException {
		this.clearCache();

		RawVersionList versionList = this.gson.fromJson(this.getUrl("versions/versions.json"), RawVersionList.class);

		for (Version version : versionList.getVersions()) {
			this.versions.add(version);
			this.versionsByName.put(version.getId(), version);
		}
	}

	public CompleteVersion addVersion(CompleteVersion version) {
		if (version.getId() == null) {
			throw new IllegalArgumentException("Cannot add blank version");
		}

		if (this.getVersion(version.getId()) != null) {
			throw new IllegalArgumentException("Version '" + version.getId() + "' is already tracked");
		}

		this.versions.add(version);
		this.versionsByName.put(version.getId(), version);

		return version;
	}

	public void removeVersion(String name) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}

		Version version = this.getVersion(name);

		if (version == null) {
			throw new IllegalArgumentException("Unknown version - cannot remove null");
		}

		this.removeVersion(version);
	}

	public void removeVersion(Version version) {
		if (version == null) {
			throw new IllegalArgumentException("Cannot remove null version");
		}

		this.versions.remove(version);
		this.versionsByName.remove(version.getId());
	}

	public String serializeVersionList() {
		RawVersionList list = new RawVersionList();

		for (Version version : this.getVersions()) {
			PartialVersion partial = null;

			if ((version instanceof PartialVersion)) {
				partial = (PartialVersion) version;
			} else {
				partial = new PartialVersion(version);
			}

			list.getVersions().add(partial);
		}

		return this.gson.toJson(list);
	}

	public String serializeVersion(CompleteVersion version) {
		if (version == null) {
			throw new IllegalArgumentException("Cannot serialize null!");
		}
		return this.gson.toJson(version);
	}

	public abstract boolean hasAllFiles(CompleteVersion paramCompleteVersion, OperatingSystem paramOperatingSystem);

	protected abstract String getUrl(String paramString) throws IOException;

	private static class RawVersionList {

		private List<PartialVersion> versions = new ArrayList<PartialVersion>();

		public List<PartialVersion> getVersions() {
			return this.versions;
		}
	}
}
