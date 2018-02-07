package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.cards.NodeCard;
import org.insightlab.graphast.model.Node;

public class SpatialNodeCard extends NodeCard {
	
	private Point point;
	
	public SpatialNodeCard(Node n, Point p) {
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
