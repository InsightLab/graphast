package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalAStarCH;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DistanceUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BidirectionalAStarTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	private static CHGraph graphHopperExample3;
//	private static CHGraph graphMITExample4;
	private static CHGraph graphMonaco;
//	private static CHGraph graphSeattle;
//	private static CHGraph graphGreece;
//	private static CHGraph graphTokyoBigger;

	@BeforeClass
	public static void setup() {

//		graphHopperExample3 = new GraphGenerator().generateGraphHopperExample3();
//		graphMITExample4 = new GraphGenerator().generateMITExample4();
		graphMonaco = new GraphGenerator().generateMonacoCH();
//		graphSeattle = new GraphGenerator().generateSeattleCH();
//		graphGreece = new GraphGenerator().generateGreeceCH();
//		graphTokyoBigger = new GraphGenerator().generateTokyoBiggerCH();

	}

//	@Test
//	public void graphMIT4Test() {
//
//		CHGraph graph = graphMITExample4;
//		int expectedDistance = 90;
//		
//		Long source = 0l;
//		Long target = 4l;
//		
//		//REGULAR DIJKSTRA
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graph);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graph.getNode(source), graph.getNode(target));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(expectedDistance, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR DIJKSTRA] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
//		
//		System.out.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//		
//		
//		
//		//BIDIRECTIONAL DIJKSTRA
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(source), graph.getNode(target));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
//		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
//		
//		assertEquals(expectedDistance, bidirectionalDijkstraFinalPath.getTotalDistance());
//		
//		System.out.println("[BIDIRECTIONAL DIJKSTRA] Number of settle nodes: " + (bidirectionalDijkstra.getNumberOfForwardSettleNodes() + bidirectionalDijkstra.getNumberOfBackwardSettleNodes()));
//		
//		
//		
//		//BIDIRECTIONAL A*
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graph);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graph.getNode(source), graph.getNode(target));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL A*] Execution Time: {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL A*] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
//		
//		System.out.println("[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//		
//		assertEquals(expectedDistance, bidirectionalAStarFinalPath.getTotalDistance());
//
//	}
	
//	@Test
//	public void graphHopperExample3Test() {
//		
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphHopperExample3);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(42, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR] Execution Time for graphHopperExample3Test(): {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
//		
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphHopperExample3);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphHopperExample3.getNode(9), graphHopperExample3.getNode(5));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL] Execution Time of graphHopperExample3Test(): {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
//		
//		assertEquals(42, bidirectionalAStarFinalPath.getTotalDistance());
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
//	}
	
	@Test
	public void monacoTest() {
		
		CHGraph graph = graphMonaco;
		int expectedDistance = 1136090;
		
		Long source = graph.getNodeId(43.72842465479131,7.414896579419745);
		Long target = graph.getNodeId(43.7354373276704,7.4212202598427295);
		
		//REGULAR DIJKSTRA
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graph);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graph.getNode(source), graph.getNode(target));

		regularDijkstraSW.stop();

		assertEquals(expectedDistance, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR DIJKSTRA] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
		
		System.out.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
		
		
		
		//BIDIRECTIONAL DIJKSTRA
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(source), graph.getNode(target));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
		
		assertEquals(expectedDistance, bidirectionalDijkstraFinalPath.getTotalDistance());
		
		System.out.println("[BIDIRECTIONAL DIJKSTRA] Number of settle nodes: " + (bidirectionalDijkstra.getNumberOfForwardSettleNodes() + bidirectionalDijkstra.getNumberOfBackwardSettleNodes()));
		
		
		
		//BIDIRECTIONAL A*
		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graph);
		
		StopWatch bidirectionalAStarSW = new StopWatch();
		
		bidirectionalAStarSW.start();
		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graph.getNode(source), graph.getNode(target));
		bidirectionalAStarSW.stop();
		
		logger.info("[BIDIRECTIONAL A*] Execution Time: {}ms", bidirectionalAStarSW.getNanos());
	
		logger.info("[BIDIRECTIONAL A*] Shortest Path");
		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
		
		System.out.println("[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
		
		assertEquals(expectedDistance, bidirectionalAStarFinalPath.getTotalDistance());
		
	}
	
//	@Test
//	public void seattleTest() {
//		
//		CHGraph graph = graphSeattle;
//		int expectedDistance = 12724821;
//		
//		Long source = graph.getNodeId(47.64660926863423,-122.37489515635882);
//		Long target = graph.getNodeId(47.5556199823831,-122.31052623638197);
//		
//		//REGULAR DIJKSTRA
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graph);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graph.getNode(source), graph.getNode(target));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(expectedDistance, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR DIJKSTRA] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath);
//		
//		System.out.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//		
//		
//		
//		//BIDIRECTIONAL DIJKSTRA
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(source), graph.getNode(target));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
//		logger.info("\t{}", bidirectionalDijkstraFinalPath);
//		
//		assertEquals(expectedDistance, bidirectionalDijkstraFinalPath.getTotalDistance());
//		
//		System.out.println("[BIDIRECTIONAL DIJKSTRA] Number of settle nodes: " + (bidirectionalDijkstra.getNumberOfForwardSettleNodes() + bidirectionalDijkstra.getNumberOfBackwardSettleNodes()));
//		
//		
//		
//		//BIDIRECTIONAL A*
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graph);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graph.getNode(source), graph.getNode(target));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL A*] Execution Time: {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL A*] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath);
//		
//		System.out.println("[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//		
//		assertEquals(expectedDistance, bidirectionalAStarFinalPath.getTotalDistance());
//		
//	}
	
