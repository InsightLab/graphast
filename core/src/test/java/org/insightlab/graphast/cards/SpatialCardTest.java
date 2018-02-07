package org.insightlab.graphast.cards;

import static org.junit.Assert.*;

import org.insightlab.graphast.cards.spatial_cards.Point;
import org.insightlab.graphast.cards.spatial_cards.SpatialCardController;
//import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.junit.Before;
import org.junit.Test;

public class SpatialCardTest {

	Graph g;
	private Node n0, n1, n2, n3;
//	private Edge e0, e1, e2, e3;
	
	@Before
	public void setUp() throws Exception {
		g = new Graph();
		n0 = new Node(0);
		SpatialCardController.setCard(n0, new Point(12, 4));
		n1 = new Node(1);
		SpatialCardController.setCard(n1, new Point(13, 3));
		n2 = new Node(2);
		SpatialCardController.setCard(n2, new Point(14, 2));
		n3 = new Node(3);
		SpatialCardController.setCard(n3, new Point(15, 1));
//		e0 = new Edge(1, 2, true);
//		e1 = new Edge(2, 3);
//		e2 = new Edge(0, 1);
//		e3 = new Edge(3, 0, true);
	}

	@Test
	public void test() {
		assertEquals("N1 lat test", 13, SpatialCardController.getCard(n1).getLat());
		assertEquals("N3 lng test", 1, SpatialCardController.getCard(n3).getLng());
	}

}
