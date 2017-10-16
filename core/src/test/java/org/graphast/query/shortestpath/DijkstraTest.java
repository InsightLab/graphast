package org.graphast.query.shortestpath;

import static org.junit.Assert.*;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.utils.DistanceVector;
import org.junit.Before;
import org.junit.Test;

public class DijkstraTest {
	
	private Graph g;

	@Before
	public void setUp() throws Exception {
		g = new Graph();
		Node n0 = new Node(0);
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Edge e0 = new Edge(n0, n1, 3, true);
		Edge e1 = new Edge(n1, n2, 3, true);
		Edge e2 = new Edge(n2, n3, 3, true);
		Edge e3 = new Edge(n0, n4, 5, true);
		Edge e4 = new Edge(n4, n3, 5, true);
		Edge e5 = new Edge(n0, n5, 15, true);
		g.addNodes(n0, n1, n2, n3, n4, n5);
		g.addEdges(e0, e1, e2, e3, e4, e5);
	}

	@Test
	public void testOneToAll() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(g.getNode(0));
		assertEquals("One to All distance test n3", 9, vector.getDistance(g.getNode(3)), 0);
		assertEquals("One to All distance test n2", 6, vector.getDistance(g.getNode(2)), 0);
		assertEquals("One to All distance test n4", 5, vector.getDistance(g.getNode(4)), 0);
		assertEquals("One to All Parent test n3", g.getNode(2), vector.getElement(g.getNode(3)).getParent());
		assertEquals("One to All Parent test n2", g.getNode(1), vector.getElement(g.getNode(2)).getParent());
		assertEquals("One to All Parent test n4", g.getNode(0), vector.getElement(g.getNode(4)).getParent());
	}

	@Test
	public void testOneToOne() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(g.getNode(3), g.getNode(1));
		assertEquals("One to One distance test n2", 6, vector.getDistance(g.getNode(1)), 0);
		assertEquals("One to One Parent test n5", null, vector.getElement(g.getNode(5)).getParent());
		assertEquals("One to One Parent test n1", g.getNode(2), vector.getElement(g.getNode(1)).getParent());
	}

}
