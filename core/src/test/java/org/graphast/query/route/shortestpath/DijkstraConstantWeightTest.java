package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.junit.BeforeClass;

public class DijkstraConstantWeightTest extends AbstractShortestPathTest {

	@BeforeClass
	public static void setupService(){
		
		serviceMonaco = new DijkstraConstantWeight(graphMonaco);
		serviceExample = new DijkstraConstantWeight(graphExample);
	}

}
