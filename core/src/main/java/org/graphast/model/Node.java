package org.graphast.model;

import java.util.ArrayList;

public class Node {
	
	private int id;
	
	private ArrayList<Edge> outEdges = new ArrayList<>();
	private ArrayList<Edge> inEdges = new ArrayList<>();
	
	public Node(int id) {
		this.id = id;
	}
	
	public void addEdge(Edge e) {
		if (e.isBidirectional()) {
			inEdges.add(e);
			outEdges.add(e);
		}
		else if (e.getFromNode() == this) {
			outEdges.add(e);
		}
		else if (e.getToNode() == this) {
			inEdges.add(e);
		}
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Edge> getOutEdges() {
		return outEdges;
	}
	
	public ArrayList<Edge> getInEdges() {
		return inEdges;
	}
	
	public ArrayList<Node> getNeighborhood() {
		ArrayList<Node> neighbors = new ArrayList<>();
		for (Edge e : this.getOutEdges())
			neighbors.add((e.getFromNode() == this) ? e.getToNode() : e.getFromNode());
		return neighbors;
	}

}
