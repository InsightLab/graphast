/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package org.insightlab.graphast.query.utils;

/**
 * The Distance Element class. It represents a node in the graph during the shortest path strategy calculation.
 * It implements the Comparable interface, this way the distance attribute can be compared between the differents
 * instances of this class easily.
 */
public class DistanceElement implements Comparable<DistanceElement>{
	
	private Long nodeId;
	private Long parentId;
	private double distance;
	private boolean visited;
	
	/**
	 * Instantiates a new distance element, given a node id. The distance is initially set to the max double value, 
	 * the parent id of this node is set to -1 and it is initially not visited by any strategy.
	 *
	 * @param nodeId the node id of the instantiated element.
	 */
	public DistanceElement(Long nodeId) {
		
		this.nodeId = nodeId;
		this.distance = Double.MAX_VALUE;
		
		parentId = -1l;
		visited = false;
		
	}
	
	/**
	 * Change previous function. In this function a new parent node id is set to this element.
	 *
	 * @param newParentId the new parent node id.
	 */
	public void changePrevious(long newParentId) {
		parentId = newParentId;
	}
	
	/**
	 * Change distance function. This functions sets a new value for the distance attribute of this element.
	 *
	 * @param newDistance the new distance value.
	 */
	public void changeDistance(double newDistance) {
		distance = newDistance;
	}
	
	/**
	 * This function returns the id of the node represented by this element.
	 *
	 * @return the node id of this element.
	 */
	public long getNodeId() {
		return this.nodeId;
	}
	
	/**
	 * This functions gets the value for the distance attribute of this element.
	 *
	 * @return the distance value.
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * This function returns the parent node id of this element.
	 *
	 * @return the parent node id.
	 */
	public long getParentId() {
		return parentId;
	}
	
	/**
	 * This function checks whether this element has been visited or not by some shortest path strategy.
	 *
	 * @return true, if the node represented by this element has been visited. false, otherwise.
	 */
	public boolean isVisited() {
		return visited;
	}
	
	/**
	 * This function sets a new value for the visited attribute, indicating whether this element was
	 * visited or not.
	 *
	 * @param b the new visited value.
	 */
	public void setVisited(boolean b) {
		visited = b;
	}

	/**
	 * Compare to function (overridden). It takes another DistanceElement object as input and compares
	 * their distances to indicate which one has the smaller distance value. It is an implementation
	 * of the Comparable interface's method, to indicate how to compare two objects of the same class.
	 *
	 * @param o the DistanceElement object to compare with the onde calling this function.
	 * @return the value 0 if this object's distance is numerically equal to o's; 
	 * a value less than 0 if this object's distance is numerically less than o's; 
	 * and a value greater than 0 if this object's distance is numerically greater than o's.
	 */
	@Override
	public int compareTo(DistanceElement o) {
		return Double.compare(this.distance,o.getDistance());
	}
	
}