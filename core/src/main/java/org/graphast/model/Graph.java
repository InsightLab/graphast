package org.graphast.model;

import java.util.HashMap;
import java.util.List;

import org.graphast.enums.CompressionType;
import org.graphast.enums.TimeType;
import org.graphast.geometry.BBox;
import org.graphast.geometry.PoI;
import org.graphast.geometry.Point;
import org.slf4j.Logger;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;

public interface Graph {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#save()
	 */
	void save();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#load()
	 */
	void load();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#addNode(org.graphast.model.GraphastNode)
	 */
	void addNode(Node n);

	// TODO Why we only update the latitude, longitude and FirstEdge?
	// Wouldn't be better if we had a method that updates everything?
	/**
	 * This method will update the IntBigArrayBigList of nodes with need
	 * information of a passed GraphastNode.
	 * 
	 * @param n
	 *            GraphastNode with the informations that must be updated.
	 */
	void updateNodeInfo(Node n);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNode(long)
	 */
	Node getNode(long id);

	Edge getEdge(long originNodeId, long destinationNodeId);

	// TODO Suggestion: delete this method and keep all these operations in
	// updateEdgeInfo
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#setEdge(org.graphast.model.GraphastEdge,
	 * long)
	 */
	void setEdge(Edge e, long pos);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#addEdge(org.graphast.model.GraphastEdge)
	 */
	void addEdge(Edge e);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#updateNeighborhood(org.graphast.model.
	 * GraphastEdge)
	 */
	void updateNeighborhood(Edge edge);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphast.model.Graphast#updateNodeNeighborhood(org.graphast.model
	 * .GraphastNode, long)
	 */
	public LongList getOutEdges(long nodeId);
	
	/**
	 * This method will return all ingoing edges for a given node.
	 * 
	 * @param nodeId Given node id.
	 * @return Ingoing edges for a given node.
	 */
	public LongList getInEdges(long nodeId);
	void updateNodeNeighborhood(Node n, long eid);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutEdges(long)
	 */
	public int[] getEdgesCosts(LongList edges, int time);
	
	//TODO documentation
	public int[] getNodeCosts(long id);
	
	/**
	 * This method returns all out neighbors of a given node. 
	 * @param vid of a node
	 * @return a list of all out neighboring nodes

	/**
	 * This method returns all out neighbors of a given node with the costs based on a given time.
	 * @param vid Id of a node
	 * @param time Time used to get edge costs.
	 * @return a list of all out neighboring nodes with costs
	 */
	LongList getOutNeighbors(long vid);

	/**
	 * This method returns all in neighbors of a given node. 
	 * @param vid of a node
	 * @return a list of all in neighboring nodes
	 */
	public LongList getInNeighbors(long vid);

	/**
	 * This method returns all in neighbors of a given node with the costs based on a given time.
	 * @param vid Id of a node
	 * @param time Time used to get edge costs.
	 * @return a list of all in neighboring nodes with costs
	 */
	public LongList getInNeighborsAndCosts(long vid, int time);

	
	/**
	 * This method returns a Edge for a given edgeId.
	 * @param id Id of a edge
	 * @return Edge
	 */
	LongList getOutNeighborsAndCosts(long vid, int time);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdge(long)
	 */
	Edge getEdge(long id);

	int[] getEdgeCosts(long edgeId);

	int[] getNodeCostsByCostsIndex(long costsIndex);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphast.model.Graphast#getEdgeCost(org.graphast.model.GraphastEdge,
	 * int)
	 */
	// TODO getEdgeCost
	Integer getEdgeCost(Edge e, int time);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdgePoints(long)
	 */
	List<Point> getGeometry(long id);

	List<Point> getGeometryByGeometryIndex(long geometryIndex);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNode(double, double)
	 */
	Long getNodeId(double latitude, double longitude);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNodeLabel(long)
	 */
	String getNodeLabel(long id);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdgeLabel(long)
	 */
	String getEdgeLabel(long id);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNodes()
	 */
	IntBigArrayBigList getNodes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdges()
	 */
	IntBigArrayBigList getEdges();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#logNodes()
	 */
	void logNodes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#logEdges()
	 */
	void logEdges();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNumberOfNodes()
	 */
	long getNumberOfNodes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNumberOfEdges()
	 */
	long getNumberOfEdges();

	Long2IntMap accessNeighborhood(Node v);

	// TODO Reimplement this method
	HashMap<Node, Integer> accessNeighborhood(Node v, int time);

	boolean hasNode(long id);

	boolean hasNode(Node n);

	boolean hasNode(double latitude, double longitude);

	Node addPoi(long id, double lat, double lon, int category, LinearFunction[] costs);

	Node addPoi(long id, double lat, double lon, int category);

	int poiGetCost(long vid, int time);

	int poiGetCost(long vid);

	int[] getPoiCost(long vid);

	LinearFunction[] convertToLinearFunction(int[] costs);

	int getMaximunCostValue(int[] costs);

	int getMinimunCostValue(int[] costs);

	boolean isPoi(long vid);

	Node getPoi(long vid);

	// TODO Verify if this access is correct
	IntSet getCategories();

	CompressionType getCompressionType();

	void setCompressionType(CompressionType compressionType);

	void reverseGraph();

	int getMaxTime();

	void setMaxTime(int maxTime);

	TimeType getTimeType();

	void setTimeType(TimeType timeType);

	void setEdgeCosts(long edgeId, int[] costs);

	void setNodeCosts(long nodeId, int[] costs);

	int getArrival(int dt, int tt);

	// TODO This method must be improved. It should use a spatial index to 
	// be much more efficient.
	// See rtree implementation in: https://github.com/davidmoten/rtree 
	Node getNearestNode(double latitude, double longitude);

	boolean equals(Graph obj);

	void setNodeCategory(long nodeId, int category);

	void setEdgeGeometry(long edgeId, List<Point> geometry);

	BBox getBBox();

	void setBBox(BBox bBox);

	List<PoI> getPOIs();

	List<PoI> getPOIs(Integer categoryId);

	List<Integer> getPOICategories();

	String getDirectory();

	String getAbsoluteDirectory();

	void setDirectory(String directory);

	void createEdgesLowerBounds();

	void createEdgesUpperBounds();

	void createNodesLowerBounds();

	void createNodesUpperBounds();

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
	Long2IntMap accessNeighborhood(Node v, short graphType, int time);

	int poiGetCost(long vid, short graphType);

	void createBounds();

	Long2IntMap getEdgesUpperBound();

	Long2IntMap getEdgesLowerBound();

	Long2IntMap getNodesUpperBound();

	int getEdgeLowerCost(long id);

	Long2IntMap getNodesLowerBound();

	int getEdgeUpperCost(long id);

	GraphBounds getReverseGraphBounds();

	Graph getReverseGraph();

	Logger getLog();

	Long2LongMap getNodeIndex();

	IntBigArrayBigList getEdgesCosts();

	IntBigArrayBigList getPoints();

	int getBlockSize();

	int[] getIntCosts();

	BBox getbBox();

	void setLog(Logger log);

	void setNodeIndex(Long2LongMap nodeIndex);

	void setAbsoluteDirectory(String absoluteDirectory);

	void setNodes(IntBigArrayBigList nodes);

	void setEdges(IntBigArrayBigList edges);

	void setEdgesCosts(IntBigArrayBigList edgesCosts);

	void setNodesCosts(IntBigArrayBigList nodesCosts);

	void setPoints(IntBigArrayBigList points);

	void setBlockSize(int blockSize);

	void setIntCosts(int[] intCosts);

	void setbBox(BBox bBox);

	void setEdgesUpperBound(Long2IntMap edgesUpperBound);

	void setEdgesLowerBound(Long2IntMap edgesLowerBound);

	void setNodesUpperBound(Long2IntMap nodesUpperBound);

	void setNodesLowerBound(Long2IntMap nodesLowerBound);

	void setReverseGraph(GraphBounds reverseGraph);
	
	ObjectBigList<String> getEdgesLabels();

	void setEdgesLabels(ObjectBigList<String> labels);
	
	IntBigArrayBigList getNodesCosts();
	
	public ObjectBigList<String> getNodesLabels();
	
	void setNodesLabels(ObjectBigList<String> labels);

}