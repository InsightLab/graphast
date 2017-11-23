package br.ufc.insightlab.graphast.exceptions;

public class OSMFormatNotSupported extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4610164949252862158L;


	public OSMFormatNotSupported(String message) {
		super(message);
	}

	public OSMFormatNotSupported(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OSMFormatNotSupported(String message, Throwable cause) {
		super(message, cause);
	}


	public OSMFormatNotSupported(Throwable cause) {
		super(cause);
	}
}
