package org.graphast.model;

import java.util.HashMap;

public class Bundle {
	
	private HashMap<String, Integer> ints = null;
	private HashMap<String, Double> doubles = null;
	private HashMap<String, Long> longs = null;
	private HashMap<String, Boolean> bools = null;
	private HashMap<String, String> strings = null;
	private HashMap<String, int[]> intArrays = null;
	private HashMap<String, double[]> doubleArrays = null;
	
	public void putInt(String id, int value) {
		if (ints == null) ints = new HashMap<>();
		ints.put(id, value);
	}
	
	public void putDouble(String id, double value) {
		if (doubles == null) doubles = new HashMap<>();
		doubles.put(id, value);
	}
	
	public void putLong(String id, long value) {
		if (longs == null) longs = new HashMap<>();
		longs.put(id, value);
	}
	
	public void putBoolean(String id, boolean value) {
		if (bools == null) bools = new HashMap<>();
		bools.put(id, value);
	}
	
	public void putString(String id, String value) {
		if (strings == null) strings = new HashMap<>();
		strings.put(id, value);
	}
	
	public void putIntArray(String id, int[] value) {
		if (intArrays == null) intArrays = new HashMap<>();
		intArrays.put(id, value);
	}
	
	public void putDoubleArray(String id, double[] value) {
		if (doubleArrays == null) doubleArrays = new HashMap<>();
		doubleArrays.put(id, value);
	}
	
	public int getInt(String id, int other) {
		return ints == null || !ints.containsKey(id) ? other : ints.get(id);
	}
	
	public double getDouble(String id, double other) {
		return doubles == null || !doubles.containsKey(id) ? other : doubles.get(id);
	}
	
	public long getLong(String id, long other) {
		return longs == null || !longs.containsKey(id) ? other : longs.get(id);
	}
	
	public boolean getBoolean(String id, boolean other) {
		return bools == null || !bools.containsKey(id) ? other : bools.get(id);
	}
	
	public String getString(String id, String other) {
		return strings == null || !strings.containsKey(id) ? other : strings.get(id);
	}
	
	public int[] getIntArray(String id) {
		return intArrays == null || !intArrays.containsKey(id) ? null : intArrays.get(id);
	}
	
	public double[] getDoubleArray(String id) {
		return doubleArrays == null || !doubleArrays.containsKey(id) ? null : doubleArrays.get(id);
	}

}
