package graphast.exceptions;

public class NodeNotFoundException extends RuntimeException {
	
	public NodeNotFoundException() {
		super();
	}
	
	public NodeNotFoundException(long i) {
		super("Node "+i+" not found");
	}
	
	public NodeNotFoundException(long i, Throwable cause) {
		super("Node "+i+" not found", cause);
	}

	public NodeNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NodeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NodeNotFoundException(String message) {
		super(message);
	}

	public NodeNotFoundException(Throwable cause) {
		super(cause);
	}
}
