package org.graphast.model;

public class Edge extends GraphObject {
	
	private int fromNode;
	private int toNode;
	private double cost;
	private boolean bidirectional;
	
	public Edge(int from, int to) {
		this(from, to, 1);
	}
	
	public Edge(int from, int to, double cost) {
		this.fromNode = from;
		this.toNode= to;
		this.cost = cost;
	}
	
	public Edge(int from, int to, boolean bidirectional) {
		this(from, to, 1, bidirectional);
	}
	
	public Edge(int from, int to, double cost, boolean bidirectional) {
		this(from, to, cost);
		this.bidirectional = bidirectional;
	}
	
	public int getFromNodeId() {
		return fromNode;
	}
	
	public int getToNodeId() {
		return toNode;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setFromNodeId(int fromNode) {
		this.fromNode = fromNode;
	}
	
	public void setToNodeId(int toNode) {
		this.toNode = toNode;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public boolean isBidirectional() {
		return bidirectional;
	}
	
	public int getAdjacent(int id) {
		return id == toNode ? fromNode : toNode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Edge)) return false;
		Edge other = (Edge) obj;
		return (fromNode + "|" + toNode + "|" + cost)
				.equals(other.getFromNodeId()+"|"+other.getToNodeId()+"|"+other.getCost());
	}
	
	@Override
	public int hashCode() {
		return (fromNode + "|" + toNode + "|" + cost).hashCode();
	}
	
	@Override
	public String toString() {
		return fromNode + "|" + toNode + "|" + cost;
	}

}
