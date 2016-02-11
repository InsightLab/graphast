package org.graphast.model;

import java.util.List;

import org.graphast.geometry.Point;

public interface Edge {
	
	/**
	 * Blocksize length of one edge on IntBigArrayBigList.  
	 */
	public static final short EDGE_BLOCKSIZE = 17;

	/**
	 * This method validates an edge if the distance is positive ,
	 * or if the "from" node is equal to the "to" node.
	 */
	public abstract void validate();

	/**
	 * This method returns the distance of a edge.
	 * @return edge distance
	 */
	public abstract int getDistance();

	/**
	 * This method returns the id of a edge.
	 * @return Edge id
	 */
	public abstract Long getId();

	/**
	 * This method returns the fromNode id of a edge.
	 * @return fromNode id
	 */
	public abstract long getFromNode();

	/**
	 * This method returns the toNode id of a edge.
	 * @return toNode id
	 */
	public abstract long getToNode();

	/**
	 * This method returns neighbor edge that has one end equal to fromNode of this edge.
	 * @return neighbor edge that has one end equal to fromNode of this edge
	 */
	public abstract long getFromNodeNextEdge();

	/**
	 * This method returns neighbor edge that has one end equal to toNode of this edge.
	 * @return neighbor edge that has one end equal to toNode of this edge
	 */
	public abstract long getToNodeNextEdge();

	/**
	 * This method returns a array containing all time costs.
	 * @return a array containing all time costs
	 */
	public abstract int[] getCosts();
	
	public void setCosts(int[] costs);

	/**
	 * This method returns a list of points that are part of the edge in the map.
	 * Useful to draw on the map.
	 * @return a list of points that are part of the edge in the map
	 */
	public abstract List<Point> getGeometry();

	/**
	 * This method returns the edge label.
	 * @return edge label
	 */
	public abstract String getLabel();

	/**
	 * toString method.
	 * @return toString
	 */
	public abstract String toString();
	
	public boolean equals(Edge e);
	
	public void addGeometryPoint(Point p);
	public void setGeometry(List<Point> geometry);
	
}