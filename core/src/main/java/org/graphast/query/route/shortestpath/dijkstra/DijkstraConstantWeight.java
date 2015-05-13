package org.graphast.query.route.shortestpath.dijkstra;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

public class DijkstraConstantWeight extends Dijkstra {

	public DijkstraConstantWeight(Graph graph) {
		super(graph);
	}
	
	public void expandVertex(Node target, DistanceEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<DistanceEntry> queue, HashMap<Long, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {

			int travelDistance = removed.getDistance() + neig.get(vid);
			DistanceEntry newDistanceEntry = new DistanceEntry(vid, travelDistance, removed.getId());
			
			Edge edge = null;
			int distance = -1;
			
			if (!wasTraversed.containsKey(vid)) {		
				
				queue.offer(newDistanceEntry);
				wasTraversed.put(newDistanceEntry.getId(), newDistanceEntry.getDistance());
				
				distance = neig.get(vid);
				edge = getEdge(removed.getId(), vid, distance);
				parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
			} else {
				
				int cost = wasTraversed.get(vid);
				
				if (cost != wasRemoved) {
					if(cost > newDistanceEntry.getDistance()) {
						queue.remove(newDistanceEntry);
						queue.offer(newDistanceEntry);
						wasTraversed.remove(newDistanceEntry.getId());
						wasTraversed.put(newDistanceEntry.getId(), newDistanceEntry.getDistance());
						parents.remove(vid);

						distance = neig.get(vid);
						edge = getEdge(removed.getId(), vid, distance);
						parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
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

	
	@Override
	public Int2ObjectMap<Path> shortestPath(Node source) {
		return super.shortestPath(source);
	}
	
	@Override
	public Path shortestPath(Node source, Node target) {
		return shortestPath(source, target, null);
	}

	@Override
	public Path shortestPath(long source, long target) {
		return shortestPath(source, target, null);
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time);
	}
}
