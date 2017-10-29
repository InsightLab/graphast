package org.graphast.query.shortestpath;

import static org.junit.Assert.*;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.utils.DistanceVector;
import org.graphast.storage.StorageUtils;
import org.graphast.structure.MMapGraphStructure;
import org.junit.BeforeClass;
import org.junit.Test;

public class MMapDijkstraTest {
	
	private static Graph g;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String graphName = "dijkstra_graph";
		
		StorageUtils.deleteMMapGraph(graphName);
		
		g = new Graph(new MMapGraphStructure(graphName));
		Node n0 = new Node(0);
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Edge e0 = new Edge(0, 1, 3, true);
		Edge e1 = new Edge(1, 2, 3, true);
		Edge e2 = new Edge(2, 3, 3, true);
		Edge e3 = new Edge(0, 4, 5, true);
		Edge e4 = new Edge(4, 3, 5, true);
		Edge e5 = new Edge(0, 5, 15, true);
		g.addNodes(n0, n1, n2, n3, n4, n5);
		g.addEdges(e0, e1, e2, e3, e4, e5);
	}

	@Test
	public void testOneToAll() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(0);
		assertEquals("One to All distance test n3", 9, vector.getDistance(3), 0);
		assertEquals("One to All distance test n2", 6, vector.getDistance(2), 0);
		assertEquals("One to All distance test n4", 5, vector.getDistance(4), 0);
		assertEquals("One to All Parent test n3", 2, vector.getElement(3).getParentId());
		assertEquals("One to All Parent test n2", 1, vector.getElement(2).getParentId());
		assertEquals("One to All Parent test n4", 0, vector.getElement(4).getParentId());
	}

	@Test
	public void testOneToOne() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(3, 1);
		assertEquals("One to One distance test n1", 6, vector.getDistance(1), 0);
		assertEquals("One to One Parent test n5", -1, vector.getElement(5).getParentId());
		assertEquals("One to One Parent test n1", 2, vector.getElement(1).getParentId());
	}

}
