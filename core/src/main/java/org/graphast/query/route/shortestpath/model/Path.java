package org.graphast.query.route.shortestpath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path {

	private List<Instruction> path;
	private List<Long> edges;

	public Path() {

	}

	//TODO Rename to constructPath
	public void reconstructPath(long id, HashMap<Long, RouteEntry> parents) {
		Instruction oldInstruction, newInstruction;
		LinkedList<Instruction> verificationQueue = new LinkedList<Instruction>();
		if(parents.get(id) == null) {
			path = new ArrayList<Instruction>();
			newInstruction = new Instruction(0, "On Start", 0);
			path.add(newInstruction);
			return;
		}
		RouteEntry re = parents.get(id);

		long parent = re.getId();

		path = new ArrayList<Instruction>();
		edges = new ArrayList<Long>();
		
		newInstruction = new Instruction(0, re.getLabel(), re.getCost());
		edges.add(re.getEdgeId());
		verificationQueue.add(newInstruction);

		while(parent!=-1) {
			re = parents.get(parent);
			if(re != null) {
				String predecessorLabel = verificationQueue.peek().getLabel();

				if((predecessorLabel == null && re.getLabel() == null) || (predecessorLabel!=null  && predecessorLabel.equals(re.getLabel())) || (predecessorLabel!=null && (predecessorLabel.isEmpty() && re.getLabel()==null))) {
					oldInstruction = verificationQueue.poll();
					newInstruction = new Instruction(0, oldInstruction.getLabel(), oldInstruction.getCost() + re.getCost());
				} else {
					newInstruction = new Instruction(0, re.getLabel(), re.getCost());
				}
				edges.add(re.getEdgeId());
				verificationQueue.addFirst(newInstruction);
				parent = re.getId();
			} else {
				break;
			}
		}
		
		Collections.reverse(edges);
		while(!verificationQueue.isEmpty()) {
			path.add(verificationQueue.poll());
		}
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

	public List<Instruction> getPath() {
		return path;
	}

	public void setPath(List<Instruction> path) {
		this.path = path;
	}

	public List<Long> getEdges() {
		return edges;
	}

	public void setEdges(List<Long> edges) {
		this.edges = edges;
	}
	
	

}
