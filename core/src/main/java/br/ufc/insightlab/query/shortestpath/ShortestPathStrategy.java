/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package br.ufc.insightlab.query.shortestpath;

import br.ufc.insightlab.exceptions.NodeNotFoundException;
import br.ufc.insightlab.query.cost_functions.CostFunction;
import br.ufc.insightlab.query.utils.DistanceVector;

/**
 * The Shortest Path Strategy interface. This interface contains declarations of general methods
 * for different shortest path strategies' implementations.
 */
public interface ShortestPathStrategy {
	
	void setCostFunction(CostFunction costFunction);
	
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
	DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException;

}
