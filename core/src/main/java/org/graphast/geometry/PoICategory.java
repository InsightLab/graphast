package org.graphast.geometry;

import org.graphast.app.AppGraph;

public class PoICategory {
	private Integer id;
	private String label;
	
	public PoICategory() {}
	
	public PoICategory(Integer id) {
		this(id, AppGraph.getAllPoiCategories().get(id));
	}
	
	public PoICategory(Integer id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "PoICategory [id=" + id + ", label=" + label + "]";
	}
	
}
