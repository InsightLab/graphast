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

package org.insightlab.graphast.query.shortestpath;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.query.cost_functions.CostFunction;
import org.insightlab.graphast.query.cost_functions.DefaultCostFunction;
import org.insightlab.graphast.query.utils.DistanceElement;
import org.insightlab.graphast.query.utils.DistanceVector;

/**
 * This class is an implementation of the ShortestPathStrategy interface and it implements the Dijkstra strategy.
 * The Dijkstra strategy solves the shortest path problem from a single source calculating a distance value for each
 * node from the source, based on the neighborhood of each node and its calculated proximity. The implementations
 * for each method of the ShortestPathStrategy interface are given based on the Dijkstra method and auxiliary functions
 * are implemented to calculate certain aspects of this specific strategy.
 */
public class DijkstraStrategy implements ShortestPathStrategy {
	
	private Graph g;
	private CostFunction costFunction;
	
	/**
	 * Instantiates a new DijkstraStrategy class, given a Graph object.
	 *
	 * @param g the Graph object, with nodes and edges from which shortest paths will be calculated.
	 */
	public DijkstraStrategy(Graph g) {
		this.g = g;
		costFunction = new DefaultCostFunction();
	}
	
	public void setCostFunction(CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	/**
	 * Run method. This method is an implementation of the ShortestPathSrategy based on the Dijkstra method.
	 * A DistanceVector object containing the shortest path between a source node and all the other nodes in
	 * the graph is returned. The DistanceVector object contains all the visited nodes and their respective
	 * parameters, e.g. distance value. A Priority Queue is used to visit all the nodes until the shortest 
	 * path between the source and every other node is found. Each new node that is not yet visited is added 
	 * to the queue and its neighborhood is explored, i.e. all the nodes that are adjacent to it. In this visit, the
	 * distance value of each node, based on the source node, is calculated (using the cost value of the edges
	 * between them) and, at the end, a DistanceVector object containing a shortest path is returned, if it exists. 
	 *
	 * @param sourceId the id of the source node in the graph.
	 * @return the distance vector containing the shortest path between the given source node and all the other 
	 * nodes in the graph, with a HashMap of nodes (DistanceElement objects) and their respective parameters, 
	 * e.g. the distance value for each node.
	 * @throws NodeNotFoundException exception thrown if a node id given in the function call is not found in the graph.
	 */
	@Override
	public DistanceVector run(long sourceId) throws NodeNotFoundException {
		return this.run(sourceId, -1);
	}

	/**
	 * Run method. This method is an implementation of the ShortestPathSrategy based on the Dijkstra method.
	 * A DistanceVector object containing the shortest path between a source node and a target node is
	 * returned. The DistanceVector object contains all the visited nodes and their respective
	 * parameters, e.g. distance value. A Priority Queue is used to visit all the nodes until the shortest 
	 * path between the source and the target is found. Each new node that is not yet visited is added to the
	 * queue and its neighborhood is explored, i.e. all the nodes that are adjacent to it. In this visit, the
	 * distance value of each node, based on the source node, is calculated (using the cost value of the edges
	 * between them) and, at the end, a DistanceVector object containing a shortest path is returned, if it exists. 
	 *
	 * @param sourceId the id of the source node in the graph.
	 * @param targetId the id of the target node in the graph.
	 * @return the distance vector containing the shortest path between the two given nodes, with a HashMap
	 * of nodes (DistanceElement objects) and their respective parameters, e.g. the distance value for each node.
	 * @throws NodeNotFoundException exception thrown if a node id given in the function call is not found in the graph.
	 */
	@Override
	public DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException {
		
		if (!g.containsNode(sourceId))
			throw new NodeNotFoundException(sourceId);
		
		if (targetId != -1 && !g.containsNode(targetId))
			throw new NodeNotFoundException(targetId);
		
		DistanceVector vector          = new DistanceVector(sourceId);
		Queue<DistanceElement> toVisit = new PriorityQueue<>();
		
		toVisit.add(vector.getElement(sourceId));
		
		while (!toVisit.isEmpty()) {
			
			DistanceElement min = toVisit.poll();
			
			if (targetId != -1 && targetId == min.getNodeId())
				return vector;
			
			if (!min.isVisited()) 
				visitNode(min, vector, toVisit);
		}
		
		return vector;	
	}
	
	/**
	 * Visit node function. This method "visits" the node, by calculating the distance value of its 
	 * entire neighborhood e.g., all the nodes that are adjacent to it. Each outgoing edge of the node is
	 * used to discover the node adjacent to the one that is being visited. If this neighbor is still
	 * not visited, then its distance value is calculated and it is added to the toVisit queue.
	 *
	 * @param node the node element that is being visited.
	 * @param vector the distance vector that will contain the shortest path being calculated.
	 * @param toVisit the queue containing all the nodes in the graph that weren't still visited.
	 */
	private void visitNode(DistanceElement node, DistanceVector vector, Queue<DistanceElement> toVisit) {
		
		node.setVisited(true);
		
		Iterator<Edge> it = g.getOutEdges(node.getNodeId());
		
		while (it.hasNext()) {
			
			Edge e = it.next();
			DistanceElement neighbor = vector.getElement(e.getAdjacent(node.getNodeId()));
			
			if (!neighbor.isVisited())
				relaxPath(e, node, neighbor, toVisit);
		}
	}
	
	/**
	 * Relax path function. This auxiliary method is used if a neighboring node of a node that is currently
	 * being visited hasn't still been visited. A new distance value is calculated between the node and its 
	 * neighbor, based on the edge they share and the distance value of the node being visited. If the new
	 * distance is less than the current distance value of the neighbor and the neighbor hasn't yet been visited,
	 * the value of the distance in the neighbor node is updated and the neighbor node is added to the toVisit queue.
	 *
	 * @param e the edge between the node being visit and its neighbor.
	 * @param node the node being visited.
	 * @param neighbor the neighbor of the node being visited.
	 * @param toVisit the queue containing all the nodes in the graph that weren't still visited.
	 */
	private void relaxPath(Edge e, DistanceElement node, DistanceElement neighbor, Queue<DistanceElement> toVisit) {
		
		double newDistance;
		try {
			newDistance = node.getDistance() + costFunction.getCost(e);
		} catch (Exception e1) {
			System.err.println(e1.getMessage() + ", using edge default cost intead.");
			newDistance = e.getCost();
		}
		
		if (neighbor.getDistance() > newDistance && !neighbor.isVisited()) {
			
			neighbor.changeDistance(newDistance);
			neighbor.changePrevious(node.getNodeId());
			
			toVisit.add(neighbor);
		}
		
	}

}
