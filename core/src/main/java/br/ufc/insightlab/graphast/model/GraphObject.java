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

package br.ufc.insightlab.graphast.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The abstract class GraphObject. The basic models of graph elements 
 * in the project extend from this class.
 */
public abstract class GraphObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5380199647561475338L;
	private HashMap<String, Object> data = null;

	/**
	 * Put data function. Given a key and a value, represented by an object,
	 * it adds a new element into its data HashMap. If its HashMap is null,
	 * this function instantiates it and then adds the new element.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 */
	public void putData(String key, Object value) {
		
		if (data == null)
			data = new HashMap<>();
		
		data.put(key, value);
		
	}
	
	/**
	 * Gets data function. Given a key, it retrieves an object from the data
	 * HashMap and returns it. If the data HashMap does not contain the given key,
	 * null is returned.
	 *
	 * @param key the key whose associate value is to be returned.
	 * @return the value to which the specified key is mapped, or null if 
	 * this map contains no mapping for the key
	 */
	public Object getData(String key) {
		return data.get(key);
	}

}
