package org.insightlab.graphast.cards.spatial_cards;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;

public class SpatialCardController {
	
	private static String cardName = "SpatialCard";
	
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
		g.setCard(cardName, new SpatialGraphCard());
	}
	
	public static void setCard(Node n, int lat, int lng) {
		n.setCard(cardName, new SpatialNodeCard(new Point(lat, lng)));
	}
	
	public static void setCard(Edge e, Geometry geometry) {
		e.setCard(cardName, new SpatialEdgeCard(geometry));
	}
	
}
