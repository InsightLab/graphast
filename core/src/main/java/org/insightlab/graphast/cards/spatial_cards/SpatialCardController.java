package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;

public class SpatialCardController {
	
	private static final String cardName = "SpatialCard";
	
	public static String getCardName() {
		return cardName;
	}
	
	public static SpatialGraphCard getCard(Graph g) {
		return (SpatialGraphCard) g.getCard(cardName);
	}
	
	public static SpatialNodeCard getCard(Node n) {
		return (SpatialNodeCard) n.getCard(cardName);
	}
	
	public static SpatialEdgeCard getCard(Edge e) {
		return (SpatialEdgeCard) e.getCard(cardName);
	}
	
	public static void setCard(Graph g) {
		g.setCard(cardName, new SpatialGraphCard(g));
	}
	
	public static void setCard(Node n, Point p) {
		n.setCard(cardName, new SpatialNodeCard(n, p));
	}
	
	public static void setCard(Edge e, Geometry geometry) {
		e.setCard(cardName, new SpatialEdgeCard(e, geometry));
	}
	
}
