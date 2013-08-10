package net.aetherteam.aether.launcher.authentication.yggdrasil;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import net.aetherteam.aether.launcher.Launcher;
import net.aetherteam.aether.launcher.authentication.BaseAuthenticationService;
import net.aetherteam.aether.launcher.authentication.GameProfile;
import net.aetherteam.aether.launcher.authentication.exceptions.AuthenticationException;
import net.aetherteam.aether.launcher.authentication.exceptions.InvalidCredentialsException;
import net.aetherteam.aether.launcher.utils.StringUtils;
import net.aetherteam.aether.launcher.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class YggdrasilAuthenticationService extends BaseAuthenticationService {

	private static final URL ROUTE_AUTHENTICATE = Utils.constantURL("https://authserver.mojang.com/authenticate");

	private static final URL ROUTE_REFRESH = Utils.constantURL("https://authserver.mojang.com/refresh");

	private final Gson gson = new Gson();

	private final Agent agent = Agent.MINECRAFT;

	private GameProfile[] profiles;

	private String accessToken;

	private boolean isOnline;

	public boolean canLogIn() {
		return (!this.canPlayOnline()) && (StringUtils.isNotBlank(this.getUsername())) && ((StringUtils.isNotBlank(this.getPassword())) || (StringUtils.isNotBlank(this.getAccessToken())));
	}

	public void logIn() throws AuthenticationException {
		if (StringUtils.isBlank(this.getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}

		if (StringUtils.isNotBlank(this.getAccessToken())) {
			this.logInWithToken();
		} else if (StringUtils.isNotBlank(this.getPassword())) {
			this.logInWithPassword();
		} else {
			throw new InvalidCredentialsException("Invalid password");
		}
	}

	protected void logInWithPassword() throws AuthenticationException {
		if (StringUtils.isBlank(this.getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}
		if (StringUtils.isBlank(this.getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		Launcher.getInstance().println("Logging in with username & password");

		AuthenticationRequest request = new AuthenticationRequest(this, this.getPassword());
		AuthenticationResponse response = (AuthenticationResponse) this.makeRequest(ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);

		if (!response.getClientToken().equals(this.getClientToken())) {
			throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
		}

		this.accessToken = response.getAccessToken();
		this.profiles = response.getAvailableProfiles();
		this.setSelectedProfile(response.getSelectedProfile());
	}

	protected void logInWithToken() throws AuthenticationException {
		if (StringUtils.isBlank(this.getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}
		if (StringUtils.isBlank(this.getAccessToken())) {
			throw new InvalidCredentialsException("Invalid access token");
		}

		Launcher.getInstance().println("Logging in with access token");

		RefreshRequest request = new RefreshRequest(this);
		RefreshResponse response = (RefreshResponse) this.makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);

		if (!response.getClientToken().equals(this.getClientToken())) {
			throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
		}

		this.accessToken = response.getAccessToken();
		this.profiles = response.getAvailableProfiles();
		this.setSelectedProfile(response.getSelectedProfile());
	}

	protected <T extends Response> Response makeRequest(URL url, Object input, Class<T> classOfT) throws AuthenticationException {
		try {
			String jsonResult = Utils.performPost(url, this.gson.toJson(input), Launcher.getInstance().getProxy(), "application/json", true);
			Response result = this.gson.fromJson(jsonResult, classOfT);

			if (result == null) {
				return null;
			}

			if (StringUtils.isNotBlank(result.getError())) {
				if (result.getError().equals("ForbiddenOperationException")) {
					throw new InvalidCredentialsException(result.getErrorMessage());
				}
				throw new AuthenticationException(result.getErrorMessage());
			}

			this.isOnline = true;

			return result;
		} catch (IOException e) {
			throw new AuthenticationException("Cannot contact authentication server", e);
		} catch (IllegalStateException e) {
			throw new AuthenticationException("Cannot contact authentication server", e);
		} catch (JsonParseException e) {
			throw new AuthenticationException("Cannot contact authentication server", e);
		}
	}

	public void logOut() {
		super.logOut();

		this.accessToken = null;
		this.profiles = null;
		this.isOnline = false;
	}

	public GameProfile[] getAvailableProfiles() {
		return this.profiles;
	}

	public boolean isLoggedIn() {
		return StringUtils.isNotBlank(this.accessToken);
	}

	public boolean canPlayOnline() {
		return (this.isLoggedIn()) && (this.getSelectedProfile() != null) && (this.isOnline);
	}

	public void loadFromStorage(Map<String, String> credentials) {
		super.loadFromStorage(credentials);

		this.accessToken = (credentials.get("accessToken"));
	}

	public Map<String, String> saveForStorage() {
		Map<String, String> result = super.saveForStorage();
		if (!this.shouldRememberMe()) {
			return result;
		}

		if (StringUtils.isNotBlank(this.getAccessToken())) {
			result.put("accessToken", this.getAccessToken());
		}

		return result;
	}

	public String getSessionToken() {
		if ((this.isLoggedIn()) && (this.getSelectedProfile() != null) && (this.canPlayOnline())) {
			return String.format("token:%s:%s", new Object[] { this.getAccessToken(), this.getSelectedProfile().getId() });
		}
		return null;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getClientToken() {
		return Launcher.getInstance().getClientToken().toString();
	}

	public Agent getAgent() {
		return this.agent;
	}

	public String toString() {
		return "YggdrasilAuthenticationService{agent=" + this.agent + ", profiles=" + Arrays.toString(this.profiles) + ", selectedProfile=" + this.getSelectedProfile() + ", sessionToken='" + this.getSessionToken() + '\'' + ", username='" + this.getUsername() + '\'' + ", isLoggedIn=" + this.isLoggedIn() + ", canPlayOnline=" + this.canPlayOnline() + ", accessToken='" + this.accessToken + '\'' + ", clientToken='" + this.getClientToken() + '\'' + '}';
	}
}