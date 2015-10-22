package org.graphast.query.route.shortestpath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graphast.geometry.Point;
import org.graphast.model.Edge;
import org.graphast.model.Graph;

public class Path {
	
	private List<Point> geometry;
	private List<Long> edges;
	private List<Instruction> instructions;
	private double totalDistance;
	private double totalCost;

	public Path() {

	}

	// TODO Rename to constructPath
	public void constructPath(long id, HashMap<Long, RouteEntry> parents, Graph graph) {
		Instruction oldInstruction, newInstruction;
		LinkedList<Instruction> verificationQueue = new LinkedList<Instruction>();
		if (parents.get(id) == null) {
			instructions = new ArrayList<Instruction>();
			newInstruction = new Instruction(0, "On Start", 0, 0);
			instructions.add(newInstruction);
			return;
		}
		RouteEntry re = parents.get(id);

		long parent = re.getId();

		instructions = new ArrayList<Instruction>();
		edges = new ArrayList<Long>();
		geometry = new ArrayList<Point>();


//		newEdge = graph.getEdge(re.getEdgeId());

		if(re.getEdgeId()!=-1) {
			newInstruction = new Instruction(0, re.getLabel(), re.getCost(), graph.getEdge(re.getEdgeId()).getDistance());
			edges.add(re.getEdgeId());
			
			if (graph.getEdge(re.getEdgeId()).getGeometry() != null) {
				for (Point point : graph.getEdge(re.getEdgeId()).getGeometry()) {
					geometry.add(point);
				}
			}
		} else {
			newInstruction = new Instruction(0, re.getLabel(), re.getCost(), 0);
			edges.add(re.getEdgeId());
		}

		

		verificationQueue.add(newInstruction);

		while (parent != -1) {
			re = parents.get(parent);

			if (re != null) {
				String predecessorLabel = verificationQueue.peek().getLabel();
//				newEdge = graph.getEdge(re.getEdgeId());
				
				if ((predecessorLabel == null && re.getLabel() == null)
						|| (predecessorLabel != null && predecessorLabel.equals(re.getLabel()))
						|| (predecessorLabel != null && (predecessorLabel.isEmpty() && re.getLabel() == null))) {
					oldInstruction = verificationQueue.poll();
					if(re.getEdgeId()!=-1) {
						newInstruction = new Instruction(0, oldInstruction.getLabel(),
							oldInstruction.getCost() + re.getCost(), graph.getEdge(re.getEdgeId()).getDistance());
					} else {
						newInstruction = new Instruction(0, oldInstruction.getLabel(),
								oldInstruction.getCost() + re.getCost(), 0);
					}
					newInstruction.setStartGeometry(oldInstruction.getStartGeometry());	
				} else {
					if(re.getEdgeId()!=-1) {
						newInstruction = new Instruction(0, re.getLabel(), re.getCost(), graph.getEdge(re.getEdgeId()).getDistance());
					} else {
						newInstruction = new Instruction(0, re.getLabel(), re.getCost(), 0);
					}
					newInstruction.setStartGeometry(geometry.size()-1);
				}
				edges.add(re.getEdgeId());

				if(re.getEdgeId()!=-1) {
					if (graph.getEdge(re.getEdgeId()).getGeometry() != null) {
						for (Point point : graph.getEdge(re.getEdgeId()).getGeometry()) {
							geometry.add(point);
						}
					}
					newInstruction.setEndGeometry(geometry.size()-1);
				}

				verificationQueue.addFirst(newInstruction);
				parent = re.getId();
			} else {
				break;
			}
		}

		Collections.reverse(edges);
		while (!verificationQueue.isEmpty()) {
			instructions.add(verificationQueue.poll());
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		Iterator<Instruction> instructionIterator = instructions.iterator();

		while (instructionIterator.hasNext()) {

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
		return instructions;
	}

	public void setPath(List<Instruction> path) {
		this.instructions = path;
	}

	public List<Long> getEdges() {
		return edges;
	}

	public void setEdges(List<Long> edges) {
		this.edges = edges;
	}

	public List<Point> getGeometry() {
		return geometry;
	}

	public void setGeometry(List<Point> geometry) {
		this.geometry = geometry;
	}

	public double getTotalDistance() {

		totalDistance = 0;

		for (Instruction instruction : instructions) {
			totalDistance = totalDistance + instruction.getDistance();
		}

		return totalDistance;
	}

	public double getTotalCost() {

		totalCost = 0;

		for (Instruction instruction : instructions) {
			totalCost = totalCost + instruction.getCost();
		}
		return totalCost;
	}

}
