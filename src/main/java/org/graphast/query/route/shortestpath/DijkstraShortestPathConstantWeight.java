package org.graphast.query.route.shortestpath;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastNode;

public class DijkstraShortestPathConstantWeight extends DijkstraShortestPath {

	public DijkstraShortestPathConstantWeight(Graphast graph) {
		super(graph);
	}
	
	public void expandVertex(GraphastNode target, Entry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<Entry> queue, HashMap<Long, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {
			//long vid = convertToInt(v);
			//int arrivalTime = graph.getArrival(removed.getAt(), neig.get(v));
			int travelTime = removed.getTravelTime() + neig.get(vid);
			Entry newEntry = new Entry(vid, travelTime, 0, removed.getId());
			
			String label = null;
			
			if (!wasTraversed.containsKey(vid)){					
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
				
				for(Long outEdges : graph.getOutEdges(removed.getId())) {
					if ((int) graph.getEdge(outEdges).getToNode() == vid) {
						label = graph.getEdge(outEdges).getLabel();
					}
				}
				
				parents.put(vid, new RouteEntry(removed.getId(), neig.get(vid), label));
			} else {
				int cost = wasTraversed.get(vid);
				if (cost != wasRemoved) {
					if(cost > newEntry.getTravelTime()){
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

						//String label = graph.getEdgeLabel(vid);
						//parents.put(vid, new RouteEntry(vid, neig.get(vid), label));
					}
				}
			}
		}
	}

	@Override
	public int shortestPath(GraphastNode source, GraphastNode target) {
		return shortestPath(source, target, null);
	}

	@Override
	public int shortestPath(long source, long target) {
		return shortestPath(source, target, null);
	}

	@Override
	public int shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time);
	}
}
