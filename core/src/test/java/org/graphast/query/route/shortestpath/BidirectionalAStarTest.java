package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalAStarCH;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BidirectionalAStarTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static CHGraph graphHopperExample3;
	private static CHGraph graphMITExample4;
	private static CHGraph graphMonaco;
	private static CHGraph graphSeattle;

	@BeforeClass
	public static void setup() {

		graphHopperExample3 = new GraphGenerator().generateGraphHopperExample3();
		graphMITExample4 = new GraphGenerator().generateMITExample4();
		graphMonaco = new GraphGenerator().generateMonacoCH();
		graphSeattle = new GraphGenerator().generateSeattleCH();

	}

	@Test
	public void graphMIT4Test() {

		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphMITExample4);

		StopWatch regularDijkstraSW = new StopWatch();

		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphMITExample4.getNode(0),
				graphMITExample4.getNode(4));

		regularDijkstraSW.stop();

		assertEquals(90, regularDijkstraFinalPath.getTotalDistance());

		logger.info("[REGULAR] Execution Time for graphMITTest(): {}ms", regularDijkstraSW.getNanos());

		logger.info("[REGULAR] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());

		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphMITExample4);

		StopWatch bidirectionalDijkstraSW = new StopWatch();

		bidirectionalDijkstraSW.start();
		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphMITExample4.getNode(0),
				graphMITExample4.getNode(4));
		bidirectionalDijkstraSW.stop();

		logger.info("[BIDIRECTIONAL] Execution Time of graphMIT4Test(): {}ms", bidirectionalDijkstraSW.getNanos());

		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());

		assertEquals(90, bidirectionalAStarFinalPath.getTotalDistance());

		double result;

		if (regularDijkstraSW.getNanos() > bidirectionalDijkstraSW.getNanos()) {
			result = (double) regularDijkstraSW.getNanos() / (double) bidirectionalDijkstraSW.getNanos();
			logger.info("Bidirectional AStar was {}x FASTER", result);
		} else {
			result = (double) bidirectionalDijkstraSW.getNanos() / (double) regularDijkstraSW.getNanos();
			logger.info("Bidirectional AStar was {}x SLOWER", result);
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
		
		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphHopperExample3);
		
		StopWatch bidirectionalAStarSW = new StopWatch();
		
		bidirectionalAStarSW.start();
		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
		bidirectionalAStarSW.stop();
		
		logger.info("[BIDIRECTIONAL] Execution Time of graphHopperExample3Test(): {}ms", bidirectionalAStarSW.getNanos());
	
		logger.info("[BIDIRECTIONAL] Shortest Path");
		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
		
		assertEquals(8, bidirectionalAStarFinalPath.getTotalDistance());
		
		double result;
		
		if(regularDijkstraSW.getNanos() > bidirectionalAStarSW.getNanos()) {
			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalAStarSW.getNanos();
			logger.info("Bidirectional AStar was {}x FASTER",  result);
		} else {
			result = (double)bidirectionalAStarSW.getNanos()/(double)regularDijkstraSW.getNanos();
			logger.info("Bidirectional AStar was {}x SLOWER",  result);
		}
	}
	
//	@Test
//	public void aStarMonacoTest() {
//
//		Long source = graphMonaco.getNodeId(43.740174,7.424376);
//		Long target = graphMonaco.getNodeId(43.735554,7.416147);
//		
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphMonaco);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphMonaco.getNode(source), graphMonaco.getNode(target));
//
//		regularDijkstraSW.stop();
//
////		assertEquals(1072346, regularDijkstraFinalPath.getTotalDistance());
//		assertEquals(1073837, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR] Execution Time for regularShortestPathMonacoTest(): {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
//		
//		System.out.println("[REGULAR] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//		
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphMonaco);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphMonaco.getNode(source), graphMonaco.getNode(target));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL] Execution Time of aStarMonacoTest(): {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
//		
////		assertEquals(1072346, bidirectionalAStarFinalPath.getTotalDistance());
//		assertEquals(1073837, bidirectionalAStarFinalPath.getTotalDistance());
//		
//		System.out.println("[BIDIRECTIONAL] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//		
//		double result;
//		
//		if(regularDijkstraSW.getNanos() > bidirectionalAStarSW.getNanos()) {
//			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalAStarSW.getNanos();
//			logger.info("Bidirectional AStar was {}x FASTER",  result);
//		} else {
//			result = (double)bidirectionalAStarSW.getNanos()/(double)regularDijkstraSW.getNanos();
//			logger.info("Bidirectional AStar was {}x SLOWER",  result);
//		}
//		
//	}
	
//	@Test
//	public void aStarSeattleTest() {
//		
//		Long source = graphSeattle.getNodeId(47.645642,-122.374813);
//		Long target = graphSeattle.getNodeId(47.555600,-122.309465);
//		
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphSeattle);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphSeattle.getNode(source), graphSeattle.getNode(target));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(12650453, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR] Execution Time for aStarSeattleTest(): {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
//		
//		System.out.println("[REGULAR] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//		
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphSeattle);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphSeattle.getNode(source), graphSeattle.getNode(target));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL] Execution Time of aStarSeattleTest(): {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
//		
//		System.out.println("[BIDIRECTIONAL] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//		
//		assertEquals(12650453, bidirectionalAStarFinalPath.getTotalDistance());
//		
//		double result;
//		
//		if(regularDijkstraSW.getNanos() > bidirectionalAStarSW.getNanos()) {
//			result = (double)regularDijkstraSW.getNanos()/(double)bidirectionalAStarSW.getNanos();
//			logger.info("Bidirectional Dijkstra was {}x FASTER",  result);
//		} else {
//			result = (double)bidirectionalAStarSW.getNanos()/(double)regularDijkstraSW.getNanos();
//			logger.info("Bidirectional Dijkstra was {}x SLOWER",  result);
//		}
//		
//	}

}
