package org.graphast.model;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {
	
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
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e1, e2, e3, e4, e5, e6);
	}

	@Test
	public void testGetOutEdges() {
		assertThat("Out Edges Test n0", Arrays.asList(e2, e6), containsInAnyOrder(n0.getOutEdges().toArray()));
		assertThat("Out Edges Test n1", Arrays.asList(e0, e3), containsInAnyOrder(n1.getOutEdges().toArray()));
		assertThat("Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(n2.getOutEdges().toArray()));
		assertThat("Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(n3.getOutEdges().toArray()));
		assertThat("Out Edges Test n4", Arrays.asList(e0, e5), containsInAnyOrder(n4.getOutEdges().toArray()));
		
	}

	@Test
	public void testGetInEdges() {
		assertThat("In Edges Test n0", Arrays.asList(e6), containsInAnyOrder(n0.getInEdges().toArray()));
		assertThat("In Edges Test n1", Arrays.asList(e0, e2, e3), containsInAnyOrder(n1.getInEdges().toArray()));
		assertThat("In Edges Test n2", Arrays.asList(), containsInAnyOrder(n2.getInEdges().toArray()));
		assertThat("In Edges Test n3", Arrays.asList(e3, e4, e5, e6), containsInAnyOrder(n3.getInEdges().toArray()));
		assertThat("In Edges Test n4", Arrays.asList(e0, e1), containsInAnyOrder(n4.getInEdges().toArray()));
	}

	@Test
	public void testGetNeighborhood() {
		assertThat("Neighborhood Test n0", Arrays.asList(n1, n3), containsInAnyOrder(n0.getNeighborhood().toArray()));
		assertThat("Neighborhood Test n1", Arrays.asList(n4, n3), containsInAnyOrder(n1.getNeighborhood().toArray()));
		assertThat("Neighborhood Test n2", Arrays.asList(n4, n3), containsInAnyOrder(n2.getNeighborhood().toArray()));
		assertThat("Neighborhood Test n3", Arrays.asList(n1, n0), containsInAnyOrder(n3.getNeighborhood().toArray()));
		assertThat("Neighborhood Test n4", Arrays.asList(n1, n3), containsInAnyOrder(n4.getNeighborhood().toArray()));
	}

}
