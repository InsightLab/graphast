package org.graphast.util;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Map;

public class MapUtils {

	public static boolean equalMaps(Map<Object, Object> m1, Map<Object, Object> m2) {
		if (m1.size() != m2.size())
			return false;
		for (Object key: m1.keySet())
			if (!m1.get(key).equals(m2.get(key)))
				return false;
		return true;
	}
	
	public static boolean equalMaps(Map<Long, Integer> m1, Long2IntMap m2) {
		if (m1.size() != m2.size())
			return false;
		for (Long key: m1.keySet()){
			
			int s1 = m1.get(key);
			int s2 = m2.get(key);
			
			if (s1 != s2){
				return false;
			}
		}
		return true;
	}

}
