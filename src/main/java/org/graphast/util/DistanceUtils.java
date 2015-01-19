package org.graphast.util;

import org.graphast.model.GraphastNode;



public class DistanceUtils {

//	public static double euclidianDistance(GraphastNode v1, GraphastNode v2){
//		double y1 = v1.getProperty(LATITUDE);
//		y1 = GeoUtils.lat2YSpherical(y1);
//		double x1 = v1.getProperty(LONGITUDE);
//		x1 = GeoUtils.long2XSpherical(x1);
//		
//		double y2 = v2.getProperty(LATITUDE);
//		y2 = GeoUtils.lat2YSpherical(y2);
//		double x2 = v2.getProperty(LONGITUDE);
//		x2 = GeoUtils.long2XSpherical(x2);
//		
//		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
//	}
	
	public static double euclidianDistance(double x1, double y1, double x2, double y2){
		y1 = GeoUtils.lat2YSpherical(y1);
		x1 = GeoUtils.long2XSpherical(x1);
		
		y2 = GeoUtils.lat2YSpherical(y2);
		x2 = GeoUtils.long2XSpherical(x2);
		
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
//	public static int timeCost(Vertex v1, Vertex v2){
//		return (int) ((int) euclidianDistance(v1, v2)/88.51392);
//	}
}
