package org.insightlab.graphast.model.cards.spatial_cards;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;

public class SpatialComponentController {
	
	private static SpatialComponentController instance = null;
	private static final String cardName = "SpatialCard";
	
	private SpatialComponentController() {}
	
	public static SpatialComponentController getInstance() {
		if (instance == null)
			instance = new SpatialComponentController();
		return instance;
	}
	
	public String getCardName() {
		return cardName;
	}
	
	public SpatialGraphComponent getCard(Graph g) {
		return (SpatialGraphComponent) g.getCard(cardName);
	}
	
	public SpatialNodeComponent getCard(Node n) {
		return (SpatialNodeComponent) n.getCard(cardName);
	}
	
	public SpatialEdgeComponent getCard(Edge e) {
		return (SpatialEdgeComponent) e.getCard(cardName);
	}
	
	public void setCard(Graph g) {
		g.setCard(cardName, new SpatialGraphComponent(g));
	}
	
	public void setCard(Node n, Point p) {
		n.setCard(cardName, new SpatialNodeComponent(n, p));
	}
	
	public void setCard(Edge e, Geometry geometry) {
		e.setCard(cardName, new SpatialEdgeComponent(e, geometry));
	}
	
}
