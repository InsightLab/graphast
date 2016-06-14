package org.graphast.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class MapUtils {

	public static boolean equalMaps(Map<Object, Object> m1, Map<Object, Object> m2) {
		if (m1.size() != m2.size())
			return false;
		for (Object key : m1.keySet())
			if (!m1.get(key).equals(m2.get(key)))
				return false;
		return true;
	}

	public static boolean equalMaps(Map<Long, Integer> m1, Long2IntMap m2) {
		if (m1.size() != m2.size())
			return false;
		for (Long key : m1.keySet()) {

			int s1 = m1.get(key);
			int s2 = m2.get(key);

			if (s1 != s2) {
				return false;
			}
		}
		return true;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
