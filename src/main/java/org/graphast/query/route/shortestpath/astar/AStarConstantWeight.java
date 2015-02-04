package org.graphast.query.route.shortestpath.astar;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.query.route.shortestpath.model.Path;
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
			//int vid = convertToInt(v.getId());
			//int at = graph.getArrival(removed.getArrivalTime(), neig.get(v));
			int travelTime = removed.getTravelTime() + neig.get(vid);
			int lowerBound = travelTime + (int)(distance.calcDist( (double)graph.getNode(vid).getLatitude(), (double) graph.getNode(vid).getLongitude(),
					(double) target.getLatitude(), (double) target.getLongitude()));
			LowerBoundEntry newEntry = new LowerBoundEntry(vid, travelTime, 0, removed.getId(), lowerBound);
			
			String label = null;
			
			if(!wasTraversed.containsKey(vid)){					
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
				
				if (cost != wasRemoved){
					
					if(cost > newEntry.getTravelTime()){
						
						queue.remove(newEntry);
						queue.offer(newEntry);
						wasTraversed.remove(newEntry.getId());
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
						parents.remove(vid);
						parents.put(vid, new RouteEntry(removed.getId(), neig.get(vid), label));
					
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
		return shortestPath(graph.getNode(source), graph.getNode(target));
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time);
	}

}
