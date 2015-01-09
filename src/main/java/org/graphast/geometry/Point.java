package org.graphast.geometry;


public class Point extends Geometry{

	private double latitude;
	private double longitude;

	public Point(Integer id, double latitude, double longitude){
		super(id);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Point(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		String s = "";
		s += "lat:" + this.latitude + " long:" + this.longitude;
		return s;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
