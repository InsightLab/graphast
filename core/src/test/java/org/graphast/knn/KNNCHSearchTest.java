package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knnch.lowerbounds.KNNCHSearch;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class KNNCHSearchTest {

	private static Logger logger = LoggerFactory.getLogger(KNNCHSearchTest.class);

	private static CHGraph graphMonacoWithPoI;
	// private static CHGraph graphHopperExampleWithPoIs;
	// private static CHGraph graphHopperExample2WithPoIs;
	// private static CHGraph graphHopperExample3WithPoIs;
	private static CHGraph graphHopperExample4WithPoIs;

	@BeforeClass
	public static void setup() {

		StopWatch preprocessingSW = new StopWatch();
		preprocessingSW.start();
		graphMonacoWithPoI = new GraphGenerator().generateMonacoCHWithPoI();
		graphMonacoWithPoI.prepareNodes();
		graphMonacoWithPoI.contractNodes();
		preprocessingSW.stop();
		logger.info("preprocessingTime = {} seconds", preprocessingSW.getSeconds());

		logger.info("Starting to generate PoI'S");
		POIImporter.generateRandomPoIs(graphMonacoWithPoI, 100);
		logger.info("Finishing PoI's generation.");

		// graphHopperExampleWithPoIs = new
		// GraphGenerator().generateGraphHopperExampleWithPoIs();
		// graphHopperExampleWithPoIs.prepareNodes();
		// graphHopperExampleWithPoIs.contractNodes();
		//
		// graphHopperExample2WithPoIs = new
		// GraphGenerator().generateGraphHopperExample2WithPoIs();
		// graphHopperExample2WithPoIs.prepareNodes();
		// graphHopperExample2WithPoIs.contractNodes();
		//
		// graphHopperExample3WithPoIs = new
		// GraphGenerator().generateGraphHopperExample3WithPoIs();
		// graphHopperExample3WithPoIs.prepareNodes();
		// graphHopperExample3WithPoIs.contractNodes();

//		 graphHopperExample4WithPoIs = new
//		 GraphGenerator().generateGraphHopperExample4WithPoIs();
//		 graphHopperExample4WithPoIs.prepareNodes();
//		 graphHopperExample4WithPoIs.contractNodes();
//		 logger.info("Starting to generate PoI'S");
//		 POIImporter.generateRandomPoIs(graphHopperExample4WithPoIs, 100);
//		 logger.info("Finishing PoI's generation.");

		// StdDraw.drawGraph(graphHopperExample4WithPoIs);

	}

	// @Test
	// public void graphHopperExampleWithPoIsTest() {
	//
	// Long source = 3l;
	//
	// KNNCHSearchBaseline knnBaseline = new
	// KNNCHSearchBaseline(graphHopperExampleWithPoIs);
	// knnBaseline.search(graphHopperExampleWithPoIs.getNode(source), 2);
	//
	// KNNCHSearch knn = new KNNCHSearch(graphHopperExampleWithPoIs);
	// knn.search(graphHopperExampleWithPoIs.getNode(source), 2);
	//
	// }

	// @Test
	// public void graphHopperExample2WithPoIsTest() {
	//
	// Long source = 3l;
	//
	// KNNCHSearchBaseline knnBaseline = new
	// KNNCHSearchBaseline(graphHopperExample2WithPoIs);
	// knnBaseline.search(graphHopperExample2WithPoIs.getNode(source), 2);
	//
	// KNNCHSearch knn = new KNNCHSearch(graphHopperExample2WithPoIs);
	// knn.search(graphHopperExample2WithPoIs.getNode(source), 2);
	//
	// }

	// @Test
	// public void graphHopperExample3WithPoIsTest() {
	//
	// Long source = 2l;
	//
	// KNNCHSearchBaseline knnBaseline = new
	// KNNCHSearchBaseline(graphHopperExample3WithPoIs);
	// knnBaseline.search(graphHopperExample3WithPoIs.getNode(source), 3);
	//
	// KNNCHSearch knn = new KNNCHSearch(graphHopperExample3WithPoIs);
	// knn.search(graphHopperExample3WithPoIs.getNode(source), 3);
	//
	// }

//	@Test
//	public void graphHopperExample4WithPoIsTest() {
//
//		Long source = 0l;
//
//		Queue<Path> finalResult = new PriorityQueue<>();
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample4WithPoIs);
//
//		finalResult = knn.search(graphHopperExample4WithPoIs.getNode(source), 32);
//
//		int size = finalResult.size();
//
//		for (int i = 1; i <= size; i++) {
//			Path poi = finalResult.poll();
//			logger.info("k = {}", i);
//
//			Long destination;
//
//			
//			if (poi.getEdges().size() == 0) {
//				logger.info("\tPoI: {}, Distance = {}", poi.getInstructions().get(0).getDirection(),
//						poi.getTotalDistance());
//				destination = 0l;
//
//			} else {
//				logger.info("\tPoI: {}, Distance = {}",
//						graphHopperExample4WithPoIs.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode(),
//						poi.getTotalDistance());
//				destination = graphHopperExample4WithPoIs.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode();
//
//			}
//			
//			Dijkstra dj = new DijkstraConstantWeight(graphHopperExample4WithPoIs);
//
//			
//			assertEquals(dj.shortestPath(graphHopperExample4WithPoIs.getNode(source), graphHopperExample4WithPoIs.getNode(destination)).getTotalDistance(),
//					poi.getTotalDistance());
//
//		}
//
//	}

	// @Test
	// public void graphHopperExample4WithPoIsExperiment() {
	//
	// CHGraph testGraph = graphHopperExample4WithPoIs;
	// Long source = 0l;
	// int numberOfRepetitions = 10;
	//
	// List<Integer> numberOfNeighbors = new ArrayList<>(Arrays.asList(1, 2, 4,
	// 8, 16, 32));
	//
	// for (Integer k : numberOfNeighbors) {
	// logger.info("Starting to run the first prunning method for kNN with CH. k
	// = {}", k);
	// int averageExecutionTime = 0;
	//
	// for (int i = 0; i < numberOfRepetitions; i++) {
	// StopWatch knnSW = new StopWatch();
	//
	// KNNCHSearch knn = new KNNCHSearch(testGraph);
	// knnSW.start();
	// knn.search(graphHopperExample4WithPoIs.getNode(source), k);
	// knnSW.stop();
	//
	// averageExecutionTime += knnSW.getNanos();
	// }
	//
	// averageExecutionTime = averageExecutionTime / numberOfRepetitions;
	// logger.info("averageExecutionTime = {}", averageExecutionTime);
	//
	// }
	//
	// }

	@Test
	public void graphMonacoWithPoITest() {
//		CHGraph testGraph = graphMonacoWithPoI;
		Long source = graphMonacoWithPoI.getNodeId(43.72842465479131, 7.414896579419745);

		Queue<Path> finalResult = new PriorityQueue<>();

		KNNCHSearch knn = new KNNCHSearch(graphMonacoWithPoI);

		finalResult = knn.search(graphMonacoWithPoI.getNode(source), 777);

		int size = finalResult.size();

		for (int i = 1; i <= size; i++) {
			Path poi = finalResult.poll();
			logger.info("k = {}", i);
			
			Long destination;

			if (poi.getEdges().size() == 0) {
				logger.info("\tPoI: {}, Distance = {}", poi.getInstructions().get(0).getDirection(),
						poi.getTotalDistance());
				destination = source;
			} else {
				logger.info("\tPoI: {}, Distance = {}",
						graphMonacoWithPoI.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode(),
						poi.getTotalDistance());
				
				destination = graphMonacoWithPoI.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode();
			}

			Dijkstra dj = new DijkstraConstantWeight(graphMonacoWithPoI);

			
			assertEquals(dj.shortestPath(graphMonacoWithPoI.getNode(source), graphMonacoWithPoI.getNode(destination)).getTotalDistance(),
					poi.getTotalDistance());
			
		}

	}

	// @Test
	// public void graphMonacoWithPoIsExperiment() {
	//
	// CHGraph testGraph = graphMonacoWithPoI;
	// Long source = testGraph.getNodeId(43.72842465479131, 7.414896579419745);
	// int numberOfRepetitions = 10;
	//
	// List<Integer> numberOfNeighbors = new ArrayList<>(Arrays.asList(1, 2, 4,
	// 8, 16, 32, 64, 128, 256, 512, 777));
	//
	// for (Integer k : numberOfNeighbors) {
	// logger.info("Starting to run the first prunning method for kNN with CH. k
	// = {}", k);
	// double averageExecutionTime = 0;
	//
	// for (int i = 0; i < numberOfRepetitions; i++) {
	// StopWatch knnSW = new StopWatch();
	//
	// KNNCHSearch knn = new KNNCHSearch(testGraph);
	// knnSW.start();
	// knn.search(testGraph.getNode(source), k);
	// knnSW.stop();
	//
	// averageExecutionTime += knnSW.getSeconds();
	// }
	//
	// averageExecutionTime = averageExecutionTime / numberOfRepetitions;
	// logger.info("averageExecutionTime = {} seconds", averageExecutionTime);
	//
	// }
	//
	// }

}
