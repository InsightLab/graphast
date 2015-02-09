package org.graphast.query.route.shortestpath.dijkstra;

import static org.graphast.util.NumberUtils.convertToInt;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

public abstract class Dijkstra extends AbstractShortestPathService {

	//private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Dijkstra(Graph graph) {
		super(graph);
	}

	public Path shortestPath(Node source, Node target, Date time) {
		PriorityQueue<DistanceEntry> queue = new PriorityQueue<DistanceEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		DistanceEntry removed = null;
		int targetId = convertToInt(target.getId());

		init(source, target, queue, parents);

		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);		

			if(removed.getId() == targetId) {

				Path path = new Path();
				path.reconstructPath(removed.getId(), parents);

				return path;
			}

			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		throw new PathNotFoundException();
	}

	//TODO Create appropriate tests for this method!
	/**
	 * This method is going to calculate the shortest path from a node v to all other nodes.
	 * @param v
	 * @return
	 */
	public Int2ObjectMap<Path> shortestPath(Node source) {
		PriorityQueue<DistanceEntry> queue = new PriorityQueue<DistanceEntry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		DistanceEntry removed = null;
		Int2ObjectMap<Path> paths = new Int2ObjectOpenHashMap<Path>();

		init(source, source, queue, parents);

		while(!queue.isEmpty()) {
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);		

			if(removed.getId()!=0) {
				Path path = new Path();
				path.reconstructPath(removed.getId(), parents);
				paths.put(convertToInt(removed.getId()), path);
			}

			expandVertex(graph.getNode(removed.getId()), removed, wasTraversed, queue, parents);
		}

		return paths;

	}

	public void init(Node source, Node target, PriorityQueue<DistanceEntry> queue, 
			HashMap<Long, RouteEntry> parents){

		int sourceId = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sourceId, 0, -1));

	}

	public abstract void expandVertex(Node target, DistanceEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<DistanceEntry> queue, HashMap<Long, RouteEntry> parents);

}
