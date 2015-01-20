package org.graphast.query.route.shortestpath;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastNode;
import org.graphast.util.DateUtils;
import org.graphast.util.DistanceUtils;

public abstract class AStarShortestPath extends AbstractShortestPathService{

	public AStarShortestPath(Graphast graphAdapter) {
		super(graphAdapter);
	}
	
	public int shortestPath(GraphastNode source, GraphastNode target, Date time) {
		PriorityQueue<LowerBoundEntry> queue = new PriorityQueue<LowerBoundEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, Long> parents = new HashMap<Long, Long>();
		LowerBoundEntry removed = null;
		long targetId = target.getId();
		int t = DateUtils.dateToMilli(time);
		
		init(source, target, queue, parents, t);
		
		while(!queue.isEmpty()){
			removed = queue.poll();
			//System.out.println("removed: " + removed.getId() + " tt: " + removed.getTt() + " lb: " + removed.getLb());
			wasTraversed.put(removed.getId(), wasRemoved);	
			parents.put(removed.getId(), removed.getParent());
			
			if(removed.getId() == targetId){
				return removed.getTravelTime();
			}
			
			expandVertex(target, removed, wasTraversed, queue);
		}
		
		return Integer.MAX_VALUE;
	}

	public void init(GraphastNode source, GraphastNode target, PriorityQueue<LowerBoundEntry> queue,
			HashMap<Long, Long> parents, int t) {
		
		long sourceId = source.getId();
		
		queue.offer(new LowerBoundEntry(sourceId, 
				0, 
				t, 
				-1,
				(int) DistanceUtils.timeCost(source, target)));

		parents.put(sourceId, null);		
	}
	
	public abstract void expandVertex(GraphastNode target, Entry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue);

}
