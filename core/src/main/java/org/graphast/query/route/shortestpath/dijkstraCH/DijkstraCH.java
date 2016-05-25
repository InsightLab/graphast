package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class DijkstraCH {

	protected static int wasRemoved = -1;

	private CHGraph graph;
	
	HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
	HashMap<Long, Integer> wasTraversedPoI = new HashMap<Long, Integer>();
	
	public DijkstraCH(CHGraph graph) {
		this.graph = graph;
	}
	
	public Set<Path> shortestPath(CHNode source, int k) {
		
		PriorityQueue<DistanceEntry> queue = new PriorityQueue<>();
//		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
//		HashMap<Long, Integer> wasTraversedPoI = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		DistanceEntry removed = null;

		init(source, queue);

		while (!queue.isEmpty()) {
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);

			if (wasTraversedPoI.size() >= k) {
				
				Set<Path> resultSet = new HashSet<>();
				
				for(Map.Entry<Long, Integer> removedPoI : wasTraversedPoI.entrySet()) {
				
					Path path = new Path();
					path.constructPath(removedPoI.getKey(), parents, graph);
					resultSet.add(path);
				}

				return resultSet;
			
			}

			expandVertex(removed, queue, parents);
		}
		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");
	}

	public void init(Node source, PriorityQueue<DistanceEntry> queue) {
		int sid = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sid, 0, -1));
	}

	public void expandVertex(DistanceEntry removed,
			PriorityQueue<DistanceEntry> queue, HashMap<Long, RouteEntry> parents) {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removed.getId()));

		for (long vid : neighbors.keySet()) {


			if(graph.getNode(vid).getLevel() < graph.getNode(removed.getId()).getLevel()) {
				continue;
			}
			
//			int arrivalTime = graph.getArrival(removed.getArrivalTime(), neighbors.get(vid));
//			int travelTime = removed.getTravelTime() + neighbors.get(vid);
//			TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime, removed.getId());

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid), removed.getId());
			
			Edge edge = null;
			int distance = -1;

			if (!wasTraversed.containsKey(vid)) {

				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), neighbors.get(vid));
				
				distance = neighbors.get(vid);
				edge = getEdge(removed.getId(), vid, distance);

//				distance/17
				parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
				
				if(graph.getNode(vid).getCategory()==2) {
					wasTraversedPoI.put(newEntry.getId(), neighbors.get(vid));
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
						
						if(graph.getNode(vid).getCategory()==2) {
							wasTraversedPoI.put(newEntry.getId(), neighbors.get(vid));
						}
					}
				}
			}
		}

	}
	
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance) {
		CHEdge edge = null;
		for(Long outEdge : graph.getOutEdges(fromNodeId)) {
			edge = graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}

}
