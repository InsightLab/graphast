package org.graphast.util;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;

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
	
	public static boolean equalMaps(Map<Object, Object> m1, Long2ShortMap m2) {
		if (m1.size() != m2.size())
			return false;
		for (Object key: m1.keySet()){
			
			int s1 = (int)m1.get(key);
			Long keyLong = (long)((Integer)key);
			int s2 = m2.get(keyLong);
			
			if (s1 != s2){
				return false;
			}
		}
		return true;
	}

}
