package org.graphast.query.route.shortestpath.dijkstra;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;

public class DijkstraLinearFunction extends Dijkstra{

	public DijkstraLinearFunction(Graph graph) {
		super(graph);
	}
	
	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<TimeEntry> queue, HashMap<Integer, RouteEntry> parents){
		
		HashMap<Node, Integer> neig = graph.accessNeighborhood(graph.getNode(removed.getId()), removed.getArrivalTime());
		
		for (Node v : neig.keySet()) {
			long vid = v.getId();
			int at = graph.getArrival(removed.getArrivalTime(), neig.get(v));
			int tt = removed.getTravelTime() + neig.get(v);
			TimeEntry newEntry = new TimeEntry(	vid, tt, at, removed.getId());
			if(!wasTraversed.containsKey(vid)){					
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
			}else{
				int cost = wasTraversed.get(vid);
				if(cost != wasRemoved){
					if(cost>newEntry.getTravelTime()){
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
					}
				}
			}
		}
	}

	@Override
	public Path shortestPath(Node source, Node target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path shortestPath(long source, long target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void expandVertex(Node target, DistanceEntry removed,
			HashMap<Long, Integer> wasTraversed,
			PriorityQueue<DistanceEntry> queue,
			HashMap<Long, RouteEntry> parents) {
		// TODO Auto-generated method stub
		
	}

}
