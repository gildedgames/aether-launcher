package net.aetherteam.aether.launcher.authentication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.utils.StringUtils;

public abstract class BaseAuthenticationService implements AuthenticationService {

	private String username;

	private String password;

	private GameProfile selectedProfile;

	private boolean shouldRememberMe = true;

	private String selectedVersion;

	private boolean isTestVersion;

	@Override
	public boolean canLogIn() {
		return (!this.canPlayOnline()) && (StringUtils.isNotBlank(this.getUsername())) && (StringUtils.isNotBlank(this.getPassword()));
	}

	@Override
	public void logOut() {
		this.password = null;
		this.setSelectedProfile(null);
	}

	@Override
	public boolean isLoggedIn() {
		return this.getSelectedProfile() != null;
	}

	@Override
	public boolean canPlayOnline() {
		return (this.isLoggedIn()) && (this.getSelectedProfile() != null) && (this.getSessionToken() != null);
	}

	@Override
	public void setUsername(String username) {
		if ((this.isLoggedIn()) && (this.canPlayOnline())) {
			throw new IllegalStateException("Cannot change username whilst logged in & online");
		}

		this.username = username;
	}

	@Override
	public void setPassword(String password) {
		if ((this.isLoggedIn()) && (this.canPlayOnline()) && (StringUtils.isNotBlank(password))) {
			throw new IllegalStateException("Cannot set password whilst logged in & online");
		}

		this.password = password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	protected String getPassword() {
		return this.password;
	}

	@Override
	public void loadFromStorage(Map<String, String> credentials) {
		this.logOut();

		if (credentials.containsKey("rememberMe")) {
			this.setRememberMe(Boolean.getBoolean(credentials.get("rememberMe")));
		}

		this.selectedVersion = credentials.get("selectedVersion");

		this.setUsername(credentials.get("username"));
		Launcher.getInstance().setClientToken(UUID.fromString(credentials.get("clientToken")));

		if ((credentials.containsKey("displayName")) && (credentials.containsKey("uuid"))) {
			this.setSelectedProfile(new GameProfile(credentials.get("uuid"), credentials.get("displayName")));
		}
	}

	@Override
	public Map<String, String> saveForStorage() {
		Map<String, String> result = new HashMap<String, String>();

		if (!this.shouldRememberMe()) {
			result.put("rememberMe", Boolean.toString(false));
			return result;
		}

		result.put("selectedVersion", this.selectedVersion);

		if (this.getUsername() != null) {
			result.put("username", this.getUsername());
		}

		result.put("clientToken", Launcher.getInstance().getClientToken().toString());

		if (this.getSelectedProfile() != null) {
			result.put("displayName", this.getSelectedProfile().getName());
			result.put("uuid", this.getSelectedProfile().getId());
		}

		return result;
	}

	public Map<String, String> removeProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldRememberMe() {
		return this.shouldRememberMe;
	}

	@Override
	public void setRememberMe(boolean rememberMe) {
		this.shouldRememberMe = rememberMe;
	}

	protected void setSelectedProfile(GameProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	@Override
	public GameProfile getSelectedProfile() {
		return this.selectedProfile;
	}

	public String getSelectedVersion() {
		return this.selectedVersion;
	}

	public boolean getIsTestVersion() {
		return this.isTestVersion;
	}

	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(this.getClass().getSimpleName());
		result.append("{");

		if (this.isLoggedIn()) {
			result.append("Logged in as ");
			result.append(this.getUsername());

			if (this.getSelectedProfile() != null) {
				result.append(" / ");
				result.append(this.getSelectedProfile());
				result.append(" - ");

				if (this.canPlayOnline()) {
					result.append("Online with session token '");
					result.append(this.getSessionToken());
					result.append("'");
				} else {
					result.append("Offline");
				}
			}
		} else {
			result.append("Not logged in");
		}

		result.append("}");

		return result.toString();
	}

	@Override
	public String guessPasswordFromSillyOldFormat(File file) {
		String[] details = getStoredDetails(file);

		if ((details != null) && (details[0].equals(this.getUsername()))) {
			return details[1];
		}

		return null;
	}

	public static String[] getStoredDetails(File lastLoginFile) {
		if (!lastLoginFile.isFile()) {
			return null;
		}
		try {
			Cipher cipher = getCipher(2, "passwordfile");

			DataInputStream dis;

			if (cipher != null) {
				dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLoginFile), cipher));
			} else {
				dis = new DataInputStream(new FileInputStream(lastLoginFile));
			}

			String username = dis.readUTF();
			String password = dis.readUTF();
			dis.close();

			return new String[]{username, password};
		} catch (Exception e) {
			Launcher.getInstance().println("Couldn't load old lastlogin file", e);
		}
		return null;
	}

	private static Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

}