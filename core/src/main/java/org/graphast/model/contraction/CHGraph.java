package org.graphast.model.contraction;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.dijkstrach.DijkstraCH;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;


public interface CHGraph extends Graph {
	
	/**
     * This methods sets the level of the specified node.
     * 
     * @param nodeId the ID of the node
     * @param level level to be setted
     */
    void setLevel( int nodeId, int level );
    
    @Override
    public CHNode getNode(long id);
    
    public boolean prepareNodes();
    public CHEdge getEdge(long id);
    
    public void addEdge(CHEdge e);
    public void addNode(CHNode n);

    /**
     * @param nodeId the ID of the node
     * @return the level of the specified node.
     */
    int getLevel( int nodeId );

    boolean isShortcut( int edgeId );

    public void findShortcut(CHNode n, boolean contract);

    public int calculatePriority(CHNode n, boolean contract);
    
	public void setReverseGraph(CHGraph reverseGraph);
	
	public int getMaximumEdgeCount();

	public void setMaximumEdgeCount(int maximumEdgeCount);

	public CHGraph getReverseGraph();
	
	public void setNodesComplement(IntBigArrayBigList nodesComplement);

	public void setEdgesComplement(IntBigArrayBigList edgesComplement);

	public void setPossibleShortcuts(Map<Long, List<CHEdge>> possibleShortcuts);

	public void setNodePriorityQueue(Queue<CHNodeImpl> nodePriorityQueue);

	public void setShortestPath(DijkstraCH shortestPath);
	
	public CHGraph generateReverseCHGraph(CHGraph originalCHGraph);
	
	public void contractNodes();
	
	public Queue<CHNodeImpl> getNodePriorityQueue();
	
	
	
}