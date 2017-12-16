package org.insightlab.graphast.model;

import java.util.HashMap;

/**
 * The abstract class GraphObject. The basic models of graph elements 
 * in the project extend from this class.
 */
public abstract class GraphObject {
	
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
		if(data == null) data = new HashMap<>();
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
