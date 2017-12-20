package org.insightlab.graphast.exceptions;

/**
 * This class is used to handle exceptions in situations which OSMFormat is not supported.
 *
 */

public class OSMFormatNotSupported extends RuntimeException {
	
	private static final long serialVersionUID = 4610164949252862158L;

	/**
	 * Create a new OSMFormatNotSupported for the given message, cause, sup and writableStackTrace.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 * @param enableSuppression used to choose suppression or not.
	 * @param writableStackTrace that indicates the stack of execution.
	 */
	public OSMFormatNotSupported(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Create a new OSMFormatNotSupported for the given message and cause.
	 * @param message that indicates which errors occurred.
	 * @param cause the reason that raise this exception.
	 */
	public OSMFormatNotSupported(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Create a new OSMFormatNotSupported for the given message.
	 * @param message that indicates which errors occurred.
	 */
	public OSMFormatNotSupported(String message) {
		super(message);
	}

	/**
	 * Create a new OSMFormatNotSupported for the given cause.
	 * @param cause the reason that raise this exception.
	 */
	public OSMFormatNotSupported(Throwable cause) {
		super(cause);
	}
	
}
