package org.insightlab.graphast.model.components.spatial_components;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;

public class SpatialComponentController {
	
	private static SpatialComponentController instance = null;
	
	private SpatialComponentController() {}
	
	public static SpatialComponentController getInstance() {
		if (instance == null)
			instance = new SpatialComponentController();
		return instance;
	}
	
	public SpatialGraphComponent getComponent(Graph g) {
		return (SpatialGraphComponent) g.getComponent(SpatialGraphComponent.class);
	}
	
	public SpatialNodeComponent getComponent(Node n) {
		return (SpatialNodeComponent) n.getComponent(SpatialNodeComponent.class);
	}
	
	public SpatialEdgeComponent getComponent(Edge e) {
		return (SpatialEdgeComponent) e.getComponent(SpatialEdgeComponent.class);
	}
	
	public void setCard(Graph g) {
		g.setComponent(new SpatialGraphComponent(g));
	}
	
	public void setCard(Node n, Point p) {
		n.setComponent(new SpatialNodeComponent(n, p));
	}
	
	public void setCard(Edge e, Geometry geometry) {
		e.setComponent(new SpatialEdgeComponent(e, geometry));
	}
	
}
