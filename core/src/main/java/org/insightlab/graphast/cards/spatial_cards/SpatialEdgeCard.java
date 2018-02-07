package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.cards.EdgeCard;

public class SpatialEdgeCard implements EdgeCard {
	
	private Geometry geometry;
	
	public SpatialEdgeCard(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

}
