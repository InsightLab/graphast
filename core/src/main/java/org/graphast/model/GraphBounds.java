package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;


public interface GraphBounds extends Graph {

	public abstract void createBounds();
	
	public abstract Long2IntMap getEdgesUpperBound();
	
	public abstract Long2IntMap getEdgesLowerBound();
	
	public abstract Long2IntMap getNodesUpperBound();
	
	public abstract Long2IntMap getNodesLowerBound();
	
	public Long2IntMap accessNeighborhood(Node v, short graphType, int time);
	
	public int poiGetCost(long vid, short graphType);
	
	public abstract int getEdgeLowerCost(long id);
	
	public abstract int getEdgeUpperCost(long id);
	
	public GraphBounds getReverseGraph();

}