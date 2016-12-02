package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.astar.AStarLinearFunction;
import org.junit.BeforeClass;

/**
 * Class that implements AbstractShortestPathTest idea with AStar shortestPath algorithm.
 * It's a simple setupService before all in AbstractShortestPathLinearFunctionTest that instantiate the services.
 */
public class AStarLinearFunctionTest extends AbstractShortestPathLinearFunctionTest {
	
	@BeforeClass
	public static void setupService(){
		serviceExample4 = new AStarLinearFunction(graphExample4);
		serviceExample4Bounds = new AStarLinearFunction(graphBounds);
//		serviceMonaco = new DijkstraLinearFunction(graphMonaco);
//		serviceSeattle = new DijkstraLinearFunction(graphSeattle);
	}
}
