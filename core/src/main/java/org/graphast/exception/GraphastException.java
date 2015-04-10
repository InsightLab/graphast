package org.graphast.exception;

public class GraphastException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GraphastException() {
		super();
	}

	public GraphastException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GraphastException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraphastException(String message) {
		super(message);
	}

	public GraphastException(Throwable cause) {
		super(cause);
	}
	
}
