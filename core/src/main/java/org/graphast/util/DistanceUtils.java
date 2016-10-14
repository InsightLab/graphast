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

	    final int R = 6371 * 1000000; // Radius of the earth (converted by millimeters)

	    Double phi1 = deg2rad(latitudeFrom);
	    Double phi2 = deg2rad(latitudeTo);
	    
	    Double deltaPhi = deg2rad(latitudeTo - latitudeFrom);
	    Double deltaLambda = deg2rad(longitudeTo - longitudeFrom);
	    
	    Double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
	    		Math.cos(phi1) * Math.cos(phi2) *
	    		Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    return R * c;
	    
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
	public static double distanceLatLong(Node fromNode, Node toNode) {
		
	    final int R = 6371 * 1000000; // Radius of the earth (converted by millimeters)

	    Double phi1 = deg2rad(fromNode.getLatitude());
	    Double phi2 = deg2rad(toNode.getLatitude());
	    
	    Double deltaPhi = deg2rad(toNode.getLatitude() - fromNode.getLatitude());
	    Double deltaLambda = deg2rad(toNode.getLongitude() - fromNode.getLongitude());
	    
	    Double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
	    		Math.cos(phi1) * Math.cos(phi2) *
	    		Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    return R * c;
	    
	}

	public static double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	
	
	
	public static int timeCost(Node fromNode, Node toNode){
		return (int) ((int) distanceLatLong(fromNode, toNode)/88.51392);
	}
	
	
}
