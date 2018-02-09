package org.insightlab.graphast.model.cards.spatial_cards;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.cards.EdgeComponent;

public class SpatialEdgeComponent extends EdgeComponent {
	
	private Geometry geometry;
	
	public SpatialEdgeComponent(Edge e) {
		this(e, new Geometry());
	}
	
	public SpatialEdgeComponent(Edge e, Geometry geometry) {
		this.setEdge(e);
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

}
