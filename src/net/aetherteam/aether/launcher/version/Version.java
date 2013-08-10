package net.aetherteam.aether.launcher.version;

import java.util.Date;

public interface Version {

	public abstract String getId();

	public abstract Date getUpdatedTime();

	public abstract void setUpdatedTime(Date paramDate);

	public abstract Date getReleaseTime();

	public abstract void setReleaseTime(Date paramDate);

}
