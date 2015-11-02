package org.graphast.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils {

	public static String append(List<?> list) {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static String append(Object ... objects) {
		return append(null, objects);
	}
	
	public static String append(String delimiter, Object ... objects) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < objects.length; i++) {
			sb.append(objects[i]);
			if (delimiter != null && i < objects.length - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}
	
	public static double[] splitDouble(String delimiter, String str) {
		StringTokenizer st = new StringTokenizer(str, delimiter);
		double[] d = new double[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			d[i++] = Double.parseDouble(st.nextToken());
		}
		return d;
	}

	public static List<Integer> splitIntToList(String delimiter, String str) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiter);
		List<Integer> result = new ArrayList<>(st.countTokens());
		while (st.hasMoreTokens()) {
			result.add(Integer.valueOf(st.nextToken()));
		}
		return result;
	}

	
	public static int[] splitInt(String delimiter, String str) {
		StringTokenizer st = new StringTokenizer(str, delimiter);
		int[] d = new int[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			d[i++] = Integer.parseInt(st.nextToken());
		}
		return d;
	}
	
	public static String[] stringToObjectArray(String str){
		String[] st = str.split("\\s*\\[\\s*\"|\"\\s*,\\s*\"|\"\\s*\\]\\s*");
		String[] target = Arrays.copyOfRange(st, 1, st.length);
		return target;
	}
	
	public static String ArrayToString(Object[] ls){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < ls.length; i++){
			sb.append("\"");
			sb.append(ls[i]);
			sb.append("\"");
			if(i < ls.length - 1){
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
