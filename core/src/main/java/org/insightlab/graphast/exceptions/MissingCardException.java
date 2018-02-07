package org.insightlab.graphast.exceptions;

public class MissingCardException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -915981947597562490L;
	
	public MissingCardException() {
		super();
	}
	
	public MissingCardException(String cardName) {
		super("'" + cardName + "' not found!");
	}

}
