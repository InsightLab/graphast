package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;

public interface GraphBounds extends Graph {

	public abstract void createBounds();
	
	public abstract Long2ShortMap getUpperBound();
	
	public abstract Long2ShortMap getLowerBound();

}