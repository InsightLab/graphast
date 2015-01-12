package org.graphast.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.graphast.exception.GraphastException;

public class NumberUtils {
	
	public static double convert(double value, int decimal){
		BigDecimal bd = new BigDecimal(value).setScale(decimal, RoundingMode.HALF_UP);
		//System.out.println("original:" + value + " new:" + bd.doubleValue());
		return bd.doubleValue();
	}
	
	public static int convertToInt(Object obj){
		if(obj instanceof Long){
			return (int) ((long) obj);
		}else if(obj instanceof String){
			return Integer.parseInt((String) obj);
		}else{
			throw new GraphastException("Can not convert to int type");
		}
	}
	
//	public static long intsToLong(int a, int b){
//		return (long)a << 32 | b & 0xFFFFFFFFL;
//	}
//	
//	public static int[] longToInts(long id){
//		int aBack = (int)(id >> 32);
//		int bBack = (int)id;
//		return new int[]{aBack,bBack};
//	}
}
