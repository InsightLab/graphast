package org.graphast.query.utils;

public class DistanceElement implements Comparable<DistanceElement>{
	
	private Integer nodeId;
	private Integer parentId;
	private double distance;
	private boolean visited;
	
	public DistanceElement(Integer nodeId) {
		this.nodeId = nodeId;
		this.distance = Double.MAX_VALUE;
		parentId = -1;
		visited = false;
	}
	
	public void changePrevious(int newParentId) {
		parentId = newParentId;
	}
	
	public void changeDistance(double newDistance) {
		distance = newDistance;
	}
	
	public int getNodeId() {
		return this.nodeId;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public int getParentId() {
		return parentId;
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