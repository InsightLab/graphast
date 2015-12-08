package org.graphast.query.route.shortestpath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graphast.geometry.PoI;
import org.graphast.geometry.Point;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.query.route.osr.Sequence;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.graphast.util.DistanceUtils;

public class Path {

	private List<Point> geometry;
	private List<Long> edges;
	private List<Instruction> instructions;
	private List<PoI> listOfPoIs;
	private long totalDistance;
	private double totalCost;
	private int numberVisitedNodes;

	public Path() {

	}


	//TODO MAJOR REFACTOR NEEDED IN THIS METHOD!
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

		List<Point> listOfGeometries;

		Edge newEdge;

		if(re.getEdgeId()!=-1) {
			newEdge = graph.getEdge(re.getEdgeId());
			newInstruction = new Instruction(0, re.getLabel(), re.getCost(), newEdge.getDistance());
			edges.add(re.getEdgeId());

			if (newEdge.getGeometry() != null) {

				listOfGeometries = graph.getEdge(re.getEdgeId()).getGeometry();

				if(DistanceUtils.distanceLatLong(listOfGeometries.get(0).getLatitude(), listOfGeometries.get(0).getLongitude(), graph.getNode(graph.getEdge(re.getEdgeId()).getFromNode()).getLatitude(), graph.getNode(graph.getEdge(re.getEdgeId()).getFromNode()).getLongitude()) > 0) {
					Collections.reverse(listOfGeometries);
				}


				Collections.reverse(listOfGeometries);
				newInstruction.setStartGeometry(geometry.size());

				for (Point point : listOfGeometries) {
					if(geometry.contains(point)) {
						continue;
					} else {
						geometry.add(point);
					}
				}
				newInstruction.setEndGeometry(geometry.size()-1);
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
								oldInstruction.getCost() + re.getCost(), oldInstruction.getDistance() + newEdge.getDistance());
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

						listOfGeometries = graph.getEdge(re.getEdgeId()).getGeometry();

						if(geometry.size()!=0) {
							if(DistanceUtils.distanceLatLong(listOfGeometries.get(0).getLatitude(), listOfGeometries.get(0).getLongitude(), geometry.get(geometry.size()-1).getLatitude(), geometry.get(geometry.size()-1).getLongitude()) > 
							DistanceUtils.distanceLatLong(listOfGeometries.get(listOfGeometries.size()-1).getLatitude(), listOfGeometries.get(listOfGeometries.size()-1).getLongitude(), geometry.get(geometry.size()-1).getLatitude(), geometry.get(geometry.size()-1).getLongitude()) ) {
								Collections.reverse(listOfGeometries);
							}
						}

						for (Point point : listOfGeometries) {
							if(geometry.contains(point)) {
								//							if(previousLatitude==point.getLatitude() && previousLongitude==point.getLongitude()) {
								continue;
							} else {
								geometry.add(point);
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
		Collections.reverse(instructions);
	}



	public static Path pathsConcatanation(List<Path> paths) {

		Path path = new Path();

		List<Point> temporaryGeometry = new ArrayList<Point>();
		List<Long> temporaryEdges = new ArrayList<Long>();
		List<Instruction> temporaryInstructions = new ArrayList<Instruction>();
		List<PoI> temporaryListOfPoIs = new ArrayList<PoI>();
		long temporaryTotalDistance = 0;
		double temporaryTotalCost = 0;

		for(Path currentPath : paths) {
			
			for(Instruction instruction : currentPath.getInstructions()) {

				instruction.setStartGeometry(instruction.getStartGeometry() + temporaryGeometry.size());
				instruction.setEndGeometry(instruction.getEndGeometry()  + temporaryGeometry.size());

			}

			temporaryInstructions.addAll(currentPath.getInstructions());

			temporaryGeometry.addAll(currentPath.getGeometry());
			temporaryEdges.addAll(currentPath.getEdges());

			temporaryListOfPoIs.addAll(currentPath.getListOfPoIs());
			temporaryTotalDistance = temporaryTotalDistance + currentPath.getTotalDistance();
			temporaryTotalCost = temporaryTotalCost + currentPath.getTotalCost();
			
		}


		path.setGeometry(temporaryGeometry);
		path.setEdges(temporaryEdges);
		path.setInstructions(temporaryInstructions);
		path.setListOfPoIs(temporaryListOfPoIs);
		path.setTotalDistance(temporaryTotalDistance);
		path.setTotalCost(temporaryTotalCost);

		return path;

	}

	public void setTotalDistance(long totalDistance) {
		this.totalDistance = totalDistance;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public List<PoI> getListOfPoIs() {
		return listOfPoIs;
	}

	public void setListOfPoIs(List<PoI> listOfPoIs) {
		this.listOfPoIs = listOfPoIs;
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
				instructions.addAll(partialPath.getPath());
			}
			inicioId = sequence.getPois().get(i).getId();
		}
		Path partialPath = sp.shortestPath(inicioId, graph.getNodeId(lat2, lon2));
		if(inicioId != graph.getNodeId(lat2, lon2)) {
			geometry.addAll(partialPath.getGeometry());
			edges.addAll(partialPath.getEdges());
			instructions.addAll(partialPath.getPath());
		}
		Path path = new Path();
		path.setEdges(edges);
		path.setGeometry(geometry);
		path.setPath(instructions);
		return path;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		Iterator<Instruction> instructionIterator = instructions.iterator();

		while (instructionIterator.hasNext()) {

			Instruction instruction = instructionIterator.next();
			sb.append("(");

			sb.append(instruction.getDirection()).append(",");
			sb.append(instruction.getLabel()).append(",");
			sb.append(instruction.getCost()).append(",");
			sb.append(instruction.getDistance());
			sb.append(")");
			sb.append("\n");

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

	public List<Instruction> getInstructions() {
		return instructions;
	}


	public void setInstructions(List<Instruction> instructions) {
		this.instructions = instructions;
	}


	public List<Point> getGeometry() {
		return geometry;
	}

	public void setGeometry(List<Point> geometry) {
		this.geometry = geometry;
	}

	public long getTotalDistance() {

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


	public int getNumberVisitedNodes() {
		return numberVisitedNodes;
	}


	public void setNumberVisitedNodes(int numberVisitedNodes) {
		this.numberVisitedNodes = numberVisitedNodes;
	}
}