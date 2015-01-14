package org.graphast.query.route.shortestpath;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastNode;
import org.graphast.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DijkstraShortestPath extends AbstractShortestPathService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public DijkstraShortestPath(Graphast graph) {
		super(graph);
	}
	
	protected List<RouteEntry> reconstructPath(int id, HashMap<Integer, RouteEntry> parents){
		
		RouteEntry re = parents.get(id);
		int parent = re.getId();
		List<RouteEntry> path = new ArrayList<RouteEntry>();
		path.add(re);
		while(parent != -1){
			re = parents.get(parent);
			path.add(re);
			parent = re.getId();
		}
		Collections.reverse(path);
		return path;
	
	}
	
	public int shortestPath(GraphastNode source, GraphastNode target, Date time) {
		PriorityQueue<Entry> queue = new PriorityQueue<Entry>();
		HashMap<Integer, Integer> wasTraversed = new HashMap<Integer, Integer>();
		HashMap<Integer, RouteEntry> parents = new HashMap<Integer, RouteEntry>();
		Entry removed = null;
		int targetId = convertToInt(target.getId());
		int t = DateUtils.dateToMilli(time);
		
		init(source, target, queue, parents, t);
		
		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);	
			
			if(removed.getId() == targetId){
//				logger.info("path:" + reconstructPath((Integer) graphAdapter.getVertex(removed.getId()).getProperty(Property.ORGINALID)
//						, parents));
				return removed.getTt();
			}
			
			expandVertex(target, removed, wasTraversed, queue, parents);
			
		}
		
		throw new PathNotFoundException();
	}
	
	public void init(GraphastNode source, GraphastNode target, PriorityQueue<Entry> queue, 
			HashMap<Integer, RouteEntry> parents, int arrivalTime){
		int sourceId = convertToInt(source.getId());
		
		queue.offer(new Entry(sourceId, 0, arrivalTime, -1));

		//parents.put((Integer) graphAdapter.getVertex(sid).getProperty(Property.ORGINALID), new RouteEntry(-1, 0));
	}
	
	public abstract void expandVertex(GraphastNode target, Entry removed, HashMap<Integer, Integer> wasTraversed, 
			PriorityQueue<Entry> queue, HashMap<Integer, RouteEntry> parents);
	

	
}
