package org.graphast.query.route.shortestpath.dijkstra;

import static org.graphast.util.NumberUtils.convertToInt;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Dijkstra extends AbstractShortestPathService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
				
//				List<Instruction> path = reconstructPath(removed.getId(), parents);
//				logger.info("path: {}", path);

//				return removed.getDistance();
				
				return path;
			}
			
			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		throw new PathNotFoundException();
	}

	public void init(Node source, Node target, PriorityQueue<DistanceEntry> queue, 
			HashMap<Long, RouteEntry> parents){
		int sourceId = convertToInt(source.getId());

		queue.offer(new DistanceEntry(sourceId, 0, -1));

		//parents.put((Integer) graphAdapter.getVertex(sid).getProperty(Property.ORGINALID), new RouteEntry(-1, 0));
	}

	public abstract void expandVertex(Node target, DistanceEntry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<DistanceEntry> queue, HashMap<Long, RouteEntry> parents);

}
