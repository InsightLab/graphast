package org.graphast.util;

import org.graphast.model.Node;




public class DistanceUtils {

	/**
	 * Haversine Formula can be found here: http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * This method is going to return the distance between two geographic points.
	 * 
	 * @param latitudeFrom latitude of the From point
	 * @param longitudeFrom longitude of the From point
	 * @param latitudeTo latitude of the To point
	 * @param longitudeTo longitude of the To point
	 * @return the distance in millimeters
	 */
	public static double distanceLatLong(double latitudeFrom, double longitudeFrom,
			double latitudeTo, double longitudeTo) {

		final double r = 6372.8 * 1000000; // Radius of the earth in mm

	    double lat1 = latitudeFrom;
		double lon1 = longitudeFrom;
		double lat2 = latitudeTo;
		double lon2 = longitudeTo;
		
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return r * c;
	    
	}
	/**
	 * Haversine Formula can be found here: http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * This method is going to return the distance between two geographic points.
	 * 
	 * @param fromNode From point
	 * @param toNode To point
	 * @return the distance in millimeters
	 */
	public static long distanceLatLong(Node fromNode, Node toNode) {
		
	    final long r = 6371000 * 1000; // Radius of the earth in mm

	    double lat1 = fromNode.getLatitude();
		double lon1 = fromNode.getLongitude();
		double lat2 = toNode.getLatitude();
		double lon2 = toNode.getLongitude();
		
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (long) (r * c);
	    
	}

	public static double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	
	
	
	public static int timeCost(Node fromNode, Node toNode){
		return (int) ((int) distanceLatLong(fromNode, toNode)/88.51392);
	}
	
	
}
