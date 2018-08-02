/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

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
	 * @param id node's id.
	 */
	public NodeNotFoundException(long id) {
		super("Node " + id + " not found");
	}
	
	/**
	 * Create a new NodeNotFoundException for the given node's id and cause.
	 * @param id node's id.
	 * @param cause the reason that raise this exception.
	 */
	public NodeNotFoundException(long id, Throwable cause) {
		super("Node " + id + " not found", cause);
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
