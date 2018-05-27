package org.insightlab.graphast.exceptions;

public class DuplicatedEdgeException extends RuntimeException {

    /**
     * Create a new DuplicatedNodeException using no information.
     */
    public DuplicatedEdgeException() {
        super();
    }

    /**
     * Create a new DuplicatedNodeException for the given id.
     * @param id the duplicate node's id
     */
    public DuplicatedEdgeException(long id) {
        super("Edge " + id + " already exists");
    }

    /**
     * Create a new DuplicatedEdgeException for the given id and cause.
     * @param id the duplicate node's id
     * @param cause the reason that raise this exception
     */
    public DuplicatedEdgeException(long id, Throwable cause) {
        super("Edge " + id + " already exists", cause);
    }

    /**
     * Create a new DuplicatedEdgeException for the given message, cause, sup and writableStackTrace.
     * @param message that indicates which errors occurred.
     * @param cause the reason that raise this exception.
     * @param enableSuppression used to choose suppression or not.
     * @param writableStackTrace that indicates the stack of execution.
     */
    public DuplicatedEdgeException(String message, Throwable cause,
                                   boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Create a new DuplicatedEdgeException for the given message and cause.
     * @param message that indicates which errors occurred.
     * @param cause the reason that raise this exception.
     */
    public DuplicatedEdgeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new DuplicatedEdgeException for the given message.
     * @param message that indicates which errors occurred.
     */
    public DuplicatedEdgeException(String message) {
        super(message);
    }

    /**
     * Create a new DuplicatedEdgeException for the given cause.
     * @param cause the reason that raise this exception.
     */
    public DuplicatedEdgeException(Throwable cause) {
        super(cause);
    }


}
