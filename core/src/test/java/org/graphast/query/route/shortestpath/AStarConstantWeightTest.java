package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.astar.AStarConstantWeight;
import org.junit.BeforeClass;

/**
 * Class that implements AbstractShortestPathTest idea with AStar shortestPath algorithm.
 * It's a simple setupService before all in AbstractShortestPathTest that instantiate the services.
 */
public class AStarConstantWeightTest extends AbstractShortestPathTest{
	
	@BeforeClass
	public static void setupService(){
		
		serviceMonaco = new AStarConstantWeight(graphMonaco);
//		serviceSeattle = new AStarConstantWeight(graphSeattle);
		serviceExample = new AStarConstantWeight(graphExample);
	
	}

}