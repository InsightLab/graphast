package org.graphast.model.contraction;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.graphast.model.Graph;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.ShortestPathService;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;


public interface CHGraph extends Graph {
	
	/**
     * This methods sets the level of the specified node.
     */
    void setLevel( int nodeId, int level );
    
    @Override
    public CHNode getNode(long id);
    
    public boolean prepareNodes();
    public CHEdge getEdge(long id);
    
    public void addEdge(CHEdge e);
    public void addNode(CHNode n);

    /**
     * @return the level of the specified node.
     */
    int getLevel( int nodeId );

    boolean isShortcut( int edgeId );

    /**
     * This method creates a shortcut between a to b which is nearly identical to creating an edge
     * except that it can be excluded or included for certain traversals or algorithms.
     */
    boolean shortcut( int a, int b );
    
    public void findShortcut(Node n);

    public int calculatePriority(Node n);
    
	public void setReverseGraph(CHGraph reverseGraph);
	
	public int getMaximumEdgeCount();

	public void setMaximumEdgeCount(int maximumEdgeCount);

	public int getMaxLevel();

	public void setMaxLevel(int maxLevel);
	
	public CHGraph getReverseGraph();
	
	public void setNodesComplement(IntBigArrayBigList nodesComplement);

	public void setEdgesComplement(IntBigArrayBigList edgesComplement);

	public void setPossibleShortcuts(Map<Long, List<CHEdge>> possibleShortcuts);

	public void setNodePriorityQueue(Queue<CHNodeImpl> nodePriorityQueue);

	public void setShortestPath(ShortestPathService shortestPath);
	
//	public void setReverseGraph(Graph reverseGraph);
	
	public CHGraph generateReverseCHGraph(CHGraph originalCHGraph);
	
	public void contractNodes();
	public void createHyperPOIS();
//	public CHGraphImpl clone();
	
	public Queue<CHNodeImpl> getNodePriorityQueue();
    
}
