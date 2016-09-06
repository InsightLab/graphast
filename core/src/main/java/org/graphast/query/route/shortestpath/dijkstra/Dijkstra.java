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

import com.graphhopper.util.StopWatch;

public abstract class Dijkstra extends AbstractShortestPathService {

	// private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected int maxVisitedNodes = Integer.MAX_VALUE;
	protected int maxHopLimit = Integer.MAX_VALUE;
	int numberOfTotalSettleNodes = 0;
	
	StopWatch nearestNeighborSW = new StopWatch();
	int numberOfNeighborsAccess = 0;
	int averageNeighborsAccessTime = 0;

	StopWatch expandingVertexSW = new StopWatch();
	int totalExpandingVertexTime = 0;
	
	public Dijkstra(GraphBounds graphBounds) {
		super(graphBounds);
	}

	public Dijkstra(Graph graph) {
		super(graph);
	}

	protected List<RouteEntry> reconstructPath(long id, HashMap<Long, RouteEntry> parents) {
		RouteEntry re = parents.get(id);
		long parent = re.getId();
		List<RouteEntry> path = new ArrayList<RouteEntry>();
		path.add(re);
		while (parent != -1) {
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

	public Path shortestPath(Node source, Node target, Date time, Node skippedNode) {
		
		//TODO Refactor this verification
//		if(this.getMaxVisitedNodes()==0) {
//			this.setMaxVisitedNodes(1);
//		}
		
		PriorityQueue<TimeEntry> queue = new PriorityQueue<TimeEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		TimeEntry removed = null;
		int targetId = convertToInt(target.getId());
		int t = DateUtils.dateToMilli(time);

		init(source, target, queue, parents, t);

		while (!queue.isEmpty()) {
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);
			numberOfTotalSettleNodes++;
			if (this.getMaxHopLimit() < wasTraversed.size())
				return null;
			
//			if (this.getMaxVisitedNodes() < wasTraversed.size())
//				return null;

			if (removed.getId() == targetId) {
				Path path = new Path();
				path.constructPath(removed.getId(), parents, graph);
				System.out.println("[REGULAR] Number of total settle nodes: " + numberOfTotalSettleNodes);
				
				averageNeighborsAccessTime = averageNeighborsAccessTime/numberOfNeighborsAccess;
				System.out.println("[REGULAR] Total Average Neighbors Access Time: " + averageNeighborsAccessTime);
				System.out.println("[REGULAR] Total Expanding Vertex Time: " + totalExpandingVertexTime);
				return path;
			}

			expandingVertexSW.start();
			expandVertex(target, removed, wasTraversed, queue, parents, skippedNode);
			expandingVertexSW.stop();
			totalExpandingVertexTime += expandingVertexSW.getNanos();
		}
		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude()
				+ ") and (" + target.getLatitude() + "," + target.getLongitude() + ")");
	}

	public void init(Node source, Node target, PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents, int t) {
		int sid = convertToInt(source.getId());

		queue.offer(new TimeEntry(sid, 0, t, -1));
	}

	public abstract void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed,
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents, Node skippedNode);

	public int getMaxVisitedNodes() {
		return maxVisitedNodes;
	}

	public void setMaxVisitedNodes(int maxVisitedNodes) {
		this.maxVisitedNodes = maxVisitedNodes;
	}
	
	public int getMaxHopLimit() {
		return maxHopLimit;
	}
	
	public void setMaxHopLimit(int maxHopLimit) {
		this.maxHopLimit = maxHopLimit;
	}

	@Override
	public Path shortestPath(Node source, Node target) {
		return shortestPath(source, target, null, null);
	}

	@Override
	public Path shortestPath(Node source, Node target, Node skippedNode) {
		return shortestPath(source, target, null, skippedNode);
	}

	@Override
	public Path shortestPath(Node source, Node target, Date time) {
		return shortestPath(source, target, time, null);
	}

	@Override
	public Path shortestPath(long source, long target) {
		return shortestPath(source, target, null, null);
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time, null);
	}

	@Override
	public Path shortestPath(long source, long target, Node skippedNode) {
		return shortestPath(graph.getNode(source), graph.getNode(target), null, skippedNode);
	}

	@Override
	public Path shortestPath(long source, long target, Date time, Node skippedNode) {
		return shortestPath(graph.getNode(source), graph.getNode(target), time, skippedNode);
	}

}
