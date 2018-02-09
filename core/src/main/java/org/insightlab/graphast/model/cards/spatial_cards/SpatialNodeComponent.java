package org.insightlab.graphast.model.cards.spatial_cards;

import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.cards.NodeComponent;

public class SpatialNodeComponent extends NodeComponent {
	
	private Point point;
	
	public SpatialNodeComponent(Node n, Point p) {
		this.setNode(n);
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
