package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.bidirectionalastar.BidirectionalAStar;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidirectionalAStarTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static CHGraph graphHopperExample4;


	@BeforeClass
	public static void setup() {

		graphHopperExample4 = new GraphGenerator().generateGraphHopperExample4();

	}

	@Test
	public void graphHopperExample4Test() {

		CHGraph testGraph = graphHopperExample4;

		for (int source = 0; source < testGraph.getNumberOfNodes(); source++) {
			for (int destination = 0; destination < testGraph.getNumberOfNodes(); destination++) {

				logger.info("SOURCE: {}, DESTINATION: {}.", source, destination);

				Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
				Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));

				BidirectionalAStar bidirectionalDijkstra = new BidirectionalAStar(testGraph);
				Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source), testGraph.getNode(destination));
				
				assertEquals(dijkstraPath.getTotalCost(), finalPath.getTotalCost(), 1);

			}
		}
	}

}
