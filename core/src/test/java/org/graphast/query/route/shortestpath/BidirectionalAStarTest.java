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

	private static CHGraph graphMonaco;
	private static CHGraph graphSeattle;
//	private static CHGraph graphGreece;
//	private static CHGraph graphTokyoBigger;
//	private static CHGraph graphSpain;

	@BeforeClass
	public static void setup() {

		graphMonaco = new GraphGenerator().generateMonacoCH();
		graphSeattle = new GraphGenerator().generateSeattleCH();
//		graphGreece = new GraphGenerator().generateGreeceCH();
//		graphTokyoBigger = new GraphGenerator().generateTokyoBiggerCH();
//		graphSpain = new GraphGenerator().generateSpainCH();

	}

	@Test
	public void monacoTest() {
		
		CHGraph graph = graphMonaco;
		int expectedDistance = 1136090;
		
		System.out.println("#numberOfNodes: " + graph.getNumberOfNodes());
		System.out.println("#numberOfEdges: " + graph.getNumberOfEdges());
		
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
	
	@Test
	public void seattleTest() {
		
		CHGraph graph = graphSeattle;
		int expectedDistance = 12724821;
		
		System.out.println("#numberOfNodes: " + graph.getNumberOfNodes());
		System.out.println("#numberOfEdges: " + graph.getNumberOfEdges());
		
		Long source = graph.getNodeId(47.64660926863423,-122.37489515635882);
		Long target = graph.getNodeId(47.5556199823831,-122.31052623638197);
		
		//REGULAR DIJKSTRA
		Dijkstra regularDijkstra = new DijkstraConstantWeight(graph);

		StopWatch regularDijkstraSW = new StopWatch();
		
		regularDijkstraSW.start();

		Path regularDijkstraFinalPath = regularDijkstra.shortestPath(graph.getNode(source), graph.getNode(target));

		regularDijkstraSW.stop();

		assertEquals(expectedDistance, regularDijkstraFinalPath.getTotalDistance());
		
		logger.info("[REGULAR DIJKSTRA] Execution Time: {}ms", regularDijkstraSW.getNanos());
		
		logger.info("[REGULAR DIJKSTRA] Shortest Path");
		logger.info("\t{}", regularDijkstraFinalPath);
		
		System.out.println("[REGULAR DIJKSTRA] Number of settle nodes: " + regularDijkstra.getNumberOfTotalSettleNodes());
		
		
		
		//BIDIRECTIONAL DIJKSTRA
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
		
		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(source), graph.getNode(target));
		bidirectionalDijkstraSW.stop();
		
		logger.info("[BIDIRECTIONAL DIJKSTRA] Execution Time: {}ms", bidirectionalDijkstraSW.getNanos());
	
		logger.info("[BIDIRECTIONAL DIJKSTRA] Shortest Path");
		logger.info("\t{}", bidirectionalDijkstraFinalPath);
		
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
		logger.info("\t{}", bidirectionalAStarFinalPath);
		
		System.out.println("[BIDIRECTIONAL A*] Number of settle nodes: " + (bidirectionalAStar.getNumberOfForwardSettleNodes() + bidirectionalAStar.getNumberOfBackwardSettleNodes()));
		
		assertEquals(expectedDistance, bidirectionalAStarFinalPath.getTotalDistance());
		
	}
	
//	@Test
//	public void greeceTest() {
//		
//		CHGraph graph = graphGreece;
//		int expectedDistance = 445880580;
//	
//		System.out.println("#numberOfNodes: " + graph.getNumberOfNodes());
//		System.out.println("#numberOfEdges: " + graph.getNumberOfEdges());
//		
//		Long source = graph.getNodeId(37.95387233690632,23.755026767142716);
//		Long target = graph.getNodeId(40.67261198176321,22.92112442674766);
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
//	public void tokyoBiggerTest() {
//
//		CHGraph graph = graphTokyoBigger;
//		int expectedDistance = 8138083;
//		
//		System.out.println("#numberOfNodes: " + graph.getNumberOfNodes());
//		System.out.println("#numberOfEdges: " + graph.getNumberOfEdges());
//
//		// Machida
//		Long source = graphTokyoBigger.getNodeId(35.709955782665816, 139.61964822455454);
//		// Tokorozawa
//		Long target = graphTokyoBigger.getNodeId(35.778598169504065, 139.62021838024748);
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
	
//	@Test
//	public void spainTest() {
//
//		CHGraph graph = graphSpain;
//		int expectedDistance = 493403528;
//		
//		System.out.println("#numberOfNodes: " + graph.getNumberOfNodes());
//		System.out.println("#numberOfEdges: " + graph.getNumberOfEdges());
//
//		// Machida
//		Long source = graph.getNodeId(40.41734912434256,-3.705363244683219);
//		// Tokorozawa
//		Long target = graph.getNodeId(36.72846954454041,-4.423437552677934);
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
