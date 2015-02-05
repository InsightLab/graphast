package org.graphast.query.route.shortestpath.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path {

	private long removedId;
	private HashMap<Long, RouteEntry> parents;
	List<Instruction> path;

	public Path() {

	//public Path(long removedId, HashMap<Long, RouteEntry> parents) {
		//this.removedId = removedId;
		//this.parents = parents;
		//reconstructPath(removedId, parents);

	}

	public void reconstructPath(long id, HashMap<Long, RouteEntry> parents) {
//	protected List<Instruction> reconstructPath(long id, HashMap<Long, RouteEntry> parents) {

		RouteEntry re = parents.get(id);
		long parent = re.getId();

		path = new ArrayList<Instruction>();
		LinkedList<Instruction> verificationQueue = new LinkedList<Instruction>();

		Instruction oldInstruction, newInstruction;
		newInstruction = new Instruction(0, re.getLabel(), re.getCost());

		verificationQueue.add(newInstruction);

		while(parent!=-1) {

			re = parents.get(parent);

			if(re != null) {

				String predecessorLabel = verificationQueue.peek().getLabel();
				
				if((predecessorLabel == null && re.getLabel() == null) || (predecessorLabel!=null  && predecessorLabel.equals(re.getLabel())) || (predecessorLabel!=null && (predecessorLabel.isEmpty() && re.getLabel()==null))) {

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

		//return path;

	}

	public double getPathCost() {
		
		double pathCost = 0d;
		
		Iterator<Instruction> instructionIterator = path.iterator();
		
		while(instructionIterator.hasNext()) {
			
			Instruction instruction = instructionIterator.next();
			pathCost = pathCost + instruction.getCost();
		}
		
		return pathCost;
		
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		
		Iterator<Instruction> instructionIterator = path.iterator();

		while(instructionIterator.hasNext()) {
			
			Instruction instruction = instructionIterator.next();
			sb.append("( ");
			
			sb.append(instruction.getDirection()).append(", ");
			sb.append(instruction.getLabel()).append(", ");
			sb.append(instruction.getCost());
			sb.append(" )");
			
		}
				
		return sb.toString();

	}

	public long getRemovedId() {
		return removedId;
	}

	public void setRemovedId(long removedId) {
		this.removedId = removedId;
	}

	public HashMap<Long, RouteEntry> getParents() {
		return parents;
	}

	public void setParents(HashMap<Long, RouteEntry> parents) {
		this.parents = parents;
	}

	public List<Instruction> getPath() {
		return path;
	}

	public void setPath(List<Instruction> path) {
		this.path = path;
	}

}
