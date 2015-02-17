package net.aetherteam.aether.launcher.version;

import java.util.Date;
import java.util.List;

public interface Version {

	public abstract int getUpdateId();
	
	public abstract String getId();

	public abstract Date getUpdatedTime();

	public abstract void setUpdatedTime(Date paramDate);

	public abstract Date getReleaseTime();

	public abstract void setReleaseTime(Date paramDate);

	public abstract List<String> getChangelog();

	public abstract boolean isTestVersion();

	public abstract void setIsTestVersion(boolean b);

}
