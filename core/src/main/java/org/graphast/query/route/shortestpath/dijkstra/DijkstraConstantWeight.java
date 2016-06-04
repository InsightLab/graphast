package org.graphast.query.route.shortestpath.dijkstra;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;

public class DijkstraConstantWeight extends Dijkstra {

	public DijkstraConstantWeight(Graph graph) {
		super(graph);
	}
	
	public DijkstraConstantWeight(GraphBounds graphBounds) {
		super(graphBounds);
	}
	
	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, Set<Long> wasVisited,
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {
			
			int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
			int travelTime = removed.getTravelTime() + neig.get(vid);
			TimeEntry newEntry = new TimeEntry(	vid, travelTime, arrivalTime, removed.getId());

			Edge edge = null;
			int distance = -1;
			
			if (!wasTraversed.containsKey(vid)) {		
				
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
				
				distance = neig.get(vid);
				edge = getEdge(removed.getId(), vid, distance);
				parents.put(vid, new RouteEntry(removed.getId(), distance/17, edge.getId(), edge.getLabel()));
			} else {
				
				int cost = wasTraversed.get(vid);
				
				if (cost != wasRemoved) {
					if(cost > newEntry.getTravelTime()) {
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
						
						parents.remove(vid);
						distance = neig.get(vid);
						edge = getEdge(removed.getId(), vid, distance);
						parents.put(vid, new RouteEntry(removed.getId(), distance/17, edge.getId(), edge.getLabel()));
					}
				}
			}
		}
	}

	private Edge getEdge(long fromNodeId, long toNodeId, int distance) {
		Edge edge = null;
		for(Long outEdge : graph.getOutEdges(fromNodeId)) {
			edge = graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}

}
