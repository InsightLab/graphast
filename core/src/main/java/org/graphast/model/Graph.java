package org.graphast.model;

import java.util.HashMap;
import java.util.List;

import org.graphast.enums.CompressionType;
import org.graphast.enums.TimeType;
import org.graphast.geometry.BBox;
import org.graphast.geometry.PoI;
import org.graphast.geometry.Point;
import org.graphast.util.FileUtils;

import com.github.davidmoten.rtree.RTree;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.LongList;


public interface Graph {

	/**
	 * Saves all the information in the graph.
	 * 
	 * @see FileUtils for more information about how each list is saved.
	 */
	public void save();

	/**
	 * Loads all the information in the graph.
	 * 
	 * @see FileUtils for more information about how each list is saved.
	 */
	public void load();

	/**
	 * For a given Node, this method will 
	 * add the information of this node to the list
	 * of all nodes.
	 * 
	 * Remember that a Node has its common 
	 * attributes, but when added to the list of
	 * nodes, theses attributes are broken, when 
	 * necessary, into smaller integer peaces so they
	 * can be added to the IntBigArrayBigList of nodes.
	 * 
	 * @param	node	Node that will be added
	 * 					to the IntBigArrayBigList of nodes.
	 */
	public void addNode(Node node);

	//TODO Why we only update the latitude, longitude and FirstEdge? 
	//Wouldn't be better if we had a method that updates everything?
	/**
	 * This method will update the IntBigArrayBigList of nodes
	 * with need information of a passed Node.
	 * 
	 * @param node Node with the informations that must be updated.
	 */
	public void updateNodeInfo(Node node);

	/**
	 * With a passed id, this method retrieves a Node
	 * by instantiating a new one using their attribute positions
	 * in the IntBigArrayBigList of nodes.
	 * 
	 * @param	id	identifier that will be used to calculate the
	 * 				start position of the node needed.
	 * @return	a Node.
	 */
	public Node getNode(long id);

	//TODO Suggestion: delete this method and keep all these operations in  updateEdgeInfo
	public void setEdge(Edge edge, long pos);

	/**
	 * For a given Edge, this method will 
	 * add the information of this edge to the list
	 * of all edges.
	 * 
	 * Remember that a Edge has its common 
	 * attributes, but when added to the list of
	 * edges, theses attributes are broken, when 
	 * necessary, into smaller integer peaces so they
	 * can be added to the IntBigArrayBigList of nodes.
	 * 
	 * Attributes like label, costs and geometry will be
	 * stored in their respective lists and what will be
	 * actually stored in the IntBigArrayBigList of edges
	 * are their index in the respective lists.
	 * 
	 * @param	edge	Edge that will be added
	 * 					to the IntBigArrayBigList of edges.
	 */
	public void addEdge(Edge edge);

	/**
	 * After add an edge, we must update the neighbor of both
	 * nodes of the edge. This method will do this operation
	 * for a given Edge.
	 * 
	 * @param	edge	Edge that will be used to update
	 * 					the neighborhood
	 */
	public void updateNeighborhood(Edge edge);

	/**
	 * This method will update the structure of neighbors used
	 * in Graph. This structure was based in GraphHopper.
	 * 
	 * @param node Base node used to update its neighbors.
	 * @param eid Edge id where the base node is placed.
	 */
	public void updateNodeNeighborhood(Node node, long eid);

	/**
	 * This method will return all outgoing edges for a given node.
	 * 
	 * @param nodeId Given node id.
	 * @return Outgoing edges for a given node.
	 */
	public LongList getOutEdges(long nodeId);
	
	/**
	 * This method will return all ingoing edges for a given node.
	 * 
	 * @param nodeId Given node id.
	 * @return Ingoing edges for a given node.
	 */
	public LongList getInEdges(long nodeId);

	/**
	 * For a given edge and time, this method will return 
	 * the costs stored in the ShortBigArrayBigList.
	 * 
	 * @param edges List containing the ids of edges.
	 * @param time Time used to get edge costs.
	 * @return Edge costs.
	 */
	public int[] getEdgesCosts(LongList edges, int time);
	
	//TODO documentation
	public int[] getNodeCosts(long id);
	
	/**
	 * This method returns all out neighbors of a given node. 
	 * @param vid of a node
	 * @return a list of all out neighboring nodes
	 */
	public LongList getOutNeighbors(long vid);

	/**
	 * This method returns all out neighbors of a given node with the costs based on a given time.
	 * @param vid Id of a node
	 * @param time Time used to get edge costs.
	 * @return a list of all out neighboring nodes with costs
	 */
	public LongList getOutNeighborsAndCosts(long vid, int time);

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
	public Edge getEdge(long id);

