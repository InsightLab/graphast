package org.graphast.query.knnch.baseline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.geometry.PoI;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class KNNCHSearchBaseline {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CHGraph graph;

	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Map<Long, BidirectionalDijkstraCHBaselineIterator> dijkstraHash = new HashMap<>();

	private Queue<DistanceEntry> nearestNeighbors = new PriorityQueue<>();

	// TODO Criar metodos para adicionar pois na estrutura poisFound
	private Map<Long, Path> poisFound = new HashMap<>();
	Queue<DistanceEntry> result = new PriorityQueue<>();

	public KNNCHSearchBaseline(CHGraph graph) {
		this.graph = graph;
	}

//	public void search(CHNode source, int k) {
//
//		for (PoI poi : graph.getPOIs()) {
//			CHNode poiNode = (CHNode) graph.getNearestNode(poi.getLatitude(), poi.getLongitude());
//
//			BidirectionalDijkstraCHBaselineIterator dj = new BidirectionalDijkstraCHBaselineIterator(graph, source, poiNode, smallerDistancePoI, dijkstraHash, poisFound);
//
//			this.smallerDistancePoI.add(new DistanceEntry(poiNode.getId(), 0, -1));
//			dijkstraHash.put(poiNode.getId(), dj);
//		}
//
//		StopWatch knnSW = new StopWatch();
//
//		knnSW.start();
//
//		while (!dijkstraHash.isEmpty()) {
//
//			long currentPoI = smallerDistancePoI.poll().getId();
//			logger.debug("PoI being analyzed: {}.", currentPoI);
//
//			if (dijkstraHash.get(currentPoI).iterate()) {
//				dijkstraHash.remove(currentPoI);
//			}
//
//		}
//
//		knnSW.stop();
//
//		logger.info("Execution Time of the Naive kNN Baseline: {}ms", knnSW.getNanos());
//
//		retrieveKNN();
//
//	}
	
	public void search(CHNode source, int k) {

		StopWatch knnSW = new StopWatch();

		knnSW.start();
		
		for (PoI poi : graph.getPOIs()) {
			BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graph);
			
			long i = source.getId();
			long j = graph.getNearestNode(poi.getLatitude(), poi.getLongitude()).getId();
			
			try {
				Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graph.getNode(i), graph.getNode(j));
				DistanceEntry poiDistance = new DistanceEntry(j, (int) bidirectionalDijkstraFinalPath.getTotalDistance(), -1);
				result.add(poiDistance);
//				System.out.println("[BIDIRECTIONAL DIJKSTRA] Path distance between " + i + " and " + j + ": ");
			} catch (Exception e) {
//				System.out.println("[BIDIRECTIONAL DIJKSTRA] Path NOT FOUND between " + i + " and " + j);
			}
		}

		knnSW.stop();

		logger.info("Execution Time of the Naive kNN Baseline: {}ms", knnSW.getNanos());

//		retrieveKNN();

	}
	

	public void retrieveKNN() {

		
		for (Entry<Long, Path> poi : poisFound.entrySet()) {
			nearestNeighbors.add(new DistanceEntry(poi.getKey(), (int) poi.getValue().getTotalCost(), -1l));
		}

//		System.out.println(nearestNeighbors);

	}

}
