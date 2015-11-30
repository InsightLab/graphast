package org.graphast.util;

import org.graphast.model.Edge;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;

public class GeoUtils {
	
	final private static double R_MAJOR = 6378137.0;
    final private static double R_MINOR = 6356752.3142;
    
    /**
     * Factor used to convert and round latitude and longitude to/from int.
     */
    public static int LAT_LONG_CONVERTION_FACTOR = 1000000;
	
	
	public GeoUtils() {
	}

	public static int latLongToInt(double number) {
		return (int) NumberUtils.convert(number, LAT_LONG_CONVERTION_FACTOR);
	}
	
	public static double latLongToDouble(int number) {
		return number / (double) LAT_LONG_CONVERTION_FACTOR;
	}
	
	public static String long2XSpherical(String number) {
		try {
			double d = Double.parseDouble(number);
			return String.valueOf(long2XSpherical(d));
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not convert " + number + " to double.", e);
		}
	}

	public static String lat2YSpherical(String number) {
		try {
			double d = Double.parseDouble(number);
			return String.valueOf(lat2YSpherical(d));
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not convert " + number + " to double.", e);
		}
	}
	
	/**
	 * 
	 * @param longitude in degrees
	 * @return X offset from your original position in meters.
	 */
	public static double long2XSpherical(double longitude) {
		return 6378137 * Math.toRadians(longitude);
	}	

	/**
	 * 
	 * @param latitude in degrees
	 * @return Y offset from your original position in meters.
	 */
	public static double lat2YSpherical(double latitude) {
		return 6378137 * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(latitude) / 2.0));
	}
	
	public static double x2LongSpherical(double x) {
	    return Math.toDegrees(x / 6378137.0);
	 }
	
	public static double y2LatSpherical(double y) {
	    return Math.toDegrees(Math.atan(Math.sinh(y / 6378137)));
	}

    public static double long2XElliptical(double lon) {
        return R_MAJOR * Math.toRadians(lon);
    }
 
    public static double lat2YElliptical(double lat) {
        if (lat > 89.5) {
            lat = 89.5;
        }
        if (lat < -89.5) {
            lat = -89.5;
        }
        double temp = R_MINOR / R_MAJOR;
        double es = 1.0 - (temp * temp);
        double eccent = Math.sqrt(es);
        double phi = Math.toRadians(lat);
        double sinphi = Math.sin(phi);
        double con = eccent * sinphi;
        double com = 0.5 * eccent;
        con = Math.pow(((1.0-con)/(1.0+con)), com);
        double ts = Math.tan(0.5 * ((Math.PI*0.5) - phi))/con;
        double y = 0 - R_MAJOR * Math.log(ts);
        return y;
    }

	public static boolean isPointInEdgeLine(GraphBounds graph, Node point, Edge edge) {
		Node start = graph.getNode(edge.getFromNode());
		Node end = graph.getNode(edge.getToNode());
		return DistanceUtils.distanceLatLong(start, point)+DistanceUtils.distanceLatLong(point, end)-DistanceUtils.distanceLatLong(start, end)<=0.1;
	}	

}
