package org.insightlab.graphast.exceptions;

public class DuplicatedNodeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6087521298798774395L;

	public DuplicatedNodeException() {
		super();
	}
	
	public DuplicatedNodeException(long i) {
		super("Node "+i+" already exists");
	}
	
	public DuplicatedNodeException(long i, Throwable cause) {
		super("Node "+i+" already exists", cause);
	}

	public DuplicatedNodeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicatedNodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicatedNodeException(String message) {
		super(message);
	}

	public DuplicatedNodeException(Throwable cause) {
		super(cause);
	}
}
