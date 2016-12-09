package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

/**
 * Interface for handle graph with lowerBound  and upperBound function
 */
public interface GraphBounds extends Graph {

	/**
	 * This method pick all edges and nodes of the graph and create in form of upperBound and lowerBound.
	 */
	public abstract void createBounds();
	
	/**
	 * simple method get
	 * @return all edges upper bound
	 */
	public abstract Long2IntMap getEdgesUpperBound();
	
	/**
	 * simple method get
	 * @return all edges lower bound
	 */
	public abstract Long2IntMap getEdgesLowerBound();
	
	/**
	 * simple method get
	 * @return all nodes upper bound
	 */
	public abstract Long2IntMap getNodesUpperBound();
	
	/**
	 * simple method get
	 * @return all nodes lower bound
	 */
	public abstract Long2IntMap getNodesLowerBound();
	
	/**
	 * This method return a list of nodes that are neighbors of a given node. 
	 * This list contains node id and cost to reach it.
	 * 
	 * @param v Node which method is applied.
	 * @param graphType The type of graph the will be used to retrieve costs needed. 0 = Regular Costs; 1 = Lower Bound Costs;
	 * 					3 = Upper Bound Costs.
	 * @param time	The time that will be used to get the time-dependent cost
	 * @return	all neighbors for the given parameters
	 */
	
	public Long2IntMap accessNeighborhood(Node v, short graphType, int time);
	
	
	public int poiGetCost(long vid, short graphType);
	
	/**
	 * simple method get
	 * @param id from the edge 
	 * @return the lower cost
	 */
	public abstract int getEdgeLowerCost(long id);
	
	/**
	 * simple method get
	 * @param id from the edge 
	 * @return the upper cost
	 */
	public abstract int getEdgeUpperCost(long id);
	
	/**
	 * simple method get
	 * @return a reverse GraphBounds
	 */
	public GraphBounds getReverseGraph();

}