package org.graphast.geometry;

public class BBox {
	private double minLatitude;
	private double minLongitude;
	private double maxLatitude;
	private double maxLongitude;
	
	public BBox() {}
	
	public BBox(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
		super();
		this.minLatitude = minLatitude;
		this.minLongitude = minLongitude;
		this.maxLatitude = maxLatitude;
		this.maxLongitude = maxLongitude;
	}
	
	public double getMinLatitude() {
		return minLatitude;
	}
	public void setMinLatitude(double minLatitude) {
		this.minLatitude = minLatitude;
	}
	public double getMinLongitude() {
		return minLongitude;
	}
	public void setMinLongitude(double minLongitude) {
		this.minLongitude = minLongitude;
	}
	public double getMaxLatitude() {
		return maxLatitude;
	}
	public void setMaxLatitude(double maxLatitude) {
		this.maxLatitude = maxLatitude;
	}
	public double getMaxLongitude() {
		return maxLongitude;
	}
	public void setMaxLongitude(double maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

}
