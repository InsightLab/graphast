package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;
import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.OSMToGraphHopperReader;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.LevelGraphStorage;
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.storage.index.LocationIndexTreeSC;
import com.graphhopper.storage.index.QueryResult;

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
//		 graphMonaco.prepareNodes();
//		 graphMonaco.contractNodes();

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

	// @Test
	// public void monacoTest() {
	//
	// CHGraph testGraph = graphMonaco;
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
//	public void shortestPathTest() {
//
//		// String osmFile = Configuration.USER_HOME +
//		// "/graphhopper/osm/monaco-latest.osm.pbf";
//		// String graphDir = Configuration.USER_HOME +
//		// "/graphhopper/osm/monaco";
//		//
//		// GraphHopper hopper = OSMToGraphHopperReader.createGraph(osmFile,
//		// graphDir, true, false);
//		//
//		// LevelGraphStorage graphStorage = (LevelGraphStorage)
//		// hopper.getGraph();
//		//
//		// LocationIndexTreeSC index = new LocationIndexTreeSC(graphStorage, new
//		// RAMDirectory(graphDir, true));
//		// if (!index.loadExisting()) {
//		// index.prepareIndex();
//		// }
//
//		CHGraph testGraph = graphMonaco;
//		for (int source = 0; source < testGraph.getNumberOfNodes(); source++) {
//			System.out.println("Source: " + source);
//			for (int destination = 0; destination < testGraph.getNumberOfNodes(); destination++) {
//
//				// QueryResult fromQR =
//				// index.findClosest(testGraph.getNode(source).getLatitude(),
//				// testGraph.getNode(source).getLongitude(),
//				// EdgeFilter.ALL_EDGES);
//				// int closestNodeSource = fromQR.getClosestNode();
//				// QueryResult toQR =
//				// index.findClosest(testGraph.getNode(destination).getLatitude(),
//				// testGraph.getNode(destination).getLongitude(),
//				// EdgeFilter.ALL_EDGES);
//				// int closestNodeDestination = toQR.getClosestNode();
//				//
//				// GHRequest req = new
//				// GHRequest(graphStorage.getNodeAccess().getLat(closestNodeSource),
//				// graphStorage.getNodeAccess().getLon(closestNodeSource),
//				// graphStorage.getNodeAccess().getLat(closestNodeDestination),
//				// graphStorage.getNodeAccess().getLon(closestNodeDestination)).setVehicle("car")
//				// .setAlgorithm("dijkstrabi");
//				// GHResponse res = hopper.route(req);
//				//
//				// int distanceGraphhopper = (int) Math.round(res.getDistance()
//				// * 1000);
//
//				Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
//				Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));
//
//				BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(testGraph);
//				Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source),
//						testGraph.getNode(destination));
//
//				if (finalPath.getTotalCost() != dijkstraPath.getTotalDistance()) {
//					System.out
//							.println("Different results between source " + source + " and destination " + destination);
//					System.out.println("\tSource: " + testGraph.getNode(source).getLatitude() + ","
//							+ testGraph.getNode(source).getLongitude());
//					System.out.println("\tDestination: " + testGraph.getNode(destination).getLatitude() + ","
//							+ testGraph.getNode(destination).getLongitude());
//					System.out.println("Graphast distance: " + dijkstraPath.getTotalDistance()
//							+ ", Graphhopper distance: " + finalPath.getTotalDistance());
//				}
//
//				// assertEquals(dijkstraPath.getTotalDistance(),
//				// finalPath.getTotalDistance());
//			}
//		}
//	}

	@Test
	public void shortestPathUnitaryTest() {

		CHGraph testGraph = graphMonaco;
		Long source = 0l;
		Long target = 12l;

		Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
		Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(target));

		for (Long id : dijkstraPath.getEdges()) {
			System.out.println(
					"FROM: " + testGraph.getEdge(id).getFromNode() + ", TO: " + testGraph.getEdge(id).getToNode());
		}

		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(testGraph);
		Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source), testGraph.getNode(target));

		if (finalPath.getTotalCost() != dijkstraPath.getTotalDistance()) {
			System.out.println("Different results between source " + source + " and destination " + target);
			System.out.println("\tSource: " + testGraph.getNode(source).getLatitude() + ","
					+ testGraph.getNode(source).getLongitude());
			System.out.println("\tDestination: " + testGraph.getNode(target).getLatitude() + ","
					+ testGraph.getNode(target).getLongitude());
			System.out.println("Regular Dijkstra distance: " + dijkstraPath.getTotalDistance()
					+ ", Bidirectional Dijkstra distance: " + finalPath.getTotalDistance());
		}

	}

//	@Test
//	public void shortestPathUnitaryGraphhopperTest() {
//
//		CHGraph testGraph = graphMonaco;
//
//		String osmFile = Configuration.USER_HOME + "/graphhopper/osm/monaco-latest.osm.pbf";
//		String graphDir = Configuration.USER_HOME + "/graphhopper/osm/monacoCH";
//
//		GraphHopper hopper = OSMToGraphHopperReader.createGraph(osmFile, graphDir, true, false);
//
//		LevelGraphStorage graphStorage = (LevelGraphStorage) hopper.getGraph();
//
//		LocationIndexTreeSC index = new LocationIndexTreeSC(graphStorage, new RAMDirectory(graphDir, true));
//		if (!index.loadExisting()) {
//			index.prepareIndex();
//		}
//
//		int source = 0;
//		int destination = 12;
//
//		QueryResult fromQR = index.findClosest(testGraph.getNode(source).getLatitude(),
//				testGraph.getNode(source).getLongitude(), EdgeFilter.ALL_EDGES);
//		int closestNodeSource = fromQR.getClosestNode();
//		QueryResult toQR = index.findClosest(testGraph.getNode(destination).getLatitude(),
//				testGraph.getNode(destination).getLongitude(), EdgeFilter.ALL_EDGES);
//		int closestNodeDestination = toQR.getClosestNode();
//
//		GHRequest req = new GHRequest(graphStorage.getNodeAccess().getLat(closestNodeSource),
//				graphStorage.getNodeAccess().getLon(closestNodeSource),
//				graphStorage.getNodeAccess().getLat(closestNodeDestination),
//				graphStorage.getNodeAccess().getLon(closestNodeDestination)).setVehicle("car")
//						.setAlgorithm("dijkstrabi");
//		GHResponse res = hopper.route(req);
//
//		int distanceGraphhopper = (int) Math.round(res.getDistance() * 1000);
//
//		Dijkstra dijkstra = new DijkstraConstantWeight(testGraph);
//		Path dijkstraPath = dijkstra.shortestPath(testGraph.getNode(source), testGraph.getNode(destination));
//
////		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(testGraph);
////		Path finalPath = bidirectionalDijkstra.execute(testGraph.getNode(source), testGraph.getNode(destination));
//
//		if (distanceGraphhopper != dijkstraPath.getTotalDistance()) {
//			System.out.println("Different results between source " + source + " and destination " + destination);
//			System.out.println("\tSource: " + testGraph.getNode(source).getLatitude() + ","
//					+ testGraph.getNode(source).getLongitude());
//			System.out.println("\tDestination: " + testGraph.getNode(destination).getLatitude() + ","
//					+ testGraph.getNode(destination).getLongitude());
//			System.out.println("Graphast distance: " + dijkstraPath.getTotalDistance() + ", Graphhopper distance: "
//					+ distanceGraphhopper);
//		}
//
//		// assertEquals(dijkstraPath.getTotalDistance(),
//		// finalPath.getTotalDistance());
//
//	}

}
