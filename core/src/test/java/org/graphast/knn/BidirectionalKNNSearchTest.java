package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.POIImporter;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knn.BidirectionalKNNSearch;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidirectionalKNNSearchTest {

	private static Logger logger = LoggerFactory.getLogger(BidirectionalKNNSearchTest.class);

	private CHGraph graphMonacoWithPoI;
	private static CHGraph graphHopperExample4WithPoIs;

	@Before
	public void setup() {

//		graphMonacoWithPoI = new GraphGenerator().generateMonacoCHWithPoI();
//		logger.info("Starting to generate PoI'S");
//		POIImporter.generateRandomPoIs(graphMonacoWithPoI, 25);
//		logger.info("Finishing PoI's generation.");

		graphHopperExample4WithPoIs = new GraphGenerator().generateGraphHopperExample4();
//		graphHopperExample4WithPoIs.prepareNodes();
//		graphHopperExample4WithPoIs.contractNodes();
		logger.info("Starting to generate PoI'S");
		POIImporter.generateRandomPoIs(graphHopperExample4WithPoIs, 100);
		logger.info("Finishing PoI's generation.");

	}

	@Test
	public void graphHopperExample4WithPoIsTest() {

		int k = graphHopperExample4WithPoIs.getPOIs().size();
		
		for (Long source = 0l; source < graphHopperExample4WithPoIs.getNumberOfNodes(); source++) {
//			 Long source = 20l;

			 if(source.equals(33l))
				 continue;
			 
			logger.info("SOURCE = {}", source);
			
			Queue<Path> finalResult = new PriorityQueue<>();

			BidirectionalKNNSearch knn = new BidirectionalKNNSearch(graphHopperExample4WithPoIs);

			finalResult = knn.search(graphHopperExample4WithPoIs.getNode(source), k);

			int size = finalResult.size();

			for (int i = 1; i <= size; i++) {
				Path poi = finalResult.poll();
				logger.info("\tk = {}", i);

				Long destination;
				
				Dijkstra dj = new DijkstraConstantWeight(graphHopperExample4WithPoIs);

				if (poi.getEdges().size() == 0) {
					logger.info("\t\tPoI: {}, Distance = {}", graphHopperExample4WithPoIs.getNode(poi.getInstructions().get(0).getDirection()).getExternalId(),
							poi.getTotalCost());
					destination = (long) poi.getInstructions().get(0).getDirection();

				} else {
					logger.info(
							"\t\tPoI: {}, Distance = {}", graphHopperExample4WithPoIs.getNode(graphHopperExample4WithPoIs.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode()).getExternalId(),
							poi.getTotalCost());
					destination = graphHopperExample4WithPoIs.getEdge(poi.getEdges().get(poi.getEdges().size() - 1))
							.getToNode();

				}

				assertEquals(
						dj.shortestPath(graphHopperExample4WithPoIs.getNode(source),
								graphHopperExample4WithPoIs.getNode(destination)).getTotalDistance(),
						poi.getTotalCost(), 0);

			}
		}
	}

//	@Test
//	public void graphMonacoWithPoITest() {
//
//		int k = graphMonacoWithPoI.getPOIs().size();
//		
////		int k = 8;
//		
////		for (Long source = 0l; source < graphMonacoWithPoI.getNumberOfNodes(); source++) {
//
//			Long source = graphMonacoWithPoI.getNodeId(43.72842465479131, 7.414896579419745);
//			 
//			logger.info("SOURCE = {}", source);
//			
//			Queue<Path> finalResult = new PriorityQueue<>();
//
//			BidirectionalKNNSearch knn = new BidirectionalKNNSearch(graphMonacoWithPoI);
//
//			finalResult = knn.search(graphMonacoWithPoI.getNode(source), k);
//
//			int size = finalResult.size();
//
//			for (int i = 1; i <= size; i++) {
//				Path poi = finalResult.poll();
//				logger.info("\tk = {}", i);
//
//				Long destination;
//				
//				Dijkstra dj = new DijkstraConstantWeight(graphMonacoWithPoI);
//
//				if (poi.getEdges().size() == 0) {
//					logger.info("\t\tPoI: {}, Distance = {}", graphMonacoWithPoI.getNode(poi.getInstructions().get(0).getDirection()).getExternalId(),
//							poi.getTotalCost());
//					destination = (long) poi.getInstructions().get(0).getDirection();
//
//				} else {
//					logger.info(
//							"\t\tPoI: {}, Distance = {}", graphMonacoWithPoI.getNode(graphMonacoWithPoI.getEdge(poi.getEdges().get(poi.getEdges().size() - 1)).getToNode()).getExternalId(),
//							poi.getTotalCost());
//					destination = graphMonacoWithPoI.getEdge(poi.getEdges().get(poi.getEdges().size() - 1))
//							.getToNode();
//
//				}
//
//				assertEquals(
//						dj.shortestPath(graphMonacoWithPoI.getNode(source),
//								graphMonacoWithPoI.getNode(destination)).getTotalDistance(),
//						poi.getTotalCost(), 0);
//
//			}
////		}
//	}

//	@Test
//	public void shortestPathTest() {
//
//		Long source = 552l;
//		Long destination = 539l;
//
//		Dijkstra dijkstra = new DijkstraConstantWeight(graphMonacoWithPoI);
//		Path dijkstraPath = dijkstra.shortestPath(graphMonacoWithPoI.getNode(source),
//				graphMonacoWithPoI.getNode(destination));
//
//		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMonacoWithPoI);
//		Path finalPath = bidirectionalDijkstra.execute(graphMonacoWithPoI.getNode(source),
//				graphMonacoWithPoI.getNode(destination));
//
//		assertEquals(dijkstraPath.getTotalDistance(), finalPath.getTotalDistance());
//
//		source = 539l;
//		destination = 552l;
//
//		dijkstra = new DijkstraConstantWeight(graphMonacoWithPoI);
//		dijkstraPath = dijkstra.shortestPath(graphMonacoWithPoI.getNode(source),
//				graphMonacoWithPoI.getNode(destination));
//
//		bidirectionalDijkstra = new BidirectionalDijkstraCH(graphMonacoWithPoI);
//		finalPath = bidirectionalDijkstra.execute(graphMonacoWithPoI.getNode(source),
//				graphMonacoWithPoI.getNode(destination));
//
//		assertEquals(dijkstraPath.getTotalDistance(), finalPath.getTotalDistance());
//
//	}

	private void manuallySetPoIs() {

		Node n = graphMonacoWithPoI.getNode(87l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

		n = graphMonacoWithPoI.getNode(621l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

		n = graphMonacoWithPoI.getNode(355l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

		n = graphMonacoWithPoI.getNode(524l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

		n = graphMonacoWithPoI.getNode(238l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

		n = graphMonacoWithPoI.getNode(403l);
		n.setCategory(4);
		graphMonacoWithPoI.updateNodeInfo(n);

	}

}
