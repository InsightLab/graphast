package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class BidirectionalDijkstraTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static CHGraph graphHopperExample;
	private static CHGraph graphHopperExample2;
	private static CHGraph graphHopperExample3;
	private static CHGraph graphHopperExample4;
	private static CHGraph graphMITExample;
	private static CHGraph graphMITExample2;
	private static CHGraph graphMITExample3;
	private static CHGraph graphMonaco;
	private static CHGraph graphTinyMonaco;
	private static CHGraph graphSeattle;
	private static CHGraph graphGreece;
	private static CHGraph graphGermany;
	private static CHGraph graphSpain;

	@BeforeClass
	public static void setup() {

		// graphHopperExample = new
		// GraphGenerator().generateGraphHopperExample();
		// graphHopperExample2 = new
		// GraphGenerator().generateGraphHopperExample2();
		// graphHopperExample3 = new
		// GraphGenerator().generateGraphHopperExample3();
		// graphHopperExample4 = new
		// GraphGenerator().generateGraphHopperExample4();
		// graphHopperExample4.prepareNodes();
		// graphHopperExample4.contractNodes();

		// graphMITExample = new GraphGenerator().generateMITExample();
		// graphMITExample2 = new GraphGenerator().generateMITExample2();
		// graphMITExample3 = new GraphGenerator().generateMITExample3();

		graphMonaco = new GraphGenerator().generateMonacoCH();
		graphMonaco.prepareNodes();
		graphMonaco.contractNodes();
		// graphTinyMonaco = new GraphGenerator().generateTinyMonacoCH();
		// graphSeattle = new GraphGenerator().generateSeattleCH();
		// graphGreece = new GraphGenerator().generateGreeceCH();
		// graphGermany = new GraphGenerator().generateGermanyCH();
		// graphSpain = new GraphGenerator().generateSpainCH();

	}

	// @Test
	// public void graphHopperExample4Test() {
	//
	// CHGraph testGraph = graphHopperExample4;
	//
	// for (int source = 0; source < testGraph.getNumberOfNodes(); source++) {
	// for (int destination = 0; destination < testGraph.getNumberOfNodes();
	// destination++) {
	//
	// logger.info("SOURCE: {}, DESTINATION: {}.", source, destination);
	//
	// Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
	// Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source),
	// testGraph.getNode(destination));
	//
	// BidirectionalDijkstraCH bidirectionalDijkstra = new
	// BidirectionalDijkstraCH(testGraph);
	// Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source),
	// testGraph.getNode(destination));
	//
	// assertEquals(dijkstraPath.getTotalDistance(),
	// finalPath.getTotalDistance());
	//
	// }
	// }
	// }

//	@Test
//	public void monacoTest() {
//
//		CHGraph testGraph = graphMonaco;
//
//		for (int source = 0; source < testGraph.getNumberOfNodes(); source++) {
//			for (int destination = 0; destination < testGraph.getNumberOfNodes(); destination++) {
//
//				logger.info("SOURCE: {}, DESTINATION: {}.", source, destination);
//
//				Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
//				Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));
//
//				BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(testGraph);
//				Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source),
//						testGraph.getNode(destination));
//
//				assertEquals(dijkstraPath.getTotalDistance(), finalPath.getTotalDistance());
//
//			}
//		}
//	}

	@Test
	public void shortestPathTest() {

		Long source = 0l;
		Long destination = 12l;

		CHGraph testGraph = graphMonaco;

		logger.info("SOURCE: {}, DESTINATION: {}.", source, destination);

		Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
		Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));

		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(testGraph);
		Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source), testGraph.getNode(destination));

		assertEquals(dijkstraPath.getTotalDistance(), finalPath.getTotalDistance());

	}

}
