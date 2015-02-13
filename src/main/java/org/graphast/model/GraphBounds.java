package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public interface GraphBounds extends Graph {

	public abstract void createBounds();
	
	public abstract Long2IntMap getUpperBound();
	
	public abstract Long2IntMap getLowerBound();
	
	public Long2IntMap accessNeighborhood(Node v, short graphType);

}