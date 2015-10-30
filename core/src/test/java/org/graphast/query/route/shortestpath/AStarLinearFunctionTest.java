package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.astar.AStarLinearFunction;
import org.junit.BeforeClass;

public class AStarLinearFunctionTest extends AbstractShortestPathLinearFunctionTest {
	
	@BeforeClass
	public static void setupService(){
		serviceExample4 = new AStarLinearFunction(graphExample4);
		serviceExample4Bounds = new AStarLinearFunction(graphBounds);
//		serviceMonaco = new DijkstraLinearFunction(graphMonaco);
//		serviceSeattle = new DijkstraLinearFunction(graphSeattle);
	}
}
