package org.insightlab.graphast.cards.spatial_cards;

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

}
