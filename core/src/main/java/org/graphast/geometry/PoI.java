package org.graphast.geometry;

public class PoI {

	private Integer categoryId;
	private String label;
	private double latitude;
	private double longitude;
	
	public PoI(Integer categoryId, String label, double latitude, double longitude) {
		this.categoryId = categoryId;
		this.label = label;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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
