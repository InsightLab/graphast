package org.insightlab.graphast.query.shortestpath;

import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.query.utils.DistanceVector;

/**
 * The Shortest Path Strategy interface. This interface contains declarations of general methods
 * for different shortest path strategies' implementations.
 */
public interface ShortestPathStrategy {
	
	/**
	 * Run method. This method serves as a declaration to a general shortest path strategy calculation.
	 * A DistanceVector object containing the shortest path between a source node and all the other nodes 
	 * in the graph is returned. The DistanceVector object contains all the visited nodes and their respective
	 * parameters, e.g. distance value. An implementation of this method must be given in a class that implements 
	 * this interface.
	 *
	 * @param sourceId the id of the source node in the graph.
	 * @return the distance vector containing the shortest path between the given source node and all the other 
	 * nodes in the graph, with a HashMap of nodes (DistanceElement objects) and their respective parameters, 
	 * e.g. the distance value for each node.
	 * @throws NodeNotFoundException exception thrown if a node id given in the function call is not found in the graph.
	 */
	DistanceVector run(long sourceId) throws NodeNotFoundException;
	
	/**
	 * Run method. This method serves as a declaration to a general shortest path strategy calculation.
	 * A DistanceVector object containing the shortest path between a source node and a target node is
	 * returned. The DistanceVector object contains all the visited nodes and their respective
	 * parameters, e.g. distance value. An implementation of this methos must be given in a class that implements this interface.
	 *
	 * @param sourceId the id of the source node in the graph.
	 * @param targetId the id of the target node in the graph.
	 * @return the distance vector containing the shortest path between the two given nodes, with a HashMap
	 * of nodes (DistanceElement objects) and their respective parameters, e.g. the distance value for each node.
	 * @throws NodeNotFoundException exception thrown if a node id given in the function call is not found in the graph.
	 */
	DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException;;

}
