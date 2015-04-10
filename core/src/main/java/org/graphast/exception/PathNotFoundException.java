package org.graphast.exception;

public class PathNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PathNotFoundException() {
		super();
	}

	public PathNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PathNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PathNotFoundException(String message) {
		super(message);
	}

	public PathNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
