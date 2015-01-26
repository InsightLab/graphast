package org.graphast.model;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.shorts.ShortBigArrayBigList;

import java.io.IOException;
import java.util.List;

import org.graphast.geometry.Point;
import org.graphast.util.FileUtils;

public interface Graph {

	/**
	 * Saves all the information in the graph.
	 * 
	 * @see FileUtils for more information about how each list is saved.
	 * @throws IOException 
	 *         If the graph can not be saved for some reason related to IO.
	 */
	public void save() throws IOException;

	/**
	 * Loads all the information in the graph.
	 * 
	 * @see FileUtils for more information about how each list is saved.
	 * @throws IOException
	 *         If the graph can not be loaded for some reason related to IO.
	 */
	public void load() throws IOException;

	/**
	 * For a given GraphastNode, this method will 
	 * add the information of this node to the list
	 * of all nodes.
	 * 
	 * Remember that a GraphastNode has its common 
	 * attributes, but when added to the list of
	 * nodes, theses attributes are broken, when 
	 * necessary, into smaller integer peaces so they
	 * can be added to the IntBigArrayBigList of nodes.
	 * 
	 * @param	node	GraphastNode that will be added
	 * 					to the IntBigArrayBigList of nodes.
	 */
	public void addNode(Node node);

	//TODO Why we only update the latitude, longitude and FirstEdge? 
	//Wouldn't be better if we had a method that updates everything?
	/**
	 * This method will update the IntBigArrayBigList of nodes
	 * with need information of a passed GraphastNode.
	 * 
	 * @param node GraphastNode with the informations that must be updated.
	 */
	public void updateNodeInfo(Node node);

	/**
	 * With a passed id, this method retrieves a GraphastNode
	 * by instantiating a new one using their attribute positions
	 * in the IntBigArrayBigList of nodes.
	 * 
	 * @param	id	identifier that will be used to calculate the
	 * 				start position of the node needed.
	 * @return	a GraphastNode.
	 */
	public Node getNode(long id);

	//TODO Suggestion: delete this method and keep all these operations in  updateEdgeInfo
	public void setEdge(Edge edge, long pos);

	/**
	 * For a given GraphastEdge, this method will 
	 * add the information of this edge to the list
	 * of all edges.
	 * 
	 * Remember that a GraphastEdge has its common 
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
	 * @param	edge	GraphastEdge that will be added
	 * 					to the IntBigArrayBigList of edges.
	 */
	public void addEdge(Edge edge);

	/**
	 * This method will store the passed list of costs in a
	 * ShortBigArrayBigList and return the position
	 * of this insertion.
	 * 
	 * @param	c	list of costs that will be stored
	 * @return	the costId (position where the cost was inserted).
	 */
	public long storeCosts(short[] c);

	/**
	 * This method will store the passed list of points
	 * in a IntBigArrayBigList and return the position
	 * of this insertion.
	 * 
	 * @param	listPoints	list of points that will be stored
	 * @return	the listId (position where the list was inserted).
	 */
	public long storePoints(List<Point> listPoints);

	/**
	 * This method will update the IntBigArrayBigList of edges
	 * with need information of a passed GraphastEdge.
	 * 
	 * @param edge GraphastEdge with the informations that must be updated.
	 */
	public void updateEdgeInfo(Edge edge);

	/**
	 * After add an edge, we must update the neighbor of both
	 * nodes of the edge. This method will do this operation
	 * for a given GraphastEdge.
	 * 
	 * @param	edge	GraphastEdge that will be used to update
	 * 					the neighborhood
	 */
	public void updateNeighborhood(Edge edge);

	/**
	 * This method will update the structure of neighbors used
	 * in Graphast. This structure was based in GraphHopper.
	 * 
	 * @param node Base node used to update its neighbors.
	 * @param eid Edge id where the base node is placed.
	 */
	public void updateNodeNeighborhood(Node node, long eid);

	/**
	 * This method will return all outgoing edges for a given node
	 * 
	 * @param nodeId Given node id.
	 * @return Outgoing edges for a given node.
	 */
	public LongList getOutEdges(long nodeId);

	/**
	 * For a given edge and time, this method will return 
	 * the costs stored in the ShortBigArrayBigList.
	 * 
	 * @param edges List containing the ids of edges.
	 * @param time Time used to get edge costs.
	 * @return Edge costs.
	 */
	public short[] getCosts(LongList edges, int time);

	public ShortBigArrayBigList getCosts();

	public LongList getOutNeighbors(long vid);

	public LongList getOutNeighborsAndCosts(long vid, int time);

	public Edge getEdge(long id);

	public short[] getEdgeCosts(long id);

	public short getEdgeCost(Edge edge, int time);

	public List<Point> getEdgePoints(long id);

	public Long getNodeId(double latitude, double longitude);

	public String getNodeLabel(long id);

	public String getEdgeLabel(long id);

	public IntBigArrayBigList getNodes();

	public IntBigArrayBigList getEdges();

	public void logNodes();

	public void logEdges();

	/**
	 * This method will return the total number of nodes
	 * for a given Graphast.
	 * 
	 * @return	total number of nodes
	 */
	public int getNumberOfNodes();

	/**
	 * This method will return the total number of edges
	 * for a given Graphast.
	 * 
	 * @return	total number of edges
	 */
	public int getNumberOfEdges();
	
	public Long2IntMap accessNeighborhood(Node v);
	
	public boolean hasNode(long id);
	
	public boolean hasNode(int lat, int lon);

}