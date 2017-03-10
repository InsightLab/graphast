package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;
import org.graphast.util.DateUtils;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class DijkstraCH {

	protected static int wasRemoved = -1;

	private CHGraph graph;

	private int maxHopLimit;
	
	private int limitVisitedNodes = Integer.MAX_VALUE;

	public DijkstraCH(CHGraph graph) {
		this.graph = graph;
	}

	// //TODO Change the name of this method to kNN
	// public List<Path> shortestPath(CHNode source, int k) {
	//
	// PriorityQueue<DistanceEntry> queue = new PriorityQueue<>();
	// HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
	// HashMap<Long, Integer> wasTraversedPoI = new HashMap<Long, Integer>();
	// HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
	// DistanceEntry removed = null;
	//
	// init(source, queue);
	//
	// while (!queue.isEmpty()) {
	// removed = queue.poll();
	// wasTraversed.put(removed.getId(), wasRemoved);
	//
	// if (graph.getNode(removed.getId()).getCategory() == 2) {
	// wasTraversedPoI.put(removed.getId(), wasRemoved);
	// }
	//
	// if (wasTraversedPoI.size() >= k) {
	//
	// Queue<Path> localResultQueue = new PriorityQueue<Path>(new
	// Comparator<Path>() {
	//
	// public int compare(Path w1, Path w2) {
	// return w1.compareTo(w2);
	// }
	// });
	//
	// List<Path> resultList = new LinkedList<>();
	//
	// for (Map.Entry<Long, Integer> removedPoI : wasTraversedPoI.entrySet()) {
	//
	// Path path = new Path();
	// path.constructPath(removedPoI.getKey(), parents, graph);
	// localResultQueue.add(path);
	// }
	//
	// for (int i = 0; i < k; i++) {
	// resultList.add(localResultQueue.poll());
	// }
	//
	// return resultList;
	//
	// }
	//
	// expandVertex(removed, queue, parents, wasTraversed, wasTraversedPoI);
	// }
	// throw new PathNotFoundException(
	// "Path not found between (" + source.getLatitude() + "," +
	// source.getLongitude() + ")");
	// }

	public Path shortestPath(Node source, Node target, CHNode skippedNode) {

		PriorityQueue<DistanceEntry> queue = new PriorityQueue<>();
		HashMap<Long, Integer> wasTraversed = new HashMap<>();
		HashMap<Long, RouteEntry> parents = new HashMap<>();
		DistanceEntry removed;
		int targetId = convertToInt(target.getId());
		int settleNodes = 0;

		init(source, queue, parents);

		while (!queue.isEmpty()) {

			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);
			settleNodes++;
			
			//TODO Double check if put the expandVertex here is correct.
			expandVertex(removed, queue, parents, wasTraversed, skippedNode);
			
			if (queue.peek().getId() == targetId) {
				Path path = new Path();
				path.constructPath(queue.peek().getId(), parents, graph);
				return path;
			}

			if(settleNodes >= limitVisitedNodes)
				return null;

		}
		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude()
				+ ") and (" + target.getLatitude() + "," + target.getLongitude() + ")");
	}

	// public void expandVertex(Node target, DistanceEntry removed,
	// HashMap<Long, Integer> wasTraversed,
	// PriorityQueue<DistanceEntry> queue, HashMap<Long, RouteEntry> parents,
	// Node skippedNode) {
	//
	// Long2IntMap neig =
	// graph.accessNeighborhood(graph.getNode(removed.getId()));
	//
	// for (long vid : neig.keySet()) {
	//
	// if (graph.getNode(vid).getLevel() <
	// graph.getNode(removed.getId()).getLevel()) {
	// continue;
	// }
	//
	// if (skippedNode == null) {
	// // if (vid != skippedNode.getId()) {
	//
	// int arrivalTime = graph.getArrival(removed.getArrivalTime(),
	// neig.get(vid));
	// int travelTime = removed.getTravelTime() + neig.get(vid);
	// TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime,
	// removed.getId());
	//
	// Edge edge = null;
	// int distance = -1;
	//
	// if (!wasTraversed.containsKey(vid)) {
	//
	// queue.offer(newEntry);
	// wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
	//
	// distance = neig.get(vid);
	// edge = getEdge(removed.getId(), vid, distance);
	// parents.put(vid, new RouteEntry(removed.getId(), distance / 17,
	// edge.getId(), edge.getLabel()));
	// } else {
	//
	// int cost = wasTraversed.get(vid);
	//
	// if (cost != wasRemoved) {
	// if (cost > newEntry.getTravelTime()) {
	// queue.remove(newEntry);
	// queue.offer(newEntry);
	// wasTraversed.remove(newEntry.getId());
	// wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
	//
	// parents.remove(vid);
	// distance = neig.get(vid);
	// edge = getEdge(removed.getId(), vid, distance);
	// parents.put(vid,
	// new RouteEntry(removed.getId(), distance / 17, edge.getId(),
	// edge.getLabel()));
	// }
	// }
	// }
	// } else {
	// if (vid != skippedNode.getId()) {
	//
	// int arrivalTime = graph.getArrival(removed.getArrivalTime(),
	// neig.get(vid));
	// int travelTime = removed.getTravelTime() + neig.get(vid);
	// TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime,
	// removed.getId());
	//
	// Edge edge = null;
	// int distance = -1;
	//
	// if (!wasTraversed.containsKey(vid)) {
	//
	// queue.offer(newEntry);
	// wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
	//
	// distance = neig.get(vid);
	// edge = getEdge(removed.getId(), vid, distance);
	// parents.put(vid, new RouteEntry(removed.getId(), distance / 17,
	// edge.getId(), edge.getLabel()));
	// } else {
	//
	// int cost = wasTraversed.get(vid);
	//
	// if (cost != wasRemoved) {
	// if (cost > newEntry.getTravelTime()) {
	// queue.remove(newEntry);
	// queue.offer(newEntry);
	// wasTraversed.remove(newEntry.getId());
	// wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
	//
	// parents.remove(vid);
	// distance = neig.get(vid);
	// edge = getEdge(removed.getId(), vid, distance);
	// parents.put(vid,
	// new RouteEntry(removed.getId(), distance / 17, edge.getId(),
	// edge.getLabel()));
	// }
	// }
	// }
	// } else {
	// continue;
	// }
	// }
	// }
	// }

	public void init(Node source, PriorityQueue<DistanceEntry> queue) {
		int sid = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sid, 0, -1));
	}

	public void init(Node source, PriorityQueue<DistanceEntry> queue, Map<Long, RouteEntry> parents) {
		int sid = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sid, 0, -1));
	}

	public void expandVertex(DistanceEntry removed, PriorityQueue<DistanceEntry> queue, Map<Long, RouteEntry> parents,
			Map<Long, Integer> wasTraversed, CHNode skippedNode) {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removed.getId()));

		for (long vid : neighbors.keySet()) {

			//TODO <= or < ?
			if (vid == skippedNode.getId() || graph.getNode(vid).getLevel() < skippedNode.getLevel()) {
				continue;
			}
			
//			if (vid == skippedNode.getId()) {
//				continue;
//			}
			

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + removed.getDistance(),
					removed.getId());

			Edge edge;
			int distance;

			if (!wasTraversed.containsKey(vid)) {

				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(removed.getId(), vid, distance);

				parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = wasTraversed.get(vid);
				distance = neighbors.get(vid) + removed.getDistance();

				if (cost > distance) {
					queue.remove(newEntry);
					queue.offer(newEntry);
					wasTraversed.remove(newEntry.getId());
					wasTraversed.put(newEntry.getId(), newEntry.getDistance());

					parents.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(removed.getId(), vid, distance);
					parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));

				}

			}
		}
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

	public void setMaxHopLimit(int maxHopLimit) {
		this.maxHopLimit = maxHopLimit;
	}

	public void setLimitVisitedNodes(int limitVisitedNodes) {
		this.limitVisitedNodes = limitVisitedNodes;
	}
	
	

}
