package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.cards.NodeCard;

public class SpatialNodeCard implements NodeCard {
	
	private Point point;
	
	public SpatialNodeCard(Point p) {
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
