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
	 * Create a new DuplicatedNodeException for the given message, cause, sup and writableStackTrace.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 * @param enableSuppression used to choose suppression or not.
	 * @param writableStackTrace that indicates the stack of execution.
	 */
	public DuplicatedNodeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Create a new DuplicatedNodeException for the given message and cause.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 */
	public DuplicatedNodeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new DuplicatedNodeException for the given message.
	 * @param message that indicates which errors occurred.
	 */
	public DuplicatedNodeException(String message) {
		super(message);
	}

	/**
	 * Create a new DuplicatedNodeException for the given cause.
	 * @param cause the reason that raise this exception.
	 */
	public DuplicatedNodeException(Throwable cause) {
		super(cause);
	}
}
