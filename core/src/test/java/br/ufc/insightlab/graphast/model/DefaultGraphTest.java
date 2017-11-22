package br.ufc.insightlab.graphast.model;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterators;

import br.ufc.insightlab.graphast.exceptions.DuplicatedNodeException;
import br.ufc.insightlab.graphast.exceptions.NodeNotFoundException;
import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.model.Node;
import hugedataaccess.DataAccessException;

public class DefaultGraphTest {

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
		e0 = new Edge(1, 4, true);
		e1 = new Edge(2, 4);
		e2 = new Edge(0, 1);
		e3 = new Edge(3, 1, true);
		e4 = new Edge(2, 3);
		e5 = new Edge(4, 3);
		e6 = new Edge(3, 0, true);
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
	public void testGetOutEdges() {
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		assertThat("Out Edges Test n0", Arrays.asList(e2, e6), containsInAnyOrder(Iterators.toArray(g.getOutEdges(0), Edge.class)));
		assertThat("Out Edges Test n1", Arrays.asList(e0, e3), containsInAnyOrder(Iterators.toArray(g.getOutEdges(1), Edge.class)));
		assertThat("Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(Iterators.toArray(g.getOutEdges(2), Edge.class)));
		assertThat("Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(Iterators.toArray(g.getOutEdges(3), Edge.class)));
		assertThat("Out Edges Test n4", Arrays.asList(e0, e5), containsInAnyOrder(Iterators.toArray(g.getOutEdges(4), Edge.class)));
		
	}

	@Test
	public void testGetInEdges() {
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		assertThat("In Edges Test n0", Arrays.asList(e6), containsInAnyOrder(Iterators.toArray(g.getInEdges(0), Edge.class)));
		assertThat("In Edges Test n1", Arrays.asList(e0, e2, e3), containsInAnyOrder(Iterators.toArray(g.getInEdges(1), Edge.class)));
		assertThat("In Edges Test n2", Arrays.<Edge>asList(), containsInAnyOrder(Iterators.toArray(g.getInEdges(2), Edge.class)));
		assertThat("In Edges Test n3", Arrays.asList(e3, e4, e5, e6), containsInAnyOrder(Iterators.toArray(g.getInEdges(3), Edge.class)));
		assertThat("In Edges Test n4", Arrays.asList(e0, e1), containsInAnyOrder(Iterators.toArray(g.getInEdges(4), Edge.class)));
	}

	@Test
	public void testGetNeighborhood() {
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		assertThat("Neighborhood Test n0", Arrays.asList(1l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(0), Long.class)));
		assertThat("Neighborhood Test n1", Arrays.asList(4l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(1), Long.class)));
		assertThat("Neighborhood Test n2", Arrays.asList(4l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(2), Long.class)));
		assertThat("Neighborhood Test n3", Arrays.asList(1l, 0l), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(3), Long.class)));
		assertThat("Neighborhood Test n4", Arrays.asList(1l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(4), Long.class)));
	}
	
	@Test
	public void testContainsNode(){
		g.addNode(n0);
		g.addNode(n1);
		g.addNode(n2);
		
		assertEquals("Contains Test n0",g.containsNode(n0.getId()),true);
		assertEquals("Contains Test n1",g.containsNode(n1.getId()),true);
		assertEquals("Contains Test n2",g.containsNode(n2.getId()),true);
		assertEquals("Contains Test n3",g.containsNode(n3.getId()),false);
	}
	
	@Test(expected = DuplicatedNodeException.class)
	public void testAddNodeException(){
		g.addNode(n0);
		g.addNode(n1);
		g.addNode(n0);
	}
	
	@Test(expected = NodeNotFoundException.class)
	public void testAddEdgeException(){
		g.addNode(n0);
		g.addEdge(e2);
	}

	@Test
	public void testNeighborhoodPertinenceOnException(){
		g.addNode(n0);
		g.addNode(n1);
		g.addNode(n2);
		
		g.addEdge(e2);
		try{
			g.addEdge(e6);
		}catch(NodeNotFoundException e){
			Iterator<Long> neighbors = g.getNeighborhood(n0.getId());
			assertEquals("First neighbor must be 1", new Long(1), neighbors.next());
			assertEquals("Iterator must end", false, neighbors.hasNext());
			
			Iterator<Edge> inEdges = g.getInEdges(n0.getId());
			assertEquals("Should be no in edge on n0", false, inEdges.hasNext());
			
			Iterator<Edge> outEdges = g.getOutEdges(n0.getId());
			assertEquals("Should be a out edge on n0", true, outEdges.hasNext());
			assertEquals("The out edge of n0 should be to node 1", 1l, outEdges.next().getToNodeId());
			assertEquals("Should be no other out edge on n0", false, outEdges.hasNext());
		}
	}
}
