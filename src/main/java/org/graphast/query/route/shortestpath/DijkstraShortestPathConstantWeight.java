package org.graphast.query.route.shortestpath;

import static org.graphast.util.NumberUtils.convertToInt;
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
	
	public void expandVertex(GraphastNode target, Entry removed, HashMap<Integer, Integer> wasTraversed, 
			PriorityQueue<Entry> queue, HashMap<Integer, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long v : neig.keySet()) {
			int vid = convertToInt(v);
			//int arrivalTime = graph.getArrival(removed.getAt(), neig.get(v));
			int travelTime = removed.getTt() + neig.get(v);
			Entry newEntry = new Entry(	vid, travelTime, 0, removed.getId());
			
			if(!wasTraversed.containsKey(vid)){					
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTt());
				
				
				parents.put(vid, new RouteEntry(removed.getId(), neig.get(v)));
				
				
			}else{
				int cost = wasTraversed.get(vid);
				if(cost != wasRemoved){
					if(cost>newEntry.getTt()){
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTt());

						parents.put(vid, new RouteEntry(vid, neig.get(v)));
					
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
