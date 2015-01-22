package org.graphast.query.route.shortestpath;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastNode;

import com.graphhopper.util.DistanceCalcEarth;

public class AStarShortestPathConstantWeight extends AStarShortestPath{
	private DistanceCalcEarth distance;
	
	public AStarShortestPathConstantWeight(Graphast graph) {
		super(graph);
		this.distance = new DistanceCalcEarth();
	}

	public void expandVertex(GraphastNode target, Entry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue, HashMap<Long, RouteEntry> parents){
		
		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {
			//int vid = convertToInt(v.getId());
			//int at = graph.getArrival(removed.getArrivalTime(), neig.get(v));
			int tt = removed.getTravelTime() + neig.get(vid);
			int lb = tt + (int) (distance.calcDist((double) graph.getNode(vid).getLatitude(), (double) graph.getNode(vid).getLongitude(),
					(double) target.getLatitude(), (double) target.getLongitude())) * 100;
			LowerBoundEntry newEntry = new LowerBoundEntry(vid, tt, 0, removed.getId(), lb);
			
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
	public int shortestPath(GraphastNode source, GraphastNode target) {
		return shortestPath(source, target, null);
	}

	@Override
	public int shortestPath(long source, long target) {
		return shortestPath(graph.getNode(source), graph.getNode(target));
	}

	@Override
	public int shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time);
	}

}
