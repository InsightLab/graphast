package org.graphast.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstrach.DijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class CHGraphTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static CHGraph graphHopperExample;
	private static CHGraph graphHopperExampleWithPoIs;
	private static CHGraph graphHopperExample2;
	private static CHGraph graphHopperExample2WithPoIs;
	private static CHGraph graphHopperExample3;
	private static CHGraph graphHopperExample3WithPoIs;
	private static CHGraph graphHopperExample4;
	private static CHGraph graphHopperExample4WithPoIs;
	private static CHGraph graphHopperExample5;
	private static CHGraph graphHopperExample5WithPoIs;
	private static CHGraph graphHopperTest;
	private static CHGraph graphHopperTest2;


	@BeforeClass
	public static void setup() {

		graphHopperExample = new GraphGenerator().generateGraphHopperExample();
		graphHopperExampleWithPoIs = new GraphGenerator().generateGraphHopperExampleWithPoIs();
		graphHopperExample2 = new GraphGenerator().generateGraphHopperExample2();
		graphHopperExample2WithPoIs = new GraphGenerator().generateGraphHopperExample2WithPoIs();
		graphHopperExample3 = new GraphGenerator().generateGraphHopperExample3();
		graphHopperExample3WithPoIs = new GraphGenerator().generateGraphHopperExample3WithPoIs();
		graphHopperExample4 = new GraphGenerator().generateGraphHopperExample4();
		graphHopperExample4WithPoIs = new GraphGenerator().generateGraphHopperExample4WithPoIs();
		graphHopperExample5 = new GraphGenerator().generateGraphHopperExample5();
		graphHopperExample5WithPoIs = new GraphGenerator().generateGraphHopperExample5WithPoIs();
		
		graphHopperTest = new GraphGenerator().generateGraphHopperTest();
		graphHopperTest2 = new GraphGenerator().generateGraphHopperTest2();
		
	}
	
	/*
	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphHopperExampleWithPoIsTest() {

		int[] nearestNodesDistances = {2, 5};

		graphHopperExampleWithPoIs.prepareNodes();
		
		graphHopperExampleWithPoIs.contractNodes();

		DijkstraCH dj = new DijkstraCH(graphHopperExampleWithPoIs);

		StopWatch knnSW = new StopWatch();
		
		knnSW.start();

		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperExampleWithPoIs.getNode(3), 2);

		knnSW.stop();
		
		logger.info("Execution Time of shortestPathMonacoTest(): {}ms", knnSW.getNanos());

		for (Path path : pathToNearestNeighbours) {

			logger.info("Contracted Path");
			logger.info("\t{}", path.getInstructions());
			
			logger.info("Uncontracted Path");
			for (CHEdge edge : path.uncontractPath(graphHopperExampleWithPoIs)) {
				logger.info("\t{}", edge.getLabel());
			}

		}
		
		for(int i=0; i<pathToNearestNeighbours.size(); i++) {
			assertEquals(nearestNodesDistances[i], pathToNearestNeighbours.get(i).getTotalDistance());
		}

	}
	
	/*
	 * This test uses a modified version of testDirectedGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphHopperExample2WithPoIsTest() {

		int[] nearestNodesDistances = {2, 3};		

		graphHopperExample2WithPoIs.prepareNodes();
		
		graphHopperExample2WithPoIs.contractNodes();

		DijkstraCH dj = new DijkstraCH(graphHopperExample2WithPoIs);

		StopWatch knnSW = new StopWatch();
		knnSW.start();

		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperExample2WithPoIs.getNode(1), 2);

		knnSW.stop();

		logger.info("Execution Time of shortestPathMonacoTest(): {}ms", knnSW.getNanos());

		for (Path path : pathToNearestNeighbours) {

			logger.info("Contracted Path");
			logger.info("\t{}", path.getInstructions());
			
			logger.info("Uncontracted Path");
			for (CHEdge edge : path.uncontractPath(graphHopperExample2WithPoIs)) {
				logger.info("\t{}", edge.getLabel());
			}

		}
		
		for(int i=0; i<pathToNearestNeighbours.size(); i++) {
			assertEquals(nearestNodesDistances[i], pathToNearestNeighbours.get(i).getTotalDistance());
		}

	}
	
	
	@Test
	public void graphHopperExample3Test() {


		graphHopperExample3.prepareNodes();
		
		graphHopperExample3.contractNodes();
		
		System.out.println(graphHopperExample3);

	}
	
	/*
	 * This test uses a modified version of testDirectedGraph3() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphHopperExample3WithPoIsTest() {

		int[] nearestNodesDistances = {4, 8, 8};

		graphHopperExample3WithPoIs.prepareNodes();
		
		graphHopperExample3WithPoIs.contractNodes();
		
		System.out.println(graphHopperExample3WithPoIs);

		DijkstraCH dj = new DijkstraCH(graphHopperExample3WithPoIs);

		StopWatch knnSW = new StopWatch();
		knnSW.start();

		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperExample3WithPoIs.getNode(0), 3);

		knnSW.stop();

		logger.info("Execution Time of shortestPathMonacoTest(): {}ms", knnSW.getNanos());

		for (Path path : pathToNearestNeighbours) {

			logger.info("Contracted Path");
			logger.info("\t{}", path.getInstructions());
			
			logger.info("Uncontracted Path");
			for (CHEdge edge : path.uncontractPath(graphHopperExample3WithPoIs)) {
				logger.info("\t{}", edge.getLabel());
			}

		}
		
		for(int i=0; i<pathToNearestNeighbours.size(); i++) {
			assertEquals(nearestNodesDistances[i], pathToNearestNeighbours.get(i).getTotalDistance());
		}
	}
	
	/*
	 * This test uses a modified version of initRoundaboutGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 * 
	 * TEST IS (not) WORKING!
	 */
