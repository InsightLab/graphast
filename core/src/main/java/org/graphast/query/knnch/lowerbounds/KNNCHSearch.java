package org.graphast.query.knnch.lowerbounds;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.geometry.PoI;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DistanceUtils;

public class KNNCHSearch {

	private CHGraph graph;

	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Queue<DistanceEntry> lowerBoundDistanceSourcePoIs = new PriorityQueue<>();
	private Map<Long, BidirectionalDijkstraCHIterator> dijkstraHash = new HashMap<>();
	private DistanceEntry nextCandidateNNLowerBound = new DistanceEntry(-1, -1, -1);
	// TODO Criar metodos para adicionar pois na estrutura poisFound
	private Map<Long, Path> poisFound = new HashMap<>();

	public KNNCHSearch(CHGraph graph) {
		this.graph = graph;
	}

	/**
	 * This method will set the distanceSourcePoIs variable with the distance
	 * from each PoI to the source node.
	 * 
	 * @param source
	 *            source node of the query.
	 */
	private void createLowerBounds(Long source) {

		for (PoI poi : graph.getPOIs()) {
			long nodeId = graph.getNodeId(poi.getLatitude(), poi.getLongitude());
			Node node = graph.getNode(nodeId);

			// TODO Double check the units in for this distance (this cast may
			// not be totally correct).
			// Use this distance for real world maps.
			// int distance = (int)
			// DistanceUtils.distanceLatLong(graph.getNode(source).getLatitude(),
			// graph.getNode(source).getLongitude(), node.getLatitude(),
			// node.getLongitude());
			int distance = (int) Math.sqrt(Math.pow((node.getLatitude() - graph.getNode(source).getLatitude()), 2)
					+ Math.pow((node.getLongitude() - graph.getNode(source).getLongitude()), 2));

			DistanceEntry lowerBound = new DistanceEntry(nodeId, distance, -1);
			lowerBoundDistanceSourcePoIs.add(lowerBound);
		}

	}

	public void search(CHNode source, int k) {

		createLowerBounds(source.getId());

		for (int i = 0; i < k; i++) {
			BidirectionalDijkstraCHIterator dj = new BidirectionalDijkstraCHIterator(graph, source,
					graph.getNode(lowerBoundDistanceSourcePoIs.peek().getId()), smallerDistancePoI, dijkstraHash);

			this.smallerDistancePoI
					.add(new DistanceEntry(graph.getNode(lowerBoundDistanceSourcePoIs.peek().getId()).getId(), 0, -1));

			dijkstraHash.put(lowerBoundDistanceSourcePoIs.poll().getId(), dj);
		}

		// If the number of nearest nodes that we need is equal to the number of
		// PoIs in the graph,
		// there is no nextCandidate: all PoI's will be considered!
		if (graph.getPOIs().size() < k) {
			nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
			nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
		}

		while (!dijkstraHash.isEmpty()) {

			System.out.println(smallerDistancePoI);

			if ((graph.getPOIs().size() < k)
					&& smallerDistancePoI.peek().getDistance() >= nextCandidateNNLowerBound.getDistance()) {
				BidirectionalDijkstraCHIterator dj = new BidirectionalDijkstraCHIterator(graph, source,
						graph.getNode(nextCandidateNNLowerBound.getId()), smallerDistancePoI, dijkstraHash);
				dijkstraHash.put(nextCandidateNNLowerBound.getId(), dj);

				lowerBoundDistanceSourcePoIs.poll();
				nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
				nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
			}

			long currentPoI = smallerDistancePoI.poll().getId();
			System.out.println("PoI que será analisado nessa iteração: " + currentPoI);
			if (dijkstraHash.get(currentPoI).iterate()) {
				dijkstraHash.remove(currentPoI);
			}

		}

	}

}
