package org.graphast.query.route.shortestpath.dijkstra;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

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
			//long vid = convertToInt(v);
			//int arrivalTime = graph.getArrival(removed.getAt(), neig.get(v));
			int travelDistance = removed.getDistance() + neig.get(vid);
			DistanceEntry newDistanceEntry = new DistanceEntry(vid, travelDistance, removed.getId());
			
			String label = null;
			
			if (!wasTraversed.containsKey(vid)) {		
				
				queue.offer(newDistanceEntry);
				wasTraversed.put(newDistanceEntry.getId(), newDistanceEntry.getDistance());
				
				for(Long outEdges : graph.getOutEdges(removed.getId())) {
					
					if ((int) graph.getEdge(outEdges).getToNode() == vid) {
						label = graph.getEdge(outEdges).getLabel();
					}
				
				}
				
				parents.put(vid, new RouteEntry(removed.getId(), neig.get(vid), label));
				
			} else {
				
				int cost = wasTraversed.get(vid);
				
				if (cost != wasRemoved) {
					
					if(cost > newDistanceEntry.getDistance()) {
						
						queue.remove(newDistanceEntry);
						queue.offer(newDistanceEntry);
						wasTraversed.remove(newDistanceEntry.getId());
						wasTraversed.put(newDistanceEntry.getId(), newDistanceEntry.getDistance());

						//String label = graph.getEdgeLabel(vid);
						//parents.put(vid, new RouteEntry(vid, neig.get(vid), label));
					}
				}
			}
		}
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
