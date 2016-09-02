package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstraCH.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BidirectionalDijkstraTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static CHGraph graphHopperExample;
	private static CHGraph graphHopperExample2;
	private static CHGraph graphHopperExample3;
	private static CHGraph graphHopperExample4;
	private static CHGraph graphMITExample;
	private static CHGraph graphMITExample2;
	private static CHGraph graphMonaco;
	
	@BeforeClass
	public static void setup() {

		graphHopperExample = new GraphGenerator().generateGraphHopperExample();
		graphHopperExample2 = new GraphGenerator().generateGraphHopperExample2();
		graphHopperExample3 = new GraphGenerator().generateGraphHopperExample3();
		graphHopperExample4 = new GraphGenerator().generateGraphHopperExample4();
		graphMITExample = new GraphGenerator().generateMITExample();
		graphMITExample2 = new GraphGenerator().generateMITExample2();
		graphMonaco = new GraphGenerator().generateMonacoCH();
		
	}
	
//	@Test
//	public void graphHopperExampleTest() {
//		
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path finalPath = bidirectionalDijkstra.execute(graphHopperExample.getNode(3), graphHopperExample.getNode(5));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of graphHopperExampleTest(): {}ms", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("Shortest Path");
//		logger.info("\t{}", finalPath.getInstructions());
//		
//		assertEquals(5, finalPath.getTotalDistance());
//	
//	}
//	
//	@Test
//	public void graphHopperExample2Test() {
//		
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample2);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path finalPath = bidirectionalDijkstra.execute(graphHopperExample2.getNode(0), graphHopperExample2.getNode(3));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphHopperExample2Test(): {}ns", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("Shortest Path");
//		logger.info("\t{}", finalPath.getInstructions());
//		
//		assertEquals(3, finalPath.getTotalDistance());
//		
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphHopperExample2);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path dijkstraPath = dijkstra.shortestPath(graphHopperExample2.getNode(0), graphHopperExample2.getNode(3));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of regular Dijkstra Algorithm graphHopperExample2Test(): {}ns", dijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Regular Dijkstra");
//		logger.info("\t{}", dijkstraPath.getInstructions());
//		
//		assertEquals(3, dijkstraPath.getTotalDistance());
//
//		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
//			logger.info("Bidirectional Dijkstra was FASTER");
//		} else {
//			logger.info("Bidirectional Dijkstra was SLOWER");
//		}
//	
//	}
//	
//	
//	/*
//	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
//	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
//	 * HyperEdges.
//	 */
//	@Test
//	public void graphMITTest() {
//
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMITExample);
//
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//
//		Path bidirectionalDijkstraPath = bidirectionalDijkstra.execute(graphMITExample.getNode(0), graphMITExample.getNode(4));
//
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphMITTest(): {}ns", bidirectionalDijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Bidirectional Dijkstra");
//		logger.info("\t{}", bidirectionalDijkstraPath.getInstructions());
//		
//		assertEquals(9, bidirectionalDijkstraPath.getTotalDistance());
//		
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphMITExample);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path dijkstraPath = dijkstra.shortestPath(graphMITExample.getNode(0), graphMITExample.getNode(4));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of regular Dijkstra Algorithm graphMITTest(): {}ns", dijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Regular Dijkstra");
//		logger.info("\t{}", dijkstraPath.getInstructions());
//		
//		assertEquals(9, dijkstraPath.getTotalDistance());
//		
//		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
//			logger.info("Bidirectional Dijkstra was FASTER");
//		} else {
//			logger.info("Bidirectional Dijkstra was SLOWER");
//		}
//
//	}
//	
//	/*
//	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
//	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
//	 * HyperEdges.
//	 */
//	@Test
//	public void graphMITExample2Test() {
//
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMITExample2);
//
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//
//		Path bidirectionalDijkstraPath = bidirectionalDijkstra.execute(graphMITExample2.getNode(0), graphMITExample2.getNode(8));
//
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphMITExample2Test(): {}ns", bidirectionalDijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Bidirectional Dijkstra");
//		logger.info("\t{}", bidirectionalDijkstraPath.getInstructions());
//		
//		assertEquals(16, bidirectionalDijkstraPath.getTotalDistance());
//		
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphMITExample2);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path dijkstraPath = dijkstra.shortestPath(graphMITExample2.getNode(0), graphMITExample2.getNode(8));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of regular Dijkstra Algorithm graphMITRegularDijkstraExample2Test(): {}ns", dijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Regular Dijkstra");
//		logger.info("\t{}", dijkstraPath.getInstructions());
//		
//		assertEquals(16, dijkstraPath.getTotalDistance());
//
//		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
//			logger.info("Bidirectional Dijkstra was FASTER");
//		} else {
//			logger.info("Bidirectional Dijkstra was SLOWER");
//		}
//		
//	}
//	
//	@Test
//	public void graphHopperExample3Test() {
//		
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample3);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path finalPath = bidirectionalDijkstra.execute(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphHopperExample2Test(): {}ns", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("Shortest Path");
//		logger.info("\t{}", finalPath.getInstructions());
//		
//		assertEquals(8, finalPath.getTotalDistance());
//		
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphHopperExample3);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path dijkstraPath = dijkstra.shortestPath(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of regular Dijkstra Algorithm graphMITRegularDijkstraExample2Test(): {}ns", dijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Regular Dijkstra");
//		logger.info("\t{}", dijkstraPath.getInstructions());
//		
//		assertEquals(8, dijkstraPath.getTotalDistance());
//
//		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
//			logger.info("Bidirectional Dijkstra was FASTER");
//		} else {
//			logger.info("Bidirectional Dijkstra was SLOWER");
//		}
//	}
//	
//	@Test
//	public void graphHopperExample4Test() {
//		
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample4);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path finalPath = bidirectionalDijkstra.execute(graphHopperExample4.getNode(15), graphHopperExample4.getNode(8));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphHopperExample4Test(): {}ns", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("Shortest Path");
//		logger.info("\t{}", finalPath.getInstructions());
//		
//		assertEquals(8, finalPath.getTotalDistance());
//		
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphHopperExample4);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path dijkstraPath = dijkstra.shortestPath(graphHopperExample4.getNode(15), graphHopperExample4.getNode(8));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of regular Dijkstra Algorithm graphHopperExample4Test(): {}ns", dijkstraSW.getNanos());
//		
//		logger.info("Shortest Path - Regular Dijkstra");
//		logger.info("\t{}", dijkstraPath.getInstructions());
//		
//		assertEquals(8, dijkstraPath.getTotalDistance());
//
//		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
//			logger.info("Bidirectional Dijkstra was FASTER");
//		} else {
//			logger.info("Bidirectional Dijkstra was SLOWER");
//		}
//	
//	}
	
	@Test
	public void shortestPathMonacoTest() {

		Long source = graphMonaco.getNodeId(43.740174,7.424376);
		Long target = graphMonaco.getNodeId(43.735554,7.416147);

		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMonaco);

		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();

		Path finalPath = bidirectionalDijkstra.execute(graphMonaco.getNode(source), graphMonaco.getNode(target));

		bidirectionalDijkstraSW.stop();
		
		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in shortestPathMonacoTest(): {}ns", bidirectionalDijkstraSW.getNanos());
		
		System.out.println("BREAK TEST");
		
	}
	
	
}
