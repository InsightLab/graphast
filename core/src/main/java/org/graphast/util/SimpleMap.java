package org.graphast.util;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleMap<K, V> implements Map<K, V>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<K, V> realMap;
	
	public SimpleMap(Object... entries) {
		putValues(entries);
	}
	
	public SimpleMap<K, V> putValues(Object... entries) {
		if (entries.length % 2 != 0) {
			throw new IllegalArgumentException("An even number of objects must be given: " + entries.length);
		}
		
		if (this.realMap == null) {
			this.realMap = new HashMap<K, V>(entries.length / 2);
		}
		
		for (int i = 0; i < entries.length; i += 2) {
			@SuppressWarnings("unchecked")
			K key = (K)entries[i];
			
			@SuppressWarnings("unchecked")
			V value = (V)(i + 1 < entries.length ? entries[i + 1] : null);
			realMap.put(key, value);
		}
		
		return this;
	}
	
	@Override
	public void clear() {
		realMap.clear();		
	}

	@Override
	public boolean containsKey(Object key) {
		return realMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return realMap.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return realMap.entrySet();
	}

	@Override
	public V get(Object key) {
		return realMap.get(key);
	}

	@Override
	public boolean isEmpty() {
		return realMap.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return realMap.keySet();
	}

	@Override
	public V put(K key, V value) {
		return realMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> t) {
		realMap.putAll(t);
	}

	@Override
	public V remove(Object key) {
		return realMap.remove(key);
	}

	@Override
	public int size() {
		return realMap.size();
	}

	@Override
	public Collection<V> values() {
		return realMap.values();
	}

	@Override
	public String toString() {
		return realMap.toString();
	}
}