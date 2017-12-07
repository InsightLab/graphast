package org.insightlab.graphast.model;

public class Edge extends GraphObject {
	
	private long fromNode;
	private long toNode;
	private double cost;
	private boolean bidirectional;
	
	public Edge(long from, long to) {
		this(from, to, 1);
	}
	
	public Edge(long from, long to, double cost) {
		this.fromNode = from;
		this.toNode= to;
		this.cost = cost;
	}
	
	public Edge(long from, long to, boolean bidirectional) {
		this(from, to, 1, bidirectional);
	}
	
	public Edge(long from, long to, double cost, boolean bidirectional) {
		this(from, to, cost);
		this.bidirectional = bidirectional;
	}
	
	public long getFromNodeId() {
		return fromNode;
	}
	
	public long getToNodeId() {
		return toNode;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setFromNodeId(long fromNode) {
		this.fromNode = fromNode;
	}
	
	public void setToNodeId(long toNode) {
		this.toNode = toNode;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public boolean isBidirectional() {
		return bidirectional;
	}
	
	public long getAdjacent(long id) {
		return id == toNode ? fromNode : toNode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Edge)) return false;
		Edge other = (Edge) obj;
		return (this.fromNode==other.fromNode && this.toNode==other.toNode &&
				this.cost==other.cost && this.bidirectional==other.bidirectional);
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
