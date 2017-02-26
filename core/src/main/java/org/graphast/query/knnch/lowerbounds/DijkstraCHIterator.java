package org.graphast.query.knnch.lowerbounds;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class DijkstraCHIterator {

	// External references
	private CHGraph graph;
	private CHNode source;
	private CHNode target;
	private Queue<DistanceEntry> smallerDistancePoI;

	private int wasRemoved = -1;
	private int maxHopLimit;

	// Internal attributes
	private PriorityQueue<DistanceEntry> queue = new PriorityQueue<>();
	private HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
	private HashMap<Long, Integer> wasTraversedPoI = new HashMap<Long, Integer>();
	private HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
	private DistanceEntry removed = null;
	private DistanceEntry smallerDistanceForThisIteration = new DistanceEntry(-1, Integer.MAX_VALUE, -1);
	

	public DijkstraCHIterator(CHGraph graph, CHNode source, CHNode target, Queue<DistanceEntry> smallerDistancePoI) {

		this.graph = graph;
		this.source = source;
		this.target = target;
		this.smallerDistancePoI = smallerDistancePoI;

		init(source, queue);

	}

	public void init(Node source, Queue<DistanceEntry> queue) {

		int sid = convertToInt(source.getId());
		queue.offer(new DistanceEntry(sid, 0, -1));

	}

	public void iterate() {

		removed = queue.poll();
		wasTraversed.put(removed.getId(), wasRemoved);
		
		if(removed.getId() == this.target.getId())
			System.out.println("Encontrou PoI " + removed.getId());

		expandVertex(removed, queue, parents, wasTraversed, wasTraversedPoI);

	}

	public void expandVertex(DistanceEntry removed, PriorityQueue<DistanceEntry> queue,
			HashMap<Long, RouteEntry> parents, HashMap<Long, Integer> wasTraversed,
			HashMap<Long, Integer> wasTraversedPoI) {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removed.getId()));

		//TODO create a way to set the greater distance
		
		for (long vid : neighbors.keySet()) {
			// if (graph.getNode(vid).getPriority() <
			// graph.getNode(removed.getId()).getPriority()) {
			if (graph.getNode(vid).getLevel() < graph.getNode(removed.getId()).getLevel()) {
				// if(graph.getNode(vid).getExternalId() !=
				// graph.getNode(removed.getId()).getExternalId()) {
				continue;
				// }

			}

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid), removed.getId());

			Edge edge = null;
			int distance = -1;

			if (!wasTraversed.containsKey(vid)) {

				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removed.getId(), vid, distance);

				parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
				
				if(smallerDistanceForThisIteration.getDistance() > distance) {
					smallerDistanceForThisIteration.setId(target.getId());
					smallerDistanceForThisIteration.setDistance(distance);
					smallerDistancePoI.add(smallerDistanceForThisIteration);
				}

			} else {

				int cost = wasTraversed.get(vid);
				distance = neighbors.get(vid);

				if (cost != wasRemoved) {
					if (cost > distance) {
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), distance);

						parents.remove(vid);
						distance = neighbors.get(vid);
						edge = getEdge(removed.getId(), vid, distance);
						parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));

					}
				}
			}
		}
	}

	public void setMaxHopLimit(int maxHopLimit) {
		this.maxHopLimit = maxHopLimit;
	}
	
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance) {
		CHEdge edge = null;
		for (Long outEdge : graph.getOutEdges(fromNodeId)) {
			edge = graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}

}
