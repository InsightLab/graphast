package org.graphast.model;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.structure.MMapGraphStructure;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterators;

public class MMapGraphTest {

	private static Graph g;
	private static Node n0, n1, n2, n3, n4;
	private static Edge e0, e1, e2, e3, e4, e5, e6;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		File f = new File("test_graph/");
		if (f.exists()) {
			new File("test_graph/nodes.mmap").delete();
			new File("test_graph/edges.mmap").delete();
			f.delete();
		}
		
		g = new Graph(new MMapGraphStructure("test_graph"));
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
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		
	}
	
	@Test
	public void testNumberOfNodes() {
		assertEquals(5, g.getNumberOfNodes());
	}
	
	@Test
	public void testNumberOfEdges() {
		assertEquals(10, g.getNumberOfEdges());
	}
	
	@Test
	public void testGetOutEdges() {
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getCost());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getCost());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getCost());
		assertThat("Out Edges Test n0", Arrays.asList(e2, e6Inv), containsInAnyOrder(Iterators.toArray(g.getOutEdges(0), Edge.class)));
		assertThat("Out Edges Test n1", Arrays.asList(e0, e3Inv), containsInAnyOrder(Iterators.toArray(g.getOutEdges(1), Edge.class)));
		assertThat("Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(Iterators.toArray(g.getOutEdges(2), Edge.class)));
		assertThat("Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(Iterators.toArray(g.getOutEdges(3), Edge.class)));
		assertThat("Out Edges Test n4", Arrays.asList(e0Inv, e5), containsInAnyOrder(Iterators.toArray(g.getOutEdges(4), Edge.class)));
		
	}

	@Test
	public void testGetInEdges() {
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getCost());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getCost());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getCost());
		assertThat("In Edges Test n0", Arrays.asList(e6), containsInAnyOrder(Iterators.toArray(g.getInEdges(0), Edge.class)));
		assertThat("In Edges Test n1", Arrays.asList(e0Inv, e2, e3), containsInAnyOrder(Iterators.toArray(g.getInEdges(1), Edge.class)));
		assertThat("In Edges Test n2", Arrays.<Edge>asList(), containsInAnyOrder(Iterators.toArray(g.getInEdges(2), Edge.class)));
		assertThat("In Edges Test n3", Arrays.asList(e3Inv, e4, e5, e6Inv), containsInAnyOrder(Iterators.toArray(g.getInEdges(3), Edge.class)));
		assertThat("In Edges Test n4", Arrays.asList(e0, e1), containsInAnyOrder(Iterators.toArray(g.getInEdges(4), Edge.class)));
	}

	@Test
	public void testGetNeighborhood() {
		assertThat("Neighborhood Test n0", Arrays.asList(1, 3), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(0), Integer.class)));
		assertThat("Neighborhood Test n1", Arrays.asList(4, 3), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(1), Integer.class)));
		assertThat("Neighborhood Test n2", Arrays.asList(4, 3), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(2), Integer.class)));
		assertThat("Neighborhood Test n3", Arrays.asList(1, 0), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(3), Integer.class)));
		assertThat("Neighborhood Test n4", Arrays.asList(1, 3), containsInAnyOrder(Iterators.toArray(g.getNeighborhood(4), Integer.class)));
	}
	
	@Test
	public void testReopenGraph() {
		Graph g2 = new Graph(new MMapGraphStructure("test_graph"));
		assertEquals("Reoppened graph number of nodes", 5, g2.getNumberOfNodes());
		assertEquals("Reoppened graph number of edges", 10, g2.getNumberOfEdges());
		
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getCost());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getCost());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getCost());
		
		assertThat("Reoppened Graph Out Edges Test n0", Arrays.asList(e2, e6Inv), containsInAnyOrder(Iterators.toArray(g2.getOutEdges(0), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n1", Arrays.asList(e0, e3Inv), containsInAnyOrder(Iterators.toArray(g2.getOutEdges(1), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(Iterators.toArray(g2.getOutEdges(2), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(Iterators.toArray(g2.getOutEdges(3), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n4", Arrays.asList(e0Inv, e5), containsInAnyOrder(Iterators.toArray(g2.getOutEdges(4), Edge.class)));
	}
	

}
