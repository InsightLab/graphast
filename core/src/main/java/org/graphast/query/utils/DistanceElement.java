package org.graphast.query.utils;

import org.graphast.model.Node;

public class DistanceElement implements Comparable<DistanceElement>{
	
	private Node node;
	private Node parent;
	private double distance;
	private boolean visited;
	
	public DistanceElement(Node node) {
		this.node = node;
		this.distance = Double.MAX_VALUE;
		parent = null;
		visited = false;
	}
	
	public void changePrevious(Node newParent) {
		parent = newParent;
	}
	
	public void changeDistance(double newDistance) {
		distance = newDistance;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean b) {
		visited = b;
	}

	@Override
	public int compareTo(DistanceElement o) {
		return Double.compare(this.distance,o.getDistance());
	}
	
}