//	@Test
//	public void greeceTest() {
//		
//		Long source = graphGreece.getNodeId(37.953927,23.754716);
//		Long target = graphGreece.getNodeId(40.672339,22.921128);
//		
//		//REGULAR DIJKSTRA
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graphGreece);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//		
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graphGreece.getNode(source), graphGreece.getNode(target));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(445880584, regularDijkstraFinalPath.getTotalDistance());
//		
//		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
//		
//		logger.info("[REGULAR DIJKSTRA] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath.getInstructions());
//		
//		System.out.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//		
//		
//		
//		//BIDIRECTIONAL DIJKSTRA
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphGreece);
//		
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//		
//		bidirectionalDijkstraSW.start();
//		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphGreece.getNode(source), graphGreece.getNode(target));
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
//		logger.info("\t{}", bidirectionalDijkstraFinalPath.getInstructions());
//		
//		assertEquals(445880584, bidirectionalDijkstraFinalPath.getTotalDistance());
//		
//		System.out.println("[BIDIRECTIONAL DIJKSTRA] Number of settle nodes: " + (bidirectionalDijkstra.getNumberOfForwardSettleNodes() + bidirectionalDijkstra.getNumberOfBackwardSettleNodes()));
//		
//		
//		
//		//BIDIRECTIONAL A*
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graphGreece);
//		
//		StopWatch bidirectionalAStarSW = new StopWatch();
//		
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graphGreece.getNode(source), graphGreece.getNode(target));
//		bidirectionalAStarSW.stop();
//		
//		logger.info("[BIDIRECTIONAL A*] Execution Time: {}ms", bidirectionalAStarSW.getNanos());
//	
//		logger.info("[BIDIRECTIONAL A*] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath.getInstructions());
//		
//		System.out.println("[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//		
//		assertEquals(445880584, bidirectionalAStarFinalPath.getTotalDistance());
//		
//	}
	
//	@Test
//	public void tokyoBiggerTest() {
//
//		CHGraph graph = graphTokyoBigger;
//		int expectedDistance = 8754003;
//
//		// Machida
//		Long source = graphTokyoBigger.getNodeId(35.70976504779827, 139.62002671405733);
//		// Tokorozawa
//		Long target = graphTokyoBigger.getNodeId(35.77867435169237, 139.62009637698748);
//
//		// REGULAR DIJKSTRA
//		Dijkstra regularDijkstra = new DijkstraConstantWeight(graph);
//
//		StopWatch regularDijkstraSW = new StopWatch();
//
//		regularDijkstraSW.start();
//
//		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graph.getNode(source), graph.getNode(target));
//
//		regularDijkstraSW.stop();
//
//		assertEquals(expectedDistance, regularDijkstraFinalPath.getTotalDistance());
//
//		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
//
//		logger.info("[REGULAR DIJKSTRA] Shortest Path");
//		logger.info("\t{}", regularDijkstraFinalPath);
//
//		System.out
//				.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
//
//		// BIDIRECTIONAL DIJKSTRA
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
//
//		StopWatch bidirectionalDijkstraSW = new StopWatch();
//
//		bidirectionalDijkstraSW.start();
//		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(source),
//				graph.getNode(target));
//		bidirectionalDijkstraSW.stop();
//
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
//
//		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
//		logger.info("\t{}", bidirectionalDijkstraFinalPath);
//
//		assertEquals(expectedDistance, bidirectionalDijkstraFinalPath.getTotalDistance());
//
//		System.out.println("[BIDIRECTIONAL DIJKSTRA] Number of settle nodes: "
//				+ (bidirectionalDijkstra.getNumberOfForwardSettleNodes()
//						+ bidirectionalDijkstra.getNumberOfBackwardSettleNodes()));
//
//		// BIDIRECTIONAL A*
//		BidirectionalAStarCH bidirectionalAStar = new BidirectionalAStarCH(graph);
//
//		StopWatch bidirectionalAStarSW = new StopWatch();
//
//		bidirectionalAStarSW.start();
//		Path bidirectionalAStarFinalPath = bidirectionalAStar.execute(graph.getNode(source), graph.getNode(target));
//		bidirectionalAStarSW.stop();
//
//		logger.info("[BIDIRECTIONAL A*] Execution Time: {}ms", bidirectionalAStarSW.getNanos());
//
//		logger.info("[BIDIRECTIONAL A*] Shortest Path");
//		logger.info("\t{}", bidirectionalAStarFinalPath);
//
//		System.out.println(
//				"[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes()
//						+ bidirectionalAStar.getNumberOfBackwardSettleNodes()));
//
//		assertEquals(expectedDistance, bidirectionalAStarFinalPath.getTotalDistance());
//
//	}

}
