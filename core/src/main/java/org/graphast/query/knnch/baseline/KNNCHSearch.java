package org.graphast.query.knnch.baseline;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.geometry.PoI;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;

public class KNNCHSearch {

	private CHGraph graph;

	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Map<Long, BidirectionalDijkstraCHIterator> dijkstraHash = new HashMap<>();
	// TODO Criar metodos para adicionar pois na estrutura poisFound
	private Map<Long, Path> poisFound = new HashMap<>();

	public KNNCHSearch(CHGraph graph) {
		this.graph = graph;
	}

	public void search(CHNode source, int k) {

		for (PoI poi : graph.getPOIs()) {
			CHNode poiNode = (CHNode) graph.getNearestNode(poi.getLatitude(), poi.getLongitude());

			BidirectionalDijkstraCHIterator dj = new BidirectionalDijkstraCHIterator(graph, source, poiNode,
					smallerDistancePoI, dijkstraHash);

			this.smallerDistancePoI.add(new DistanceEntry(poiNode.getId(), 0, -1));
			dijkstraHash.put(poiNode.getId(), dj);
		}

		while (!dijkstraHash.isEmpty()) {

			System.out.println(smallerDistancePoI);

			long currentPoI = smallerDistancePoI.poll().getId();
			System.out.println("PoI que será analisado nessa iteração: " + currentPoI);

			if (dijkstraHash.get(currentPoI).iterate()) {
				dijkstraHash.remove(currentPoI);
			}

		}

	}

}
