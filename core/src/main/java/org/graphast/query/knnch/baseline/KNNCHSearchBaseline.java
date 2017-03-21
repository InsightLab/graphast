package org.graphast.query.knnch.baseline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.geometry.PoI;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
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

	public KNNCHSearchBaseline(CHGraph graph) {
		this.graph = graph;
	}

	public void search(CHNode source, int k) {

		for (PoI poi : graph.getPOIs()) {
			CHNode poiNode = (CHNode) graph.getNearestNode(poi.getLatitude(), poi.getLongitude());

			BidirectionalDijkstraCHBaselineIterator dj = new BidirectionalDijkstraCHBaselineIterator(graph, source,
					poiNode, smallerDistancePoI, dijkstraHash, poisFound);

			this.smallerDistancePoI.add(new DistanceEntry(poiNode.getId(), 0, -1));
			dijkstraHash.put(poiNode.getId(), dj);
		}

		StopWatch knnSW = new StopWatch();

		knnSW.start();

		while (!dijkstraHash.isEmpty()) {

			long currentPoI = smallerDistancePoI.poll().getId();
			logger.debug("PoI being analyzed: {}.", currentPoI);

			if (dijkstraHash.get(currentPoI).iterate()) {
				dijkstraHash.remove(currentPoI);
			}

		}

		knnSW.stop();

		logger.info("Execution Time of the Naive kNN Baseline: {}ms", knnSW.getNanos());

		retrieveKNN();

	}

	public void retrieveKNN() {

		for (Entry<Long, Path> poi : poisFound.entrySet()) {
			nearestNeighbors.add(new DistanceEntry(poi.getKey(), (int) poi.getValue().getTotalCost(), -1l));
		}

//		System.out.println(nearestNeighbors);

	}

}
