package org.insightlab.graphast.model.components.spatial_components;

import org.insightlab.graphast.model.components.NodeComponent;

public class SpatialNodeComponent extends NodeComponent {
	
	private Point point;
	
	public SpatialNodeComponent(Point p) {
		this.point = p;
	}
	
	public int getLat() {
		return point.getLat();
	}
	
	public int getLng() {
		return point.getLng();
	}
	
	public void setLat(int lat) {
		point.setLat(lat);
	}
	
	public void setLng(int lng) {
		point.setLng(lng);
	}

}
