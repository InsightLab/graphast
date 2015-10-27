package org.graphast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.graphast.exception.GraphastException;

public class DateUtils {

	@SuppressWarnings("deprecation")
	public static int dateToMinutes(Date time){
		int min;
		min = time.getHours() * 60;
		min += time.getMinutes();
		return min;
	}

	@SuppressWarnings("deprecation")
	public static int dateToMilli(Date time){
		if(time== null) {
			return 0;
		} else{
			int min;
			min = time.getHours() * 60 * 60 * 1000;
			min += time.getMinutes() * 60 * 1000;
			min += time.getSeconds() * 1000;
			return min;
		}
	}

	public static int minToMilli(int min){
		return min * 60 * 1000;
	}

	public static Date parseDate(int hour, int minutes, int seconds) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			String dateInString = hour + ":" + minutes + ":" + seconds;
			return sdf.parse(dateInString);
		} catch (ParseException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}
}
