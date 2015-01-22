package org.graphast.query.route.shortestpath;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
				
				//TODO Revise this verification and Data Structure. 
				//Cases that must be checked: null == null, null == String, String == String
				if(verificationQueue.peek().getLabel() == re.getLabel() || verificationQueue.peek().getLabel().equals(re.getLabel())) {
					
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

	public int shortestPath(GraphastNode source, GraphastNode target, Date time) {
		PriorityQueue<Entry> queue = new PriorityQueue<Entry>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		Entry removed = null;
		int targetId = convertToInt(target.getId());
		int timeMillis = DateUtils.dateToMilli(time);

		init(source, target, queue, parents, timeMillis);

		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);		

			if(removed.getId() == targetId) {

				List<Instruction> path = reconstructPath(removed.getId(), parents);
				logger.info("path: {}", path);

				return removed.getTravelTime();
			}
			expandVertex(target, removed, wasTraversed, queue, parents);
		}
		throw new PathNotFoundException();
	}

	public void init(GraphastNode source, GraphastNode target, PriorityQueue<Entry> queue, 
			HashMap<Long, RouteEntry> parents, int arrivalTime){
		int sourceId = convertToInt(source.getId());

		queue.offer(new Entry(sourceId, 0, arrivalTime, -1));

		//parents.put((Integer) graphAdapter.getVertex(sid).getProperty(Property.ORGINALID), new RouteEntry(-1, 0));
	}

	public abstract void expandVertex(GraphastNode target, Entry removed, HashMap<Long, Integer> wasTraversed, 
			PriorityQueue<Entry> queue, HashMap<Long, RouteEntry> parents);

}
