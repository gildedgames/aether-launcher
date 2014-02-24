package net.aetherteam.aether.launcher.authentication.exceptions;

public class InvalidCredentialsException extends AuthenticationException {

	private static final long serialVersionUID = -4214843657099356400L;

	public InvalidCredentialsException() {
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}
}