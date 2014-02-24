package net.aetherteam.aether.launcher.process;

public class MinecraftProcessArguments {

	public static final String LEGACY = " ${auth_player_name} ${auth_session}";

	public static final String USERNAME_SESSION = "--username ${auth_player_name} --session ${auth_session}";

	public static final String USERNAME_SESSION_VERSION = "--username ${auth_player_name} --session ${auth_session} --version ${profile_name}";
}