package net.aetherteam.aether.launcher.authentication.exceptions;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -5719109226649326740L;

	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}