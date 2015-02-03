package org.graphast.model;


public interface GraphBounds extends Graph {

	public abstract void createBounds();
	
	public abstract int getEdgeLowerCost(long id);
	
<<<<<<< HEAD
	public abstract int getEdgeUpperCost(long id);
=======
	public abstract Long2IntMap getLowerBound();

	public abstract int getEdgeLowerCost(long id);
	
	public abstract int getEdgeUpperCost(long id);

>>>>>>> new methods on GraphBounds* and some adjustments on KNNSearchTest(not working yet).
}