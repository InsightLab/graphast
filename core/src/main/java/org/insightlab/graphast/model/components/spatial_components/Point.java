package org.insightlab.graphast.model.components.spatial_components;

public class Point {
	
	private double lat;
	private double lng;
	
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
