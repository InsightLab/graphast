package org.graphast.query.route.shortestpath.astar;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;
import org.graphast.util.DateUtils;
import org.graphast.util.DistanceUtils;

public abstract class AStar extends AbstractShortestPathService{

	//private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public AStar(Graph graphAdapter) {
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
	
	public Path shortestPath(Node source, Node target, Date time) {
		PriorityQueue<LowerBoundEntry> queue = new PriorityQueue<LowerBoundEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		LowerBoundEntry removed = null;
		int targetId = convertToInt(target.getId());
		int t = DateUtils.dateToMilli(time);
		
		init(source, target, queue, parents, t);
		
		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);	
			
			if(removed.getId() == targetId){
				Path path = new Path();
				path.constructPath(removed.getId(), parents, graph);
				return path;
			}
			
			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ") and (" 
				+ target.getLatitude() + "," + target.getLongitude() + ")");
	}

	public void init(Node source, Node target, PriorityQueue<LowerBoundEntry> queue,
			HashMap<Long, RouteEntry> parents, int t) {
		int sid = convertToInt(source.getId());
		
		queue.offer(new LowerBoundEntry(sid, 0, t, -1,	(int) DistanceUtils.timeCost(source, target)));

//		parents.put(sourceId, null);		
	}
	
	public abstract void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<LowerBoundEntry> queue, HashMap<Long, RouteEntry> parents);
	
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
