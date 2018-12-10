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

package br.ufc.insightlab.graphast.exceptions;

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
