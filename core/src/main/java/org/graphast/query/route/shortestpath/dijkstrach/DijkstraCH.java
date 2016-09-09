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

	public DijkstraCH(CHGraph graph) {
		this.graph = graph;
	}

	public List<Path> shortestPath(CHNode source, int k) {

		PriorityQueue<DistanceEntry> queue = new PriorityQueue<>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, Integer> wasTraversedPoI = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		DistanceEntry removed = null;

		init(source, queue);

		while (!queue.isEmpty()) {
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);
			
			if (graph.getNode(removed.getId()).getCategory() == 2) {
				wasTraversedPoI.put(removed.getId(), wasRemoved);
			}

			if (wasTraversedPoI.size() >= k) {

				Queue<Path> localResultQueue = new PriorityQueue<Path>(new Comparator<Path>() {

					public int compare(Path w1, Path w2) {
						return w1.compareTo(w2);
					}
				});

				List<Path> resultList = new LinkedList<>();

				for (Map.Entry<Long, Integer> removedPoI : wasTraversedPoI.entrySet()) {

					Path path = new Path();
					path.constructPath(removedPoI.getKey(), parents, graph);
					localResultQueue.add(path);
				}

				for (int i = 0; i < k; i++) {
					resultList.add(localResultQueue.poll());
				}

				return resultList;

			}

			expandVertex(removed, queue, parents, wasTraversed, wasTraversedPoI);
		}
		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");
	}

	public Path shortestPath(Node source, Node target, Date time, Node skippedNode) {

		PriorityQueue<TimeEntry> queue = new PriorityQueue<TimeEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		TimeEntry removed = null;
		int targetId = convertToInt(target.getId());
		int t = DateUtils.dateToMilli(time);

		init(source, target, queue, parents, t);

		while (!queue.isEmpty()) {
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);

			if(this.maxHopLimit < wasTraversed.size())
				return null;
			
			if (removed.getId() == targetId) {
				Path path = new Path();
				path.constructPath(removed.getId(), parents, graph);
				return path;
			}
			
			

			expandVertex(target, removed, wasTraversed, queue, parents, skippedNode);
		}
		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude()
				+ ") and (" + target.getLatitude() + "," + target.getLongitude() + ")");
	}

	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed,
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents, Node skippedNode) {

		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));

		for (long vid : neig.keySet()) {
			
			if (graph.getNode(vid).getLevel() < graph.getNode(removed.getId()).getLevel()) {
				continue;
			}

			if (skippedNode == null) {
				// if (vid != skippedNode.getId()) {

				int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
				int travelTime = removed.getTravelTime() + neig.get(vid);
				TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime, removed.getId());

				Edge edge = null;
				int distance = -1;

				if (!wasTraversed.containsKey(vid)) {

					queue.offer(newEntry);
					wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

					distance = neig.get(vid);
					edge = getEdge(removed.getId(), vid, distance);
					parents.put(vid, new RouteEntry(removed.getId(), distance / 17, edge.getId(), edge.getLabel()));
				} else {

					int cost = wasTraversed.get(vid);

					if (cost != wasRemoved) {
						if (cost > newEntry.getTravelTime()) {
							queue.remove(newEntry);
							queue.offer(newEntry);
							wasTraversed.remove(newEntry.getId());
							wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

							parents.remove(vid);
							distance = neig.get(vid);
							edge = getEdge(removed.getId(), vid, distance);
							parents.put(vid,
									new RouteEntry(removed.getId(), distance / 17, edge.getId(), edge.getLabel()));
						}
					}
				}
			} else {
				if (vid != skippedNode.getId()) {

					int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
					int travelTime = removed.getTravelTime() + neig.get(vid);
					TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime, removed.getId());

					Edge edge = null;
					int distance = -1;

					if (!wasTraversed.containsKey(vid)) {

						queue.offer(newEntry);
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

						distance = neig.get(vid);
						edge = getEdge(removed.getId(), vid, distance);
						parents.put(vid, new RouteEntry(removed.getId(), distance / 17, edge.getId(), edge.getLabel()));
					} else {

						int cost = wasTraversed.get(vid);

						if (cost != wasRemoved) {
							if (cost > newEntry.getTravelTime()) {
								queue.remove(newEntry);
								queue.offer(newEntry);
								wasTraversed.remove(newEntry.getId());
								wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

								parents.remove(vid);
								distance = neig.get(vid);
								edge = getEdge(removed.getId(), vid, distance);
								parents.put(vid,
										new RouteEntry(removed.getId(), distance / 17, edge.getId(), edge.getLabel()));
							}
						}
					}
				} else {
					continue;
				}
			}
		}
	}

	public void init(Node source, PriorityQueue<DistanceEntry> queue) {
		int sid = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sid, 0, -1));
	}

	public void init(Node source, Node target, PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents,
			int t) {
		int sid = convertToInt(source.getId());

		queue.offer(new TimeEntry(sid, 0, t, -1));
	}

	public void expandVertex(DistanceEntry removed, PriorityQueue<DistanceEntry> queue,
			HashMap<Long, RouteEntry> parents, HashMap<Long, Integer> wasTraversed,
			HashMap<Long, Integer> wasTraversedPoI) {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removed.getId()));

		for (long vid : neighbors.keySet()) {
			// if (graph.getNode(vid).getPriority() <
			// graph.getNode(removed.getId()).getPriority()) {
			if (graph.getNode(vid).getLevel() < graph.getNode(removed.getId()).getLevel()) {
//				if(graph.getNode(vid).getExternalId() != graph.getNode(removed.getId()).getExternalId()) {
					continue;
//				}
				
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
	
}
