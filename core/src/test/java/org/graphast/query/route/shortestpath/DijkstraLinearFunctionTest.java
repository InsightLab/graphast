package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.junit.BeforeClass;

public class DijkstraLinearFunctionTest extends AbstractShortestPathLinearFunctionTest {
	
	
	@BeforeClass
	public static void setupService(){
		serviceExample4 = new DijkstraLinearFunction(graphExample4);
		
	}
	
	
}