//	@Test
//	public void graphHopperExample4WithPoIsTest() {
//
//		int[] nearestNodesDistances = {3, 3, 7, 7};
//		
//		graphHopperExample4WithPoIs.prepareNodes();
//		graphHopperExample4WithPoIs.contractNodes();
//		System.out.println(graphHopperExample4WithPoIs);
//		DijkstraCH dj = new DijkstraCH(graphHopperExample4WithPoIs);
//
//		StopWatch knnSW = new StopWatch();
//		knnSW.start();
//
//		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperExample4WithPoIs.getNode(1), 3);
//
//		knnSW.stop();
//
//		logger.info("Execution Time of shortestPathMonacoTest(): {}ms", knnSW.getNanos());
//
//		for (Path path : pathToNearestNeighbours) {
//
//			logger.info("Contracted Path");
//			logger.info("\t{}", path.getInstructions());
//			
//			logger.info("Uncontracted Path");
//			for (CHEdge edge : path.uncontractPath(graphHopperExample4WithPoIs)) {
//				logger.info("\t{}", edge.getLabel());
//			}
//
//		}
//		
//		for(int i=0; i<pathToNearestNeighbours.size(); i++) {
//			assertEquals(nearestNodesDistances[i], pathToNearestNeighbours.get(i).getTotalDistance());
//		}
//
//	}
	
//	@Test
//	public void graphHopperTest() {
//		
//		
//		graphHopperTest.prepareNodes();
//		System.out.println(graphHopperTest);
//		graphHopperTest.contractNodes();
//		System.out.println(graphHopperTest);
//		DijkstraCH dj = new DijkstraCH(graphHopperTest);
//
//		StopWatch knnSW = new StopWatch();
//		knnSW.start();
//
//		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperTest.getNode(0), 3);
//
//		knnSW.stop();
//
//		System.out.println(knnSW.getNanos());
//
//		for (Path path : pathToNearestNeighbours) {
//
//			System.out.println("Caminho contraído");
//			System.out.println("\t" + path.getInstructions());
//			
//			System.out.println("Caminho descontraído");
//			for (CHEdge edge : path.uncontractPath(graphHopperTest)) {
//				System.out.println("\t" + edge.getLabel());
//			}
//
//		}
//		System.out.println("Terminou");
//		
//	}
	
