package org.graphast.geometry;

public class PoI extends Point {

	private PoICategory poiCategory;
	private String label;
	
	public PoI() {}
	
	public PoI(String label, double latitude, double longitude, PoICategory poiCategory) {
		super(latitude, longitude);
		this.label = label;
		this.poiCategory = poiCategory;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public PoICategory getPoiCategory() {
		return poiCategory;
	}

	public void setPoiCategory(PoICategory poiCategory) {
		this.poiCategory = poiCategory;
	}
	
	@Override
	public String toString() {
		return super.toString() + " label:" + label + " poiCategory:" + poiCategory;
	}
	
}