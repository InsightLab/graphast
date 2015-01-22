package org.graphast.query.route.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastNode;
import org.graphast.util.DateUtils;
import org.graphast.util.DistanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AStarShortestPath extends AbstractShortestPathService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public AStarShortestPath(Graphast graphAdapter) {
		super(graphAdapter);
	}
	
	protected List<RouteEntry> reconstructPath(long id, HashMap<Long, RouteEntry> parents){
		RouteEntry re = parents.get(id);
		long parent = re.getId();
		List<RouteEntry> path = new ArrayList<RouteEntry>();
		path.add(re);
		while(parent != -1){
			re = parents.get(parent);
			if (re != null) {
				path.add(re);
				parent = re.getId();
			} else {
				break;
			}
		}
		Collections.reverse(path);
		return path;
	}
	
	public int shortestPath(GraphastNode source, GraphastNode target, Date time) {
		PriorityQueue<LowerBoundEntry> queue = new PriorityQueue<LowerBoundEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		LowerBoundEntry removed = null;
		long targetId = target.getId();
		int t = DateUtils.dateToMilli(time);
		
		init(source, target, queue, parents, t);
		
		while(!queue.isEmpty()){
			removed = queue.poll();
			//System.out.println("removed: " + removed.getId() + " tt: " + removed.getTt() + " lb: " + removed.getLb());
			wasTraversed.put(removed.getId(), wasRemoved);	
			
			if(removed.getId() == targetId){
				List<RouteEntry> path = reconstructPath(removed.getId(), parents);
				logger.info("path: {}", path);
				return removed.getTravelTime();
			}
			
			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		
		return Integer.MAX_VALUE;
	}

	public void init(GraphastNode source, GraphastNode target, PriorityQueue<LowerBoundEntry> queue,
			HashMap<Long, RouteEntry> parents, int t) {
		
		long sourceId = source.getId();
		
		queue.offer(new LowerBoundEntry(sourceId, 
				0, 
				t, 
				-1,
				(int) DistanceUtils.timeCost(source, target)));

		parents.put(sourceId, null);		
	}
	
	public abstract void expandVertex(GraphastNode target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue, HashMap<Long, RouteEntry> parents);

}
