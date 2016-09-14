package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
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
	private static CHGraph graphMITExample3;
	private static CHGraph graphMonaco;
	private static CHGraph graphSeattle;
	private static CHGraph graphGreece;
	
	@BeforeClass
	public static void setup() {

		graphHopperExample = new GraphGenerator().generateGraphHopperExample();
		graphHopperExample2 = new GraphGenerator().generateGraphHopperExample2();
		graphHopperExample3 = new GraphGenerator().generateGraphHopperExample3();
		graphHopperExample4 = new GraphGenerator().generateGraphHopperExample4();
		graphMITExample = new GraphGenerator().generateMITExample();
		graphMITExample2 = new GraphGenerator().generateMITExample2();
		graphMITExample3 = new GraphGenerator().generateMITExample3();
		graphMonaco = new GraphGenerator().generateMonacoCH();
		graphSeattle = new GraphGenerator().generateSeattleCH();
		graphGreece = new GraphGenerator().generateGreeceCH();
		
	}
	
	@Test
	public void graphHopperExampleTest() {
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphHopperExample);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphHopperExample.getNode(3), graphHopperExample.getNode(5));

		regularDijkstraSW.stop();

		assertEquals(5, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for graphHopperExample(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphHopperExample.getNode(3), graphHopperExample.getNode(5));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphHopperExampleTest(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(5, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
	
	}
	
	@Test
	public void graphHopperExample2Test() {
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphHopperExample2);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphHopperExample2.getNode(0), graphHopperExample2.getNode(3));

		regularDijkstraSW.stop();

		assertEquals(3, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for graphHopperExample2Test(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample2);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphHopperExample2.getNode(0), graphHopperExample2.getNode(3));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphHopperExample2Test(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(3, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
	}
	
	/*
	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphMITTest() {

		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphMITExample);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphMITExample.getNode(0), graphMITExample.getNode(4));

		regularDijkstraSW.stop();

		assertEquals(9, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for graphMITTest(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMITExample);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphMITExample.getNode(0), graphMITExample.getNode(4));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphMITTest(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(9, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}

	}
	
	/*
	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphMITExample2Test() {
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphMITExample2);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphMITExample2.getNode(0), graphMITExample2.getNode(8));

		regularDijkstraSW.stop();

		assertEquals(16, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for graphMITExample2Test(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMITExample2);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphMITExample2.getNode(0), graphMITExample2.getNode(8));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphMITExample2Test(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(16, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
		
		
	}
	
	@Test
	public void graphHopperExample3Test() {
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphHopperExample3);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));

		regularDijkstraSW.stop();

		assertEquals(8, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for graphHopperExample3Test(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample3);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphHopperExample3Test(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(8, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
	}
	
	@Test
	public void graphHopperExample4Test() {
		
		Dijkstra dijkstra = new DijkstraConstantWeight(graphHopperExample4);

		StopWatch dijkstraSW = new StopWatch();
		
		dijkstraSW.start();

		Path dijkstraPath = dijkstra.shortestPath(graphHopperExample4.getNode(15), graphHopperExample4.getNode(8));

		dijkstraSW.stop();
		
		logger.info("Execution Time of regular Dijkstra Algorithm graphHopperExample4Test(): {}ns", dijkstraSW.getNanos());
		
		logger.info("Shortest Path - Regular Dijkstra");
		logger.info("\t{}", dijkstraPath.getInstructions());
		
		assertEquals(8, dijkstraPath.getTotalDistance());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphHopperExample4);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path finalPath = bidirectionalDijkstra.execute(graphHopperExample4.getNode(15), graphHopperExample4.getNode(8));
		bidirectionalDijkstraSW.stop();
		
		logger.info("Execution Time of Bidirectional Dijkstra Algorithm in graphHopperExample4Test(): {}ns", bidirectionalDijkstraSW.getNanos());
	
		logger.info("Shortest Path");
		logger.info("\t{}", finalPath.getInstructions());
		
		assertEquals(8, finalPath.getTotalDistance());

		if(dijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			logger.info("Bidirectional Dijkstra was FASTER");
		} else {
			logger.info("Bidirectional Dijkstra was SLOWER");
		}
	
	}
	
	@Test
	public void regularShortestPathMonacoTest() {

		Long source = graphMonaco.getNodeId(43.740174,7.424376);
		Long target = graphMonaco.getNodeId(43.735554,7.416147);
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphMonaco);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphMonaco.getNode(source), graphMonaco.getNode(target));

		regularDijkstraSW.stop();

		assertEquals(1073837, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for regularShortestPathMonacoTest(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMonaco);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphMonaco.getNode(source), graphMonaco.getNode(target));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of regularShortestPathMonacoTest(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(1073837, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
		
	}

	@Test
	public void regularShortestPathSeattleTest() {
		
		Long source = graphSeattle.getNodeId(47.645642,-122.374813);
		Long target = graphSeattle.getNodeId(47.555600,-122.309465);
		
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphSeattle);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphSeattle.getNode(source), graphSeattle.getNode(target));

		regularDijkstraSW.stop();

		assertEquals(12650453, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR] Execution Time for regularShortestPathSeattleTest(): {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphSeattle);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphSeattle.getNode(source), graphSeattle.getNode(target));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of regularShortestPathSeattleTest(): {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(12650453, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalDijkstraSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
		}
		
	}
	
}
