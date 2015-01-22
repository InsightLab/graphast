package org.graphast.query.route.shortestpath.dijkstra;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Instruction;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DijkstraShortestPath extends AbstractShortestPathService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public DijkstraShortestPath(Graph graph) {
		super(graph);
	}

	protected List<Instruction> reconstructPath(long id, HashMap<Long, RouteEntry> parents) {

		RouteEntry re = parents.get(id);
		long parent = re.getId();

		List<Instruction> path = new ArrayList<Instruction>();
		LinkedList<Instruction> verificationQueue = new LinkedList<Instruction>();
		
		Instruction oldInstruction, newInstruction;
		newInstruction = new Instruction(0, re.getLabel(), re.getCost());
		
		verificationQueue.add(newInstruction);
		
		while(parent!=-1) {
			
			re = parents.get(parent);
			
			if(re != null) {
				
				//TODO Use equals
				if((verificationQueue.peek().getLabel() == null && re.getLabel() == null) || verificationQueue.peek().getLabel().equals(re.getLabel())) {
					
					oldInstruction = verificationQueue.poll();
					newInstruction = new Instruction(0, oldInstruction.getLabel(), oldInstruction.getCost() + re.getCost());
					verificationQueue.addFirst(newInstruction);
					
				} else {
					
					newInstruction = new Instruction(0, re.getLabel(), re.getCost());
					verificationQueue.addFirst(newInstruction);
				
				}
				
				parent = re.getId();
				
			} else {
				
				break;
			
			}
		
		}
		
		while(!verificationQueue.isEmpty()) {
			path.add(verificationQueue.poll());
		}
		
 		return path;

	}

	public int shortestPath(Node source, Node target, Date time) {
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

				List<Instruction> path = reconstructPath(removed.getId(), parents);
				logger.info("path: {}", path);

				return removed.getDistance();
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
