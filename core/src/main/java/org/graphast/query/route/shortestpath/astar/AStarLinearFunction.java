package org.graphast.query.route.shortestpath.astar;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;
import org.graphast.util.DistanceUtils;


public class AStarLinearFunction extends AStar{
	
	public AStarLinearFunction(Graph graph) {
		super(graph);
	}

	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue, HashMap<Long,RouteEntry> parents){
		
		HashMap<Node, Integer> neig = graph.accessNeighborhood(graph.getNode(removed.getId()), removed.getArrivalTime());
		
		for (Node v : neig.keySet()) {
			long vid = v.getId();
			int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(v));
			int travelTime = removed.getTravelTime() + neig.get(v);
			int lowerBound = travelTime + (int) DistanceUtils.timeCost(v, target);
			LowerBoundEntry newEntry = new LowerBoundEntry(vid, travelTime, arrivalTime, removed.getId(), lowerBound);
			
			Edge edge = null;
			int distance = -1;
			
			if(!wasTraversed.containsKey(vid)){					
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
				
				distance = neig.get(v);
				edge = getEdge(removed.getId(), vid, distance);
				parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
			}else{
				int cost = wasTraversed.get(vid);
				if(cost != wasRemoved){
					if(cost>newEntry.getTravelTime()){
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
						
						parents.remove(v);
						distance = neig.get(v);
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
}