	/**
	 * This method returns a array containing all time costs for a given edge.
	 * @param id Id of a edge
	 * @return a array containing all time costs
	 */
	public int[] getEdgeCosts(long id);
	
	/**
	 * This method returns a cost value of a given edge based on a given time
	 * @param edge edge at which the method is applied
	 * @param time Time used to get edge costs.
	 * @return return the cost of an edge in a give time
	 */
	public Integer getEdgeCost(Edge edge, int time);
	
	/**
	 * This method returns a list of points that are part of a given edge in the map.
	 * @param id Id of a edge
	 * @return a list of points that are part of the edge in the map
	 */
	public List<Point> getGeometry(long id);
	
	/**
	 * This method return a nodeId based on a given latitude and longitude.
	 * @param latitude latitude that is given
	 * @param longitude longitude that is given
	 * @return Id of a node
	 */
	public Long getNodeId(double latitude, double longitude);

	/**
	 * This method returns a label of a given node. 
	 * @param id Id of a node
	 * @return Label of a node
	 */
	public String getNodeLabel(long id);
	
	/**
	 * This method returns a label of a given edge.
	 * @param id If of a edge.
	 * @return Label of a edge
	 */
	public String getEdgeLabel(long id);

	/**
	 * This method returns all nodes as integer array. 
	 * @return All nodes
	 */
	public IntBigArrayBigList getNodes();

	/**
	 * This method returns all edges as integer array. 
	 * @return All edges
	 */
	public IntBigArrayBigList getEdges();
	
	/**
	 * this method shows Nodes log.
	 */
	public void logNodes();

	/**
	 * this method shows Edges log.
	 */
	public void logEdges();

	/**
	 * This method will return the total number of nodes
	 * @return	total number of nodes
	 */
	public long getNumberOfNodes();

	/**
	 * This method will return the total number of edges
	 * @return	total number of edges
	 */
	public long getNumberOfEdges();

	/**
	 * This method returns a map containing the distances of neighbors, of a given node, being the key their id's
	 * @param v A node
	 * @return a map containing the distances of neighbors being the key their id's
	 */
	public Long2IntMap accessNeighborhood(Node v);
	
	
	public HashMap<Node, Integer> accessNeighborhood(Node v, int time);
	
	/**
	 * This method verify if a given nodeId exists on graph.
	 * @param id Id of a node
	 * @return true if a node with this id exists
	 */
	public boolean hasNode(long id);
	
	/**
	 * This method verify if a node with this latitude and longitude exists on graph.
	 * @param lat Latitude
	 * @param lon Longitude
	 * @return true if a node with this latitude and longitude exists
	 */
	public boolean hasNode(double lat, double lon);
	
	public abstract Node addPoi(long id, double lat, double lon, int category, LinearFunction[] costs);
	
	public Node addPoi(long id, double lat, double lon, int category);
	
	public abstract boolean isPoi(long vid);

	public abstract Node getPoi(long vid);
	
	public abstract int poiGetCost(long vid, int time);
	
	public abstract int poiGetCost(long vid);
	
	public abstract int[] getPoiCost(long vid);
	
	public LinearFunction[] convertToLinearFunction(int[] costs);
	
	public int getMaxTime();

	public void setMaxTime(int maxTime);
	
	public int getArrival(int dt, int tt);

	public CompressionType getCompressionType();

	public void setCompressionType(CompressionType compressionType);
	
	public TimeType getTimeType();

	public void setTimeType(TimeType timeType);
	
	public IntSet getCategories();

	public void reverseGraph();
	
	public void setEdgeCosts(long edgeId, int[] costs);
	
	public <T> Node getNearestNode (double latitude, double longitude);
	
	public boolean equals(Graph graph);

	public void setNodeCategory(long nodeId, int category);
	
	public void setEdgeGeometry(long edgeId, List<Point> geometry);
	
	public BBox getBBox();
	
	public void setBBox(BBox bBox);
	
	public Edge getEdge(long originNodeId, long destinationNodeId);

	public List<PoI> getPOIs();
	
	public List<PoI> getPOIs(Integer categoryId);

	public List<Integer> getPOICategories();
	
	public String getDirectory();
	
	public String getAbsoluteDirectory();

	public void setDirectory(String directory);
	
	public RTree<Object, com.github.davidmoten.rtree.geometry.Point> getRTree();
	
	public abstract void setRTree(RTree<Object, com.github.davidmoten.rtree.geometry.Point> add);
	
}