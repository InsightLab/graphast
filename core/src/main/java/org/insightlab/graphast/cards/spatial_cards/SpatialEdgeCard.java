package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.cards.EdgeCard;
import org.insightlab.graphast.model.Edge;

public class SpatialEdgeCard extends EdgeCard {
	
	private Geometry geometry;
	
	public SpatialEdgeCard(Edge e) {
		this(e, new Geometry());
	}
	
	public SpatialEdgeCard(Edge e, Geometry geometry) {
		this.setEdge(e);
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

}
