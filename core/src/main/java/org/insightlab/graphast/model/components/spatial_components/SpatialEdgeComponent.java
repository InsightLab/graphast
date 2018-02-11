package org.insightlab.graphast.model.components.spatial_components;

import org.insightlab.graphast.model.components.EdgeComponent;

public class SpatialEdgeComponent extends EdgeComponent {
	
	private Geometry geometry;
	public SpatialEdgeComponent(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

}
