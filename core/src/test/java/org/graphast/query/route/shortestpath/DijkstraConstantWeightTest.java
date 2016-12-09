package org.graphast.query.route.shortestpath;

import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.junit.BeforeClass;

/**
 * Class that implements AbstractShortestPathTest idea with Dijkstra shortestPath algorithm.
 * It's a simple setupService before all in AbstractShortestPathTest that instantiate the services.
 */
public class DijkstraConstantWeightTest extends AbstractShortestPathTest {

	@BeforeClass
	public static void setupService(){
		
		serviceMonaco = new DijkstraConstantWeight(graphMonaco);
//		serviceSeattle = new DijkstraConstantWeight(graphSeattle);
		serviceExample = new DijkstraConstantWeight(graphExample);
	}

}