package net.aetherteam.aether.launcher.authentication.yggdrasil;

import net.aetherteam.aether.launcher.authentication.GameProfile;

@SuppressWarnings("unused")
public class RefreshRequest {

	private String clientToken;

	private String accessToken;

	private GameProfile selectedProfile;

	public RefreshRequest(YggdrasilAuthenticationService authenticationService) {
		this(authenticationService, null);
	}

	public RefreshRequest(YggdrasilAuthenticationService authenticationService, GameProfile profile) {
		this.clientToken = authenticationService.getClientToken();
		this.accessToken = authenticationService.getAccessToken();
		this.selectedProfile = profile;
	}
}