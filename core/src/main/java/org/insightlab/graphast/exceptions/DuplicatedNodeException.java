package org.insightlab.graphast.exceptions;

/**
 * This class is used to handle exceptions in situations which duplicate nodes may exists.
 *
 */

public class DuplicatedNodeException extends RuntimeException {
	
	private static final long serialVersionUID = 6087521298798774395L;

	/**
	 * Create a new DuplicatedNodeException using no information.
	 */
	public DuplicatedNodeException() {
		super();
	}
	
	/**
	 * Create a new DuplicatedNodeException for the given id.
	 * @param id the duplicate node's id
	 */
	public DuplicatedNodeException(long id) {
		super("Node " + id + " already exists");
	}
	
	/**
	 * Create a new DuplicatedNodeException for the given id and cause.
	 * @param id the duplicate node's id
	 * @param cause the reason that raise this exception
	 */
	public DuplicatedNodeException(long id, Throwable cause) {
		super("Node " + id + " already exists", cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
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
