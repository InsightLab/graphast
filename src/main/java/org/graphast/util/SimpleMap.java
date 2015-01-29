package org.graphast.util;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleMap implements Map<Object, Object>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<Object, Object> realMap;

	
	public SimpleMap(Object... entries) {
		putValues(entries);
	}
	
	public SimpleMap putValues(Object... entries) {
		if (entries.length % 2 != 0)
			throw new IllegalArgumentException("Espera-se número par de objetos: " + entries.length);
		
		if (this.realMap == null)
			this.realMap = new HashMap<Object, Object>(entries.length / 2);
		
		for (int i = 0; i < entries.length; i += 2) {
			Object key   = entries[i];
			
			Object value = (i + 1 < entries.length ? entries[i + 1] : null);
			realMap.put((Object)key, (Object)value);
		}
		
		return this;
	}
	
	public void clear() {
		realMap.clear();		
	}

	public boolean containsKey(Object key) {
		return realMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return realMap.containsValue(value);
	}

	public Set<Entry<Object, Object>> entrySet() {
		return realMap.entrySet();
	}

	public Object get(Object key) {
		return realMap.get(key);
	}

	public boolean isEmpty() {
		return realMap.isEmpty();
	}

	public Set<Object> keySet() {
		return realMap.keySet();
	}

	public Object put(Object key, Object value) {
		return realMap.put(key, value);
	}

	public void putAll(Map<? extends Object, ? extends Object> t) {
		realMap.putAll(t);
	}

	public Object remove(Object key) {
		return realMap.remove(key);
	}

	public int size() {
		return realMap.size();
	}

	public Collection<Object> values() {
		return realMap.values();
	}

	@Override
	public String toString() {
		return realMap.toString();
	}
}