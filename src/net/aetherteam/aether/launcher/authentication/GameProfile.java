package net.aetherteam.aether.launcher.authentication;

public class GameProfile {

	private final String id;

	private final String name;

	public GameProfile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		GameProfile that = (GameProfile) o;

		if (!this.id.equals(that.id)) {
			return false;
		}
		if (!this.name.equals(that.name)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result = this.id.hashCode();
		result = (31 * result) + this.name.hashCode();
		return result;
	}

	public String toString() {
		return "GameProfile{id='" + this.id + '\'' + ", name='" + this.name + '\'' + '}';
	}
}