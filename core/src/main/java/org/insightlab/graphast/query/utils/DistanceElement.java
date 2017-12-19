package org.insightlab.graphast.query.utils;

/**
 * The Class DistanceElement.
 */
public class DistanceElement implements Comparable<DistanceElement>{
	
	private Long nodeId;
	private Long parentId;
	private double distance;
	private boolean visited;
	
	/**
	 * Instantiates a new distance element.
	 *
	 * @param nodeId the node id
	 */
	public DistanceElement(Long nodeId) {
		this.nodeId = nodeId;
		this.distance = Double.MAX_VALUE;
		parentId = -1l;
		visited = false;
	}
	
	/**
	 * Change previous.
	 *
	 * @param newParentId the new parent id
	 */
	public void changePrevious(long newParentId) {
		parentId = newParentId;
	}
	
	/**
	 * Change distance.
	 *
	 * @param newDistance the new distance
	 */
	public void changeDistance(double newDistance) {
		distance = newDistance;
	}
	
	/**
	 * Gets the node id.
	 *
	 * @return the node id
	 */
	public long getNodeId() {
		return this.nodeId;
	}
	
	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Gets the parent id.
	 *
	 * @return the parent id
	 */
	public long getParentId() {
		return parentId;
	}
	
	/**
	 * Checks if is visited.
	 *
	 * @return true, if is visited
	 */
	public boolean isVisited() {
		return visited;
	}
	
	/**
	 * Sets the visited.
	 *
	 * @param b the new visited
	 */
	public void setVisited(boolean b) {
		visited = b;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(DistanceElement o) {
		return Double.compare(this.distance,o.getDistance());
	}
	
}