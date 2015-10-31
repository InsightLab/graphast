package org.graphast.geometry;

public class PoI extends Point {

	private Integer categoryId;
	private String label;
	
	public PoI(Integer categoryId, String label, double latitude, double longitude) {
		super(latitude, longitude);
		this.categoryId = categoryId;
		this.label = label;
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
	
	@Override
	public String toString() {
		return super.toString() + " categoryId:" + categoryId + " label:" + label;
	}
	
}