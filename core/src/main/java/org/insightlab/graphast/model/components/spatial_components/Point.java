package org.insightlab.graphast.model.components.spatial_components;

import java.io.Serializable;

public class Point implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7597535743107576754L;
	
	private double lat;
	private double lng;
	
	public Point() {
		this(0, 0);
	}
	
	public Point(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point))
			return false;
		Point other = (Point) obj;
		return lat == other.getLat() && lng == other.getLng();
	}

}
