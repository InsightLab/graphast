package org.graphast.query.route.shortestpath2;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Collections;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastNode;

/**
 * This class contains useful methods for the Dijkstra's Shortest Path Algorithm with
 * constant weight.
 * 
 * @author NEX2ME
 *
 */
public class DijkstraShortestPathWithConstantWeight extends DijkstraShortestPath {

	/**
	 * Class constructor
	 * @param source
	 * @param target
	 * @param gr
	 */
	public DijkstraShortestPathWithConstantWeight(Graphast gr, long source, long target) {
		super(gr,source,target);
	}
	
	//TODO Put some reference for the Dijkstra Algorithm here!
	/**
	 * Main execution of the Dijkstra Algorithm with Constant Weight.
	 */
	public void execute() {
		
		LongList resultPath;
		
		//TODO Change this "i+1"
		for (long i = 0; i < nodes.size64(); i = i + GraphastNode.NODE_BLOCKSIZE) {
			
			
			unsettledNodes.add(nodes.get(i+1));
			distance.put(nodes.get(i+1), Integer.MAX_VALUE);
		
		}
		
		distance.put(source.getId().longValue(), 0);
		

		while (unsettledNodes.size() > 0) {
			
			long node = getMinimum(unsettledNodes);
			settledNodes.add(node);
			
			unsettledNodes.remove(node);
			
			findMinimalDistances(node);
		
		}
		
		resultPath = getPath(target.getId());
		
		System.out.println(resultPath.toString());

	}
	
	/**
	 * This method iterates the neighbors nodes, changing the neighbor node distance if the current
	 * distance is greater than the source node plus edge weight.
	 * 
	 * @param	node	node that will be used to find the minimal distance
	 */
	private void findMinimalDistances(long node) {

		LongList adjacentNodes = getNeighbors(node);
		
		for (long target : adjacentNodes) {
			
			if (getShortestDistance(target) > getShortestDistance(node)	+ getDistance(node, target)) {
				
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unsettledNodes.add(target);
			
			}
		
		}

	}
	
	/**
	 * Assemble the edge into a object then returns the distance of it.
	 * 
	 * @param sourceId
	 * @param destinationId
	 * @return edge weight
	 */
	private int getDistance(long sourceId, long destinationId) {
		
		GraphastEdge edge;
		
		for (long id=0;	id<edges.size64()/GraphastEdge.EDGE_BLOCKSIZE; id++) {
			
			edge = gr.getEdge(id);

			if (edge.getFromNode() == sourceId && edge.getToNode() == destinationId) {
				return edge.getDistance();
			}
		
		}
		
		throw new RuntimeException("Should not happen");
	
	}
	
	/**
	 * Runs the vector of edges, mounting them on an object and seeing if the "fromNode" is equal
	 * to parameter node. if so, includes the "toNode" to the neighbor list.
	 * 
	 * @param node
	 * @return neighbors list of a node
	 */
	private LongList getNeighbors(long node) {
		
		LongList neighbors = new LongArrayList();

		GraphastEdge edge;

		for (long i=0; i<edges.size64()/17; i++) {
			
			edge = gr.getEdge(i);

			if ( edge.getFromNode() == node && !isSettled(edge.getToNode())) {
				
				neighbors.add(edge.getToNode());
			
			}
		
		}
		
		return neighbors;

	}

	/**
	 * This method iterates over a set of nodes, changing the value of a variable if the distance
	 * from the next iteration node is less than the distance value of the variable.
	 * @param nodes
	 * @return the node that has the minimum distance of a set
	 */
	private long getMinimum(LongSet nodes) {
	    
		long minimum = Integer.MAX_VALUE;
	    
	    for (long node : nodes) {
	    	
	      if (minimum == Integer.MAX_VALUE) {
	        minimum = node;
	      } else {
	    	  
	        if (getShortestDistance(node) < getShortestDistance(minimum)) {
	          minimum = node;
	        }
	      }
	    }
	    
	    return minimum;
	  
	}

	/**
	 * This method receives a nodeExternalId and checks if this node is on the settledNodes set.
	 * @param node	the nodeExternalId
	 * @return	true or false, if the node is or isn't in the set
	 */
	private boolean isSettled(long node) {
	
		return settledNodes.contains(node);

	}
	/**
	 * Get the current distance of a node during the execution of the algorithm
	 * @param destination
	 * @return the distance of a node 
	 */
	private int getShortestDistance(long destination) {
		int d = distance.get(destination);
		return d;
	}	

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LongList getPath(long target) {
		
		LongList path = new LongArrayList();
		
		long step = target;
		
		// check if a path exists
		if (!predecessors.containsKey(step)) {
			return null;
		}
		
		path.add(step);
		
		while (predecessors.containsKey(step)) {
			step = predecessors.get(step);
			path.add(step);
		}
		
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	
	}

}
