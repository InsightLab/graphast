package org.insightlab.graphast.exceptions;

/**
 * This class is used to handle exceptions in situations which nodes not exists.
 *
 */

public class NodeNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -4697887137944949430L;

	/**
	 * Create a new NodeNotFoundException using no information.
	 */
	public NodeNotFoundException() {
		super();
	}

	/**
	 * Create a new NodeNotFoundException for the given node's id.
	 * @param node's id.
	 */
	public NodeNotFoundException(long i) {
		super("Node "+i+" not found");
	}
	
	/**
	 * Create a new NodeNotFoundException for the given node's id and cause.
	 * @param node's id.
	 * @param cause the reason that raise this exception.
	 */
	public NodeNotFoundException(long i, Throwable cause) {
		super("Node "+i+" not found", cause);
	}

	/**
	 * Create a new NodeNotFoundException for the given message, cause, sup and writableStackTrace.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 * @param enableSuppression used to choose suppression or not.
	 * @param writableStackTrace that indicates the stack of execution.
	 */
	public NodeNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Create a new NodeNotFoundException for the given message and cause.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 */
	public NodeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new NodeNotFoundException for the given message.
	 * @param message that indicates which errors occurred.
	 */
	public NodeNotFoundException(String message) {
		super(message);
	}

	/**
	 * Create a new NodeNotFoundException for the given cause.
	 * @param cause the reason that raise this exception.
	 */
	public NodeNotFoundException(Throwable cause) {
		super(cause);
	}
}
