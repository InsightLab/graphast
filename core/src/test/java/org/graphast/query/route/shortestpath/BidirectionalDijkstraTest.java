package org.graphast.query.route.shortestpath;

import org.graphast.graphgenerator.GraphGenerator;
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
	
	private static CHGraph graphMITExample;
	private static CHGraph graphMITExample2;
	
	@BeforeClass
	public static void setup() {

		graphMITExample = new GraphGenerator().generateMITExample();
		graphMITExample2 = new GraphGenerator().generateMITExample2();
		
	}
	
	/*
	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	@Test
	public void graphMITTest() {

		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMITExample);

		StopWatch bidirectionalDijkstraSW = new StopWatch();
		
		bidirectionalDijkstraSW.start();

		Path finalPath = bidirectionalDijkstra.execute(graphMITExample.getNode(0), graphMITExample.getNode(4));

		bidirectionalDijkstraSW.stop();
		
		logger.info("Execution Time of graphMITTest(): {}ms", bidirectionalDijkstraSW.getNanos());

	}
	
	@Test
	public void graphMITRegularDijkstraTest() {

		Dijkstra bidirectionalDijkstra = new DijkstraConstantWeight(graphMITExample);

		StopWatch dijkstraSW = new StopWatch();
		
		dijkstraSW.start();

		Path finalPath = bidirectionalDijkstra.shortestPath(graphMITExample.getNode(0), graphMITExample.getNode(4));

		dijkstraSW.stop();
		
		logger.info("Execution Time of graphMITRegularDijkstraTest(): {}ms", dijkstraSW.getNanos());

	}
	
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
//		Path finalPath = bidirectionalDijkstra.execute(graphMITExample2.getNode(0), graphMITExample2.getNode(8));
//
//		bidirectionalDijkstraSW.stop();
//		
//		logger.info("Execution Time of graphMITExample2Test(): {}ms", bidirectionalDijkstraSW.getNanos());
//
//	}
//	
//	@Test
//	public void graphMITRegularDijkstraExample2Test() {
//
//		Dijkstra bidirectionalDijkstra = new DijkstraConstantWeight(graphMITExample2);
//
//		StopWatch dijkstraSW = new StopWatch();
//		
//		dijkstraSW.start();
//
//		Path finalPath = bidirectionalDijkstra.shortestPath(graphMITExample2.getNode(0), graphMITExample2.getNode(8));
//
//		dijkstraSW.stop();
//		
//		logger.info("Execution Time of graphMITRegularDijkstraExample2Test(): {}ms", dijkstraSW.getNanos());
//
//	}
	
}
