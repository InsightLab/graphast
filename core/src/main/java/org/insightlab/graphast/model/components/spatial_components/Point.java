package org.insightlab.graphast.model.components.spatial_components;

public class Point {
	
	private int lat;
	private int lng;
	
	public Point(int lat, int lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public int getLat() {
		return lat;
	}
	
	public int getLng() {
		return lng;
	}
	
	public void setLat(int lat) {
		this.lat = lat;
	}
	
	public void setLng(int lng) {
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
