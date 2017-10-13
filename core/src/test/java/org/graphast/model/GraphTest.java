package org.graphast.model;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class GraphTest {

	private Graph g;
	private Node n0, n1, n2, n3, n4;
	private Edge e0, e1, e2, e3, e4, e5, e6;

	@Before
	public void setUp() throws Exception {
		g = new Graph();
		n0 = new Node(0);
		n1 = new Node(1);
		n2 = new Node(2);
		n3 = new Node(3);
		n4 = new Node(4);
		e0 = new Edge(n1, n4, true);
		e1 = new Edge(n2, n4);
		e2 = new Edge(n0, n1);
		e3 = new Edge(n3, n1, true);
		e4 = new Edge(n2, n3);
		e5 = new Edge(n4, n3);
		e6 = new Edge(n3, n0, true);
	}

	@Test
	public void testAddNode() {
		g.addNode(n0);
		Iterator<Node> it = g.nodeIterator();
		assertEquals("Add node n0", n0, it.next());
		g.addNode(n1);
		g.addNode(n3);
		it = g.nodeIterator();
		it.next();
		assertEquals("Add node n1", n1, it.next());
		assertEquals("Add node n3", n3, it.next());
	}

	@Test
	public void testAddNodes() {
		g.addNodes(n0, n1, n3);
		Iterator<Node> it = g.nodeIterator();
		assertEquals("Add nodes n0", n0, it.next());
		assertEquals("Add nodes n1", n1, it.next());
		assertEquals("Add nodes n3", n3, it.next());
	}

	@Test
	public void testAddEdge() {
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdge(e0);
		Iterator<Edge> it = g.edgeIterator();
		assertEquals("Add edge e0", e0, it.next());
		g.addEdge(e2);
		g.addEdge(e5);
		it = g.edgeIterator();
		it.next();
		assertEquals("Add edge e2", e2, it.next());
		assertEquals("Add edge e5", e5, it.next());
	}

	@Test
	public void testAddEdges() {
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		Iterator<Edge> it = g.edgeIterator();
		assertEquals("Add edges e0", e0, it.next());
		assertEquals("Add edges e2", e2, it.next());
		assertEquals("Add edges e4", e4, it.next());
		assertEquals("Add edges e1", e1, it.next());
		assertEquals("Add edges e6", e6, it.next());
		assertEquals("Add edges e5", e5, it.next());
		assertEquals("Add edges e3", e3, it.next());
	}

	@Test
	public void testGetNode() {
		g.addNodes(n0, n1, n2, n3, n4);
		assertEquals("Get Node n1", n1, g.getNode(n1.getId()));
		assertEquals("Get Node n3", n3, g.getNode(n3.getId()));
	}

}
