package net.aetherteam.aether.launcher;

import java.io.File;
import java.net.Proxy;
import java.util.UUID;

import net.aetherteam.aether.launcher.authentication.exceptions.AuthenticationException;
import net.aetherteam.aether.launcher.authentication.yggdrasil.YggdrasilAuthenticationService;
import net.aetherteam.aether.launcher.version.LocalVersionList;
import net.aetherteam.aether.launcher.version.RemoteVersionList;

public class Launcher {

	static Launcher instance;

	private VersionManager versionManager;

	private GameLauncher gameLauncher = new GameLauncher();

	private ProfileManager profileManager;

	private Proxy proxy = Proxy.NO_PROXY;

	protected File baseDirectory;

	private UUID clientToken = UUID.randomUUID();

	public Launcher() {
		this.baseDirectory = new File("/Users/cafaxo/documents/launcher/");
		this.versionManager = new VersionManager(new LocalVersionList(this.baseDirectory), new RemoteVersionList(this.proxy));

		Launcher.instance = this;

		this.profileManager = new ProfileManager();
		this.profileManager.loadProfile();

		this.refreshVersions();
	}

	public void refreshVersions() {
		this.versionManager.getExecutorService().submit(new Runnable() {

			public void run() {
				try {
					Launcher.this.versionManager.refreshVersions();
				} catch (Throwable e) {
					Launcher.getInstance().println("Unexpected exception refreshing version list", e);
				}
			}
		});
	}

	public void login(String username, String password) {
		YggdrasilAuthenticationService auth = this.profileManager.getAuthenticationService();

		auth.setUsername(username);
		auth.setPassword(password);

		try {
			auth.logIn();
			this.profileManager.saveProfile();
		} catch (AuthenticationException e) {
			this.println("Invalid creditentials");
		}
	}

	public void println(String string) {
		System.out.println(string);
	}

	public void println(String string, Throwable e) {
		System.out.println("exception, " + string);
	}

	public static Launcher getInstance() {
		return instance;
	}

	public File getBaseDirectory() {
		return this.baseDirectory;
	}

	public VersionManager getVersionManager() {
		return this.versionManager;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public UUID getClientToken() {
		return this.clientToken;
	}

	public void setClientToken(UUID clientToken) {
		this.clientToken = clientToken;
	}

	public ProfileManager getProfileManager() {
		return this.profileManager;
	}

	public GameLauncher getGameLauncher() {
		return this.gameLauncher;
	}

}
