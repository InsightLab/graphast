package org.graphast.query.route.shortestpath.astar;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;

import com.graphhopper.util.DistanceCalcEarth;

public class AStarConstantWeight extends AStar{
	
	private DistanceCalcEarth distance;
	
	public AStarConstantWeight(Graph graph) {
		super(graph);
		this.distance = new DistanceCalcEarth();
	}

	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue, HashMap<Long, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {
			
			int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
			int travelTime = removed.getTravelTime() + neig.get(vid);

			//TODO The problem is in this calculation of lower bound! When we add the distance between 
			//     two points, the priority changes!
			
//			int lowerBound = travelTime + (int)Math.sqrt(Math.pow(((double) target.getLatitude() - (double)graph.getNode(vid).getLatitude()),2)+Math.pow(((double) target.getLongitude() - (double) graph.getNode(vid).getLongitude()),2));
					
			
			
			int lowerBound = travelTime + (int)(distance.calcDist( (double)graph.getNode(vid).getLatitude(), (double) graph.getNode(vid).getLongitude(),
					(double) target.getLatitude(), (double) target.getLongitude()))*100;
			LowerBoundEntry newEntry = new LowerBoundEntry(vid, travelTime, arrivalTime, removed.getId(), lowerBound);
			
			String label = null;
			Edge edge = null;
			long edgeId = -1;
			int distance = -1;
			
			if(!wasTraversed.containsKey(vid)){					
				queue.offer(newEntry);
				wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
				
				for(Long outEdges : graph.getOutEdges(removed.getId())) {
					edge = graph.getEdge(outEdges);
					if ((int) edge.getToNode() == vid) {
						label = edge.getLabel();
						edgeId = edge.getId();
						distance = neig.get(vid);
					}
				}
				parents.put(vid, new RouteEntry(removed.getId(), neig.get(vid)/17, edgeId, label));
				
			} else {
				
				int cost = wasTraversed.get(vid);
				
				if (cost != wasRemoved){
					
					if(cost > newEntry.getTravelTime()){
						
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
						
						parents.remove(vid);
						distance = neig.get(vid);
						parents.put(vid, new RouteEntry(removed.getId(), distance/17, edgeId, label));
					}
				}
			}
		}
	}
}
