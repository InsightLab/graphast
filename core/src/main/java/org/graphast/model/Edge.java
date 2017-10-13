package org.graphast.model;

public class Edge {
	
	private Node fromNode;
	private Node toNode;
	private double cost;
	private boolean bidirectional;
	
	public Edge(Node from, Node to) {
		this(from, to, 1);
	}
	
	public Edge(Node from, Node to, double cost) {
		this(from, to, cost, false);
	}
	
	public Edge(Node from, Node to, boolean bidirectional) {
		this(from, to, 1, bidirectional);
	}
	
	public Edge(Node from, Node to, double cost, boolean bidirectional) {
		this.fromNode = from;
		this.toNode= to;
		this.cost = cost;
		this.bidirectional = bidirectional;
	}
	
	public Node getFromNode() {
		return fromNode;
	}
	
	public Node getToNode() {
		return toNode;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}
	
	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public boolean isBidirectional() {
		return bidirectional;
	}

}
