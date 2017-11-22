package br.ufc.insightlab.graphast.query.utils;

public class DistanceElement implements Comparable<DistanceElement>{
	
	private Long nodeId;
	private Long parentId;
	private double distance;
	private boolean visited;
	
	public DistanceElement(Long nodeId) {
		this.nodeId = nodeId;
		this.distance = Double.MAX_VALUE;
		parentId = -1l;
		visited = false;
	}
	
	public void changePrevious(long newParentId) {
		parentId = newParentId;
	}
	
	public void changeDistance(double newDistance) {
		distance = newDistance;
	}
	
	public long getNodeId() {
		return this.nodeId;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public long getParentId() {
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