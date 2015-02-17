package net.aetherteam.aether.launcher.version;

import java.util.Date;
import java.util.List;

public class PartialVersion implements Version {
	private int updateId;
	
	private String id;

	private Date time;

	private Date releaseTime;

	private List<String> changelog;

	private boolean isTestVersion = false;

	public PartialVersion() {
	}

	public PartialVersion(String id, Date releaseTime, Date updateTime) {
		if ((id == null) || (id.length() == 0)) {
			throw new IllegalArgumentException("ID cannot be null or empty");
		}
		if (releaseTime == null) {
			throw new IllegalArgumentException("Release time cannot be null");
		}
		if (updateTime == null) {
			throw new IllegalArgumentException("Update time cannot be null");
		}
		this.id = id;
		this.releaseTime = releaseTime;
		this.time = updateTime;
	}

	public PartialVersion(Version version) {
		this(version.getId(), version.getReleaseTime(), version.getUpdatedTime());
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
	public void setUpdatedTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("Time cannot be null");
		}
		this.time = time;
	}

	@Override
	public Date getReleaseTime() {
		return this.releaseTime;
	}

	@Override
	public void setReleaseTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("Time cannot be null");
		}
		this.releaseTime = time;
	}

	@Override
	public List<String> getChangelog() {
		return this.changelog;
	}

	@Override
	public String toString() {
		return "PartialVersion{id='" + this.id + '\'' + ", updateTime=" + this.time + ", releaseTime=" + this.releaseTime + '}';
	}

	@Override
	public boolean isTestVersion() {
		return isTestVersion;
	}

	@Override
	public void setIsTestVersion(boolean b) {
		this.isTestVersion = b;

	}

	@Override
	public int getUpdateId() {
		return this.updateId;
	}
}
