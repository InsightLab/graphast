package org.graphast.util;

import org.graphast.model.GraphastNode;



public class DistanceUtils {

	public static double distanceLatLong(double latitudeFrom, double longitudeFrom,
			double latitudeTo, double longitudeTo) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = deg2rad(latitudeTo - latitudeFrom);
	    Double lonDistance = deg2rad(longitudeTo - longitudeFrom);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(deg2rad(latitudeFrom)) * Math.cos(deg2rad(latitudeTo))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    
	    distance = Math.pow(distance, 2);
	    return Math.sqrt(distance);
	}

	public static double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	
	
	
}
