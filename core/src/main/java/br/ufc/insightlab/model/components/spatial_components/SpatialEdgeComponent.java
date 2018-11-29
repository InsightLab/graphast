package br.ufc.insightlab.model.components.spatial_components;

import br.ufc.insightlab.model.components.EdgeComponent;

public class SpatialEdgeComponent extends EdgeComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5946241963056545943L;
	
	private Geometry geometry;
	private String label;
	
	public SpatialEdgeComponent() {
		this(new Geometry());
	}
	
	public SpatialEdgeComponent(String label) {
		this(new Geometry(), label);
	}
	
	public SpatialEdgeComponent(Geometry geometry) {
		this(geometry, "");
	}
	
	public SpatialEdgeComponent(Geometry geometry, String label) {
		this.geometry = geometry;
		this.label = label;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

}
