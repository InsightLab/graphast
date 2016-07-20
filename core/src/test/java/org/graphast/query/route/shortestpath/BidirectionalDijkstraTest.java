package org.graphast.query.route.shortestpath;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
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
	
	@BeforeClass
	public static void setup() {

		graphMITExample = new GraphGenerator().generateMITExample();
		
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
	
}
