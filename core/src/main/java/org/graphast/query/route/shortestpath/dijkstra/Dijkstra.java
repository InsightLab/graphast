package org.graphast.query.route.shortestpath.dijkstra;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;
import org.graphast.util.DateUtils;

/**
 * Abstract Class for Dijkstra Algorithm. The algorithm is subdivide in two main methods:
 *  -expandVertex
 *  -shortestPath
 *  Both methods have small differences depending on how it is implemented Dijkstra.
 *  Extends the class that has the pattern of the classes of shortestPath like AStar and Dijkstra 
 */
public abstract class Dijkstra extends AbstractShortestPathService {

	//private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Dijkstra(GraphBounds graphBounds) {
		super(graphBounds);
	}
	
	public Dijkstra(Graph graph) {
		super(graph);
	}
	
	//TODO Double check this method!!
	//TODO Create tests!!
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

	/**the basic implementation of shortestPath method*/
	public Path shortestPath(Node source, Node target, Date time) {
		PriorityQueue<TimeEntry> queue = new PriorityQueue<TimeEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		TimeEntry removed = null;
		int targetId = convertToInt(target.getId());
		int t = DateUtils.dateToMilli(time);

		init(source, target, queue, parents, t);

		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);		

			if(removed.getId() == targetId) {
				Path path = new Path();
				path.constructPath(removed.getId(), parents, graph);
				return path;
			}

			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ") and (" 
				+ target.getLatitude() + "," + target.getLongitude() + ")");
	}
	
	public void init(Node source, Node target, PriorityQueue<TimeEntry> queue, 
			HashMap<Long, RouteEntry> parents, int t){
		int sid = convertToInt(source.getId());
		
		queue.offer(new TimeEntry(sid, 0, t, -1));
	}

	public abstract void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents);
	
	/** shortestPath with no Time*/
	@Override
	public Path shortestPath(Node source, Node target) {
		return shortestPath(source, target, null);
	}

	/** shortestPath with no Time*/
	@Override
	public Path shortestPath(long source, long target) {
		return shortestPath(source, target, null);
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time);
	}
	
}
