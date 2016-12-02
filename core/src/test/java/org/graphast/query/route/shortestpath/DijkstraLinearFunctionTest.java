package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.junit.BeforeClass;

/**
 * Class that implements AbstractShortestPathTest idea with Dijkstra shortestPath algorithm.
 * It's a simple setupService before all in AbstractShortestPathLinearFunctionTest that instantiate the services.
 */
public class DijkstraLinearFunctionTest extends AbstractShortestPathLinearFunctionTest {
	
	
	@BeforeClass
	public static void setupService(){
		serviceExample4 = new DijkstraLinearFunction(graphExample4);
		serviceExample4Bounds = new DijkstraLinearFunction(graphBounds);
//		serviceMonaco = new DijkstraLinearFunction(graphMonaco);
//		serviceSeattle = new DijkstraLinearFunction(graphSeattle);
	}	
}
