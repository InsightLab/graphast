package org.graphast.model;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.junit.BeforeClass;
import org.junit.Test;

public class CHGraphTest {

	private static CHGraph graphHopperExample;
	private static CHGraph graphExampleCHWithPoIs;

	@BeforeClass
	public static void setup() {

		graphHopperExample = new GraphGenerator().generateExampleCH();
		graphExampleCHWithPoIs = new GraphGenerator().generateExampleCHWithPoIs();

	}

	@Test
	public void getNodeExternalIDTest() {

		assertEquals(0, graphHopperExample.getNode(0).getExternalId());
		assertEquals(8, graphHopperExample.getNode(8).getExternalId());
		assertEquals(9, graphHopperExample.getNode(9).getExternalId());

	}

	@Test
	public void findShortcutTest() {
		// TODO COMPARE TESTS WITH GRAPHHOPPER
		// assertEquals(10, graphHopperExample.getNumberOfNodes());
		// assertEquals(19, graphHopperExample.getNumberOfEdges());
		//
		// graphHopperExample.prepareNodes();
		// graphHopperExample.contractNodes();
		//
		// // TODO COMPARE TESTS WITH GRAPHHOPPER
		// assertEquals(10, graphHopperExample.getNumberOfNodes());
		// assertEquals(21, graphHopperExample.getNumberOfEdges());

		// for(int i=0; i<graphExampleCHWithPoIs.getNumberOfNodes(); i++) {
		// CHNode n = graphExampleCHWithPoIs.getNode(i);
		//
		// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() +
		// ", Priority: " + n.getPriority());
		//
		// }
		//
		// for(int i=0; i<graphExampleCHWithPoIs.getNumberOfEdges(); i++) {
		// CHEdge e = graphExampleCHWithPoIs.getEdge(i);
		//
		// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode()
		// + ", TO: " + e.getToNode() + ", Distance: " + e.getDistance() + ",
		// isShortcut: " + e.isShortcut());
		//
		// }
		//
		// DijkstraCH dj = new DijkstraCH(graphExampleCHWithPoIs);
		//
		// for(Path p : dj.shortestPath(graphExampleCHWithPoIs.getNode(2), 2)) {
		//
		// System.out.println(p.getInstructions());
		//
		// }

	}

	@Test
	public void prepareNodesTest() {
		// TODO COMPARE TESTS WITH GRAPHHOPPER
		assertEquals(13, graphExampleCHWithPoIs.getNumberOfNodes());
		assertEquals(31, graphExampleCHWithPoIs.getNumberOfEdges());

		graphExampleCHWithPoIs.prepareNodes();
		

		int j = graphExampleCHWithPoIs.getNodePriorityQueue().size();

		for (int i = 0; i < j; i++) {

			System.out.println("NID: " + graphExampleCHWithPoIs.getNodePriorityQueue().peek().getId() + ", Priority: "
					+ graphExampleCHWithPoIs.getNodePriorityQueue().peek().getPriority());
			graphExampleCHWithPoIs.getNodePriorityQueue().poll();
		}
		
		
		
		
		
//		graphExampleCHWithPoIs.contractNodes();

//		for (int i = 0; i < graphExampleCHWithPoIs.getNodePriorityQueue().size(); i++) {
//			CHNode n = graphExampleCHWithPoIs.getNode(i);
//
//			System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() + ", Priority: " + n.getPriority());
//
//		}
//
//		for (int i = 0; i < graphExampleCHWithPoIs.getNumberOfEdges(); i++) {
//			CHEdge e = graphExampleCHWithPoIs.getEdge(i);
//
//			System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() + ", TO: " + e.getToNode()
//					+ ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut());
//
//		}
	}

}
