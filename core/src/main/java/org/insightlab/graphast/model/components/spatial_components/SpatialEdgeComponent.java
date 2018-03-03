package org.insightlab.graphast.model.components.spatial_components;

import org.insightlab.graphast.model.components.EdgeComponent;

public class SpatialEdgeComponent extends EdgeComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5946241963056545943L;
	
	private Geometry geometry;
	
	public SpatialEdgeComponent() {
		this(new Geometry());
	}
	
	public SpatialEdgeComponent(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

}