//	@Test
//	public void graphHopper2Test() {
//		
//		System.out.println(graphHopperTest2);
//		
//		graphHopperTest2.prepareNodes();
//		graphHopperTest2.contractNodes();
//		System.out.println(graphHopperTest2);
//		DijkstraCH dj = new DijkstraCH(graphHopperTest2);
//
//		StopWatch knnSW = new StopWatch();
//		knnSW.start();
//
//		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperTest2.getNode(1), 3);
//
//		knnSW.stop();
//
//		System.out.println(knnSW.getNanos());
//
//		for (Path path : pathToNearestNeighbours) {
//
//			System.out.println("Caminho contraído");
//			System.out.println("\t" + path.getInstructions());
//			
//			System.out.println("Caminho descontraído");
//			for (CHEdge edge : path.uncontractPath(graphHopperTest2)) {
//				System.out.println("\t" + edge.getLabel());
//			}
//
//		}
//		System.out.println("Terminou");
//		
//	}

	
	
//	
//	/*
//	 * This test uses the testDirectedGraph() graph from GraphHopper
//	 * PrepareContractionHierarchiesTest.java class. 
//	 */
//	@Test
//	public void graphHopperExample5Test() {
//
//		int[] prepareNodesPriorities = {40, -40, -20, -20};
//		int[] contractNodesPriorities = {40, -40, -20, -20};
//		
//		assertEquals(4, graphHopperExample5.getNumberOfNodes());
//		assertEquals(8, graphHopperExample5.getNumberOfEdges());
//
//		graphHopperExample5.prepareNodes();
//		
//		for(int i=0; i<graphHopperExample5.getNumberOfNodes(); i++) {
//			assertEquals(prepareNodesPriorities[i], graphHopperExample5.getNode(i).getPriority());
//		}
//		
//		graphHopperExample5.contractNodes();
//
//		for(int i=0; i<graphHopperExample5.getNumberOfNodes(); i++) {
//			assertEquals(contractNodesPriorities[i], graphHopperExample5.getNode(i).getPriority());
//		}
//
//		assertEquals(4, graphHopperExample5.getNumberOfNodes());
//		assertEquals(8, graphHopperExample5.getNumberOfEdges());
//
//	}
//	
//	/*
//	 * This test uses a modified version of testDirectedGraph() graph from GraphHopper
//	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
//	 * HyperEdges.
//	 */
//	@Test
//	public void graphHopperExample5WithPoIsTest() {
//
//		int[] prepareNodesPriorities = {192, -26, -20, -20, 252};
//		int[] contractNodesPriorities = {192, -26, -20, -20, 252};
//		int[] nearestNodesDistances = {14};		
//		
//		assertEquals(5, graphHopperExample5WithPoIs.getNumberOfNodes());
//		assertEquals(13, graphHopperExample5WithPoIs.getNumberOfEdges());
//
//		graphHopperExample5WithPoIs.prepareNodes();
//		
//		for(int i=0; i<graphHopperExample5WithPoIs.getNumberOfNodes(); i++) {
//			assertEquals(prepareNodesPriorities[i], graphHopperExample5WithPoIs.getNode(i).getPriority());
//		}
//		
//		graphHopperExample5WithPoIs.contractNodes();
//
//		for(int i=0; i<graphHopperExample5WithPoIs.getNumberOfNodes(); i++) {
//			assertEquals(contractNodesPriorities[i], graphHopperExample5WithPoIs.getNode(i).getPriority());
//		}
//
//		assertEquals(5, graphHopperExample5WithPoIs.getNumberOfNodes());
//		assertEquals(13, graphHopperExample5WithPoIs.getNumberOfEdges());
//
//		DijkstraCH dj = new DijkstraCH(graphHopperExample5WithPoIs);
//
//		StopWatch knnSW = new StopWatch();
//		knnSW.start();
//
//		List<Path> pathToNearestNeighbours = dj.shortestPath(graphHopperExample5WithPoIs.getNode(3), 1);
//
//		knnSW.stop();
//
//		logger.info("Execution Time of shortestPathMonacoTest(): {}ms", knnSW.getNanos());
//
//		for (Path path : pathToNearestNeighbours) {
//
//			logger.info("Contracted Path");
//			logger.info("\t{}", path.getInstructions());
//			
//			logger.info("Uncontracted Path");
//			for (CHEdge edge : path.uncontractPath(graphHopperExample5WithPoIs)) {
//				logger.info("\t{}", edge.getLabel());
//			}
//
//		}
//		
//		for(int i=0; i<pathToNearestNeighbours.size(); i++) {
//			assertEquals(nearestNodesDistances[i], pathToNearestNeighbours.get(i).getTotalDistance());
//		}
//
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	// Test OK!
	// @Test
	// public void graphHopperExample4WithPoIsTest() {
	//
	// assertEquals(8, graphHopperExample4WithPoIs.getNumberOfNodes());
	// assertEquals(21, graphHopperExample4WithPoIs.getNumberOfEdges());
	//
	// graphHopperExample4WithPoIs.prepareNodes();
	// graphHopperExample4WithPoIs.contractNodes();
	//
	// assertEquals(8, graphHopperExample4WithPoIs.getNumberOfNodes());
	// assertEquals(25, graphHopperExample4WithPoIs.getNumberOfEdges());
	//
	// for (int i = 0; i < graphHopperExample4WithPoIs.getNumberOfNodes(); i++)
	// {
	// CHNode n = graphHopperExample4WithPoIs.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() + ",
	// Priority: " + n.getPriority());
	//
	// }
	//
	// for (int i = 0; i < graphHopperExample4WithPoIs.getNumberOfEdges(); i++)
	// {
	// CHEdge e = graphHopperExample4WithPoIs.getEdge(i);
	//
	// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() +
	// ", TO: " + e.getToNode()
	// + ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut());
	//
	// }
	//
	// DijkstraCH dj = new DijkstraCH(graphHopperExample4WithPoIs);
	//
	// StopWatch knnSW = new StopWatch();
	// knnSW.start();
	//
	// dj.shortestPath(graphHopperExample4WithPoIs.getNode(0), 2);
	//
	// knnSW.stop();
	//
	// System.out.println(knnSW.getNanos());
	//
	// for (Path p : dj.shortestPath(graphHopperExample4WithPoIs.getNode(0), 2))
	// {
	//
	// // TODO Create a Priority Queue for all paths, so we can retrieve
	// // just the k ones
	// System.out.println("Caminho contraído");
	// System.out.println("\t" + p.getInstructions());
	// System.out.println("Caminho descontraído");
	// for (CHEdge edge : p.uncontractPath(graphHopperExample4WithPoIs)) {
	// System.out.println("\t" + edge.getLabel());
	// }
	//
	// }
	//
	// }

	// @Test
	// public void graphHopperExample5WithPoIsTest() {
	//
	// assertEquals(5, graphHopperExample5WithPoIs.getNumberOfNodes());
	// assertEquals(13, graphHopperExample5WithPoIs.getNumberOfEdges());
	//
	// graphHopperExample5WithPoIs.prepareNodes();
	// graphHopperExample5WithPoIs.contractNodes();
	//
	// assertEquals(5, graphHopperExample5WithPoIs.getNumberOfNodes());
	// assertEquals(13, graphHopperExample5WithPoIs.getNumberOfEdges());
	//
	// for (int i = 0; i < graphHopperExample5WithPoIs.getNumberOfNodes(); i++)
	// {
	// CHNode n = graphHopperExample5WithPoIs.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() + ",
	// Priority: " + n.getPriority());
	//
	// }
	//
	// for (int i = 0; i < graphHopperExample5WithPoIs.getNumberOfEdges(); i++)
	// {
	// CHEdge e = graphHopperExample5WithPoIs.getEdge(i);
	//
	// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() +
	// ", TO: " + e.getToNode()
	// + ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut());
	//
	// }
	//
	// DijkstraCH dj = new DijkstraCH(graphHopperExample5WithPoIs);
	//
	// StopWatch knnSW = new StopWatch();
	// knnSW.start();
	//
	// dj.shortestPath(graphHopperExample5WithPoIs.getNode(3), 1);
	//
	// knnSW.stop();
	//
	// System.out.println(knnSW.getNanos());
	//
	// for (Path p : dj.shortestPath(graphHopperExample5WithPoIs.getNode(3), 1))
	// {
	//
	// // TODO Create a Priority Queue for all paths, so we can retrieve
	// // just the k ones
	// System.out.println("Caminho contraído");
	// System.out.println("\t" + p.getInstructions());
	// System.out.println("Caminho descontraído");
	// for (CHEdge edge : p.uncontractPath(graphHopperExample5WithPoIs)) {
	// System.out.println("\t" + edge.getLabel());
	// }
	//
	// }
	//
	// }

	// @Test
	// public void graphHopperExample6WithPoIsTest() {
	//
	// assertEquals(9, graphHopperExample6WithPoIs.getNumberOfNodes());
	// assertEquals(22, graphHopperExample6WithPoIs.getNumberOfEdges());
	//
	// graphHopperExample6WithPoIs.prepareNodes();
	// graphHopperExample6WithPoIs.contractNodes();
	//
	// assertEquals(9, graphHopperExample6WithPoIs.getNumberOfNodes());
	// assertEquals(26, graphHopperExample6WithPoIs.getNumberOfEdges());
	//
	// for (int i = 0; i < graphHopperExample6WithPoIs.getNumberOfNodes(); i++)
	// {
	// CHNode n = graphHopperExample6WithPoIs.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() + ",
	// Priority: " + n.getPriority());
	//
	// }
	//
	// for (int i = 0; i < graphHopperExample6WithPoIs.getNumberOfEdges(); i++)
	// {
	// CHEdge e = graphHopperExample6WithPoIs.getEdge(i);
	//
	// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() +
	// ", TO: " + e.getToNode()
	// + ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut());
	//
	// }
	//
	// DijkstraCH dj = new DijkstraCH(graphHopperExample6WithPoIs);
	//
	// StopWatch knnSW = new StopWatch();
	// knnSW.start();
	//
	// dj.shortestPath(graphHopperExample6WithPoIs.getNode(1), 2);
	//
	// knnSW.stop();
	//
	// System.out.println(knnSW.getNanos());
	//
	// for (Path p : dj.shortestPath(graphHopperExample6WithPoIs.getNode(1), 2))
	// {
	//
	// // TODO Create a Priority Queue for all paths, so we can retrieve
	// // just the k ones
	// System.out.println("Caminho contraído");
	// System.out.println("\t" + p.getInstructions());
	// System.out.println("Caminho descontraído");
	// for (CHEdge edge : p.uncontractPath(graphHopperExample6WithPoIs)) {
	// System.out.println("\t" + edge.getLabel());
	// }
	//
	// }
	//
	// }

	// @Test
	// public void prepareNodesTest() {
	//
	// assertEquals(13, graphHopperExampleWithPoIs.getNumberOfNodes());
	// assertEquals(31, graphHopperExampleWithPoIs.getNumberOfEdges());
	//
	// graphHopperExampleWithPoIs.prepareNodes();
	// graphHopperExampleWithPoIs.contractNodes();
	//
	// assertEquals(13, graphHopperExampleWithPoIs.getNumberOfNodes());
	// assertEquals(37, graphHopperExampleWithPoIs.getNumberOfEdges());
	//
	// int j = graphHopperExampleWithPoIs.getNodePriorityQueue().size();

	// for (int i = 0; i < j; i++) {
	//
	// System.out.println("NID: " +
	// graphHopperExampleWithPoIs.getNodePriorityQueue().peek().getId()
	// + ", Priority: " +
	// graphHopperExampleWithPoIs.getNodePriorityQueue().peek().getPriority());
	// graphHopperExampleWithPoIs.getNodePriorityQueue().poll();
	// }
	//
	// for (int i = 0; i < graphHopperExampleWithPoIs.getNumberOfNodes(); i++) {
	// CHNode n = graphHopperExampleWithPoIs.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() + ",
	// Priority: " + n.getPriority());
	//
	// }
	//
	// for (int i = 0; i < graphHopperExampleWithPoIs.getNumberOfEdges(); i++) {
	// CHEdge e = graphHopperExampleWithPoIs.getEdge(i);
	//
	// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() +
	// ", TO: " + e.getToNode()
	// + ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut());
	//
	// }

	// DijkstraCH dj = new DijkstraCH(graphHopperExampleWithPoIs);
	//
	// StopWatch knnSW = new StopWatch();
	// knnSW.start();
	//
	// dj.shortestPath(graphHopperExampleWithPoIs.getNode(2), 2);
	//
	// knnSW.stop();
	//
	// System.out.println(knnSW.getNanos());
	//
	// for (Path p : dj.shortestPath(graphHopperExampleWithPoIs.getNode(2), 3))
	// {
	//
	// //TODO Create a Priority Queue for all paths, so we can retrieve just the
	// k ones
	// System.out.println("Caminho contraído");
	// System.out.println("\t" + p.getInstructions());
	// System.out.println("Caminho descontraído");
	// for(CHEdge edge : p.uncontractPath(graphHopperExampleWithPoIs)) {
	// System.out.println("\t" + edge.getLabel());
	// }
	//
	//
	// }

	// }

	// @Test
	// public void contractionHierarchyAndorraTest() {
	// TODO COMPARE TESTS WITH GRAPHHOPPER
	// assertEquals(13, graphExampleAndorraCH.getNumberOfNodes());
	// assertEquals(31, graphExampleAndorraCH.getNumberOfEdges());

	// graphExampleAndorraCH.prepareNodes();

	// for (int i = 0; i < graphExampleAndorraCH.getNumberOfNodes(); i++) {
	// CHNode n = graphExampleAndorraCH.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() +
	// ", Priority: " + n.getPriority());
	//
	// }

	// graphExampleAndorraCH.contractNodes();

	// for (int i = 0; i < graphExampleAndorraCH.getNumberOfNodes(); i++) {
	// CHNode n = graphExampleAndorraCH.getNode(i);
	//
	// System.out.println("NID: " + n.getId() + ", Level: " + n.getLevel() +
	// ", Priority: " + n.getPriority());
	//
	// }
	//
	// for (int i = 0; i < graphExampleAndorraCH.getNumberOfEdges(); i++) {
	// CHEdge e = graphExampleAndorraCH.getEdge(i);
	//
	// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode()
	// + ", TO: " + e.getToNode()
	// + ", Distance: " + e.getDistance() + ", isShortcut: " +
	// e.isShortcut());
	//
	// }

	// j = graphExampleAndorraCH.getNodePriorityQueue().size();
	//
	// for (int i = 0; i < j; i++) {
	//
	// System.out.println("NID: " +
	// graphExampleAndorraCH.getNodePriorityQueue().peek().getId() + ",
	// Priority: "
	// + graphExampleAndorraCH.getNodePriorityQueue().peek().getPriority());
	// graphExampleAndorraCH.getNodePriorityQueue().poll();
	// }

	// }

	// @Test
	// public void contractionHierarchyMonacoTest() {
	//
	// StopWatch preComputationSW = new StopWatch();
	// preComputationSW.start();
	// graphExampleMonacoCH.prepareNodes();
	//// System.out.println(graphExampleMonacoCH.getNumberOfNodes());
	//// System.out.println(graphExampleMonacoCH.getNumberOfEdges());
	//
	// graphExampleMonacoCH.contractNodes();
	// preComputationSW.stop();
	//
	// System.out.println(preComputationSW);
	//
	//// for (int i = 0; i < graphExampleMonacoCH.getNumberOfNodes(); i++) {
	//// CHNode n = graphExampleMonacoCH.getNode(i);
	////
	//// System.out.println(n.getId() + "," + n.getLevel() + "," +
	// n.getPriority());
	////
	//// }
	////
	//// for (int i = 0; i < graphExampleMonacoCH.getNumberOfEdges(); i++) {
	//// CHEdge e = graphExampleMonacoCH.getEdge(i);
	////
	//// System.out.println("EID: " + e.getId() + ", FROM: " + e.getFromNode() +
	// ", TO: " + e.getToNode()
	//// + ", Distance: " + e.getDistance() + ", isShortcut: " +
	// e.isShortcut());
	////
	//// }
	//
	// DijkstraCH dj = new DijkstraCH(graphExampleMonacoCH);
	//
	// StopWatch knnSW = new StopWatch();
	// knnSW.start();
	//
	// dj.shortestPath(graphExampleMonacoCH.getNode(177), 3);
	//
	// knnSW.stop();
	//
	// System.out.println(knnSW.getNanos());
	//
	// for (Path p : dj.shortestPath(graphExampleMonacoCH.getNode(177), 3)) {
	//
	// System.out.println(p.getInstructions());
	//// p.uncontractPath(graphExampleMonacoCH);
	// }
	//
	// }

}
