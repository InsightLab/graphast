package org.graphast.model;


public interface GraphBounds extends Graph {

	public abstract void createBounds();
	
	public abstract int getEdgeLowerCost(long id);
	
	public abstract int getEdgeUpperCost(long id);
}