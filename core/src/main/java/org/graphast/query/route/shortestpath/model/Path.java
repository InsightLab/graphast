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
import org.graphast.query.route.osr.Sequence;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;

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

		
		double previousLatitude=0;
		double previousLongitude=0;

		Edge newEdge; 

		if(re.getEdgeId()!=-1) {
			newEdge = graph.getEdge(re.getEdgeId());
			newInstruction = new Instruction(0, re.getLabel(), re.getCost(), newEdge.getDistance());
			edges.add(re.getEdgeId());
			
			if (newEdge.getGeometry() != null) {
				for (Point point : newEdge.getGeometry()) {
					if(previousLatitude==point.getLatitude() && previousLongitude==point.getLongitude()) {
						continue;
					} else {
						geometry.add(point);
						previousLatitude = point.getLatitude();
						previousLongitude = point.getLongitude();
					}
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
				if(re.getEdgeId()!=-1) {
					newEdge = graph.getEdge(re.getEdgeId());
				} else {
					newEdge = null;
				}
				
				if ((predecessorLabel == null && re.getLabel() == null)
						|| (predecessorLabel != null && predecessorLabel.equals(re.getLabel()))
						|| (predecessorLabel != null && (predecessorLabel.isEmpty() && re.getLabel() == null))) {
					oldInstruction = verificationQueue.poll();
					if(re.getEdgeId()!=-1) {
						newInstruction = new Instruction(0, oldInstruction.getLabel(),
							oldInstruction.getCost() + re.getCost(), newEdge.getDistance());
					} else {
						newInstruction = new Instruction(0, oldInstruction.getLabel(),
								oldInstruction.getCost() + re.getCost(), 0);
					}
					newInstruction.setStartGeometry(oldInstruction.getStartGeometry());	
				} else {
					if(re.getEdgeId()!=-1) {
						newInstruction = new Instruction(0, re.getLabel(), re.getCost(), newEdge.getDistance());
					} else {
						newInstruction = new Instruction(0, re.getLabel(), re.getCost(), 0);
					}
					newInstruction.setStartGeometry(geometry.size()-1);
				}
				edges.add(re.getEdgeId());

				if(re.getEdgeId()!=-1) {
					if (newEdge.getGeometry() != null) {
						for (Point point : newEdge.getGeometry()) {
							if(previousLatitude==point.getLatitude() && previousLongitude==point.getLongitude()) {
								continue;
							} else {
								geometry.add(point);
								previousLatitude = point.getLatitude();
								previousLongitude = point.getLongitude();
							}
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
		Collections.reverse(geometry);
		while (!verificationQueue.isEmpty()) {
			instructions.add(verificationQueue.poll());
		}
	}
	
	public Path generatePath(double lat1, double lon1, double lat2, double lon2, Sequence sequence, Graph graph) {
		List<Point> geometry = new ArrayList<Point>();
		List<Long> edges = new ArrayList<Long>();
		List<Instruction> instructions = new ArrayList<Instruction>();
		long inicioId = graph.getNodeId(lat1, lon1);
		AbstractShortestPathService sp = new DijkstraLinearFunction(graph);
		for(int i = 0; i < sequence.getPois().size(); i++) {
			Path partialPath = sp.shortestPath(inicioId, sequence.getPois().get(i).getId());
			if(inicioId != sequence.getPois().get(i).getId()) {
				geometry.addAll(partialPath.getGeometry());
				edges.addAll(partialPath.getEdges());
				instructions.addAll(partialPath.getInstructions());
			}
			inicioId = sequence.getPois().get(i).getId();
		}
		Path partialPath = sp.shortestPath(inicioId, graph.getNodeId(lat2, lon2));
		if(inicioId != graph.getNodeId(lat2, lon2)) {
			geometry.addAll(partialPath.getGeometry());
			edges.addAll(partialPath.getEdges());
			instructions.addAll(partialPath.getInstructions());
		}
		Path path = new Path();
		path.setEdges(edges);
		path.setGeometry(geometry);
		path.setInstructions(instructions);
		return path;
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

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<Instruction> instructions) {
		this.instructions = instructions;
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
