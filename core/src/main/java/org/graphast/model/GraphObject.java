package org.graphast.model;

import java.util.HashMap;

public abstract class GraphObject {
	
private HashMap<String, Object> data = null;
	
	public void putData(String key, Object value) {
		if(data == null) data = new HashMap<>();
		data.put(key, value);
	}
	
	public Object getData(String key) {
		return data.get(key);
	}

}
