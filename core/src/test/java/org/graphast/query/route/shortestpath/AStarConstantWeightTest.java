package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.astar.AStarConstantWeight;
import org.junit.BeforeClass;

public class AStarConstantWeightTest extends AbstractShortestPathTest{
	
	@BeforeClass
	public static void setupService(){
		
		serviceMonaco = new AStarConstantWeight(graphMonaco);
//		serviceSeattle = new AStarConstantWeight(graphSeattle);
		serviceExample = new AStarConstantWeight(graphExample);
	
	}

}