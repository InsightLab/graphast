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

package org.insightlab.graphast.model;

import java.util.Iterator;
import java.util.Set;

import org.insightlab.graphast.model.cards.GraphComponent;
import org.insightlab.graphast.structure.DefaultGraphStructure;
import org.insightlab.graphast.structure.GraphStructure;

/**
 * The Graph class. It represents the model of a graph.
 * This class has a GraphStructure element as an attribute, and 
 * this element possesses functions and attributes to represent 
 * the state of a graph: Nodes, edges and functions do manage the
 * nodes and edges of the graph.
 * 
 */
public class Graph extends GraphObject {
	
	private GraphStructure structure;
	
	/**
	 * Instantiates a new empty graph.
	 */
	public Graph() {
		this(new DefaultGraphStructure());
	}
	
	/**
	 * Instantiates a new graph, given a pre-instantiated structure.
	 *
	 * @param structure the GraphStructure element representing the structure
	 * of this graph.
	 */
	public Graph(GraphStructure structure) {
		this.structure = structure;
	}
	
	/**
	 * Adds a new node to the graph, given a new id. If a node already
	 * exists with the given id, a DuplicatedNodeException is thrown.
	 *
	 * @param id the id of the new node to be added to the graph.
	 */
	public void addNode(long id) {
		this.addNode(new Node(id));
	}
	
	/**
	 * Adds a new node to the graph, given the Node object. If a node already
	 * exists with an id equal to the given node, a DuplicatedNodeException is thrown.
	 *
	 * @param n the new node to be added to the graph.
	 */
	public void addNode(Node n) {
		structure.addNode(n);
	}
	
	/**
	 * Adds a batch of new nodes to the graph. It utilizes the addNode function to
	 * add each new node given in the function.
	 *
	 * @param nodes the Node objects to be added to the graph. 
	 */
	public void addNodes(Node ...nodes) {
		for (Node n : nodes)
			this.addNode(n);
	}
	
	/**
	 * Adds a new edge to this graph, given a new Edge object. It uses the GraphStructure attribute's
	 * addEdge method to add the new edge to this graph. If one of the node ids of the given Edge object
	 * does not exists in this graph, a NodeNotFoundException is thrown. Otherwise, the edge is successfully
	 * added to this graph.
	 *
	 * @param e the new edge to be added to the graph.
	 */
	public void addEdge(Edge e) {
		structure.addEdge(e);
	}
	
	/**
	 * Adds a batch of new edges to this graph. It utilizes the addEdge function to
	 * add each new edge given in the function.
	 *
	 * @param edges the new edges to be added to the graph.
	 */
	public void addEdges(Edge ...edges) {
		
		for (Edge e : edges)
			this.addEdge(e);
	}
	
	/**
	 * Contains node. This function checks if a node with the given id already exists
	 * within the graph structure.
	 *
	 * @param id the id of the node to checked.
	 * @return true, if the graph structure already contains a Node object with the given id.
	 * false, otherwise.
	 */
	public boolean containsNode(long id) {
		return structure.containsNode(id);
	}
	
	/**
	 * It returns an Iterator object of the Node elements contained in this graph.
	 *
	 * @return the iterator of the list of nodes contained in this graph.
	 */
	public Iterator<Node> nodeIterator() {
		return structure.nodeIterator();
	}
	
	/**
	 * It returns an Iterator object of the Edge elements contained in this graph.
	 *
	 * @return the iterator of the list of edges contained in this graph.
	 */
	public Iterator<Edge> edgeIterator() {
		return structure.edgeIterator();
	}
	
	/**
	 * Gets the number of nodes of the graph.
	 *
	 * @return the number of nodes of the graph.
	 */
	public long getNumberOfNodes() {
		return structure.getNumberOfNodes();
	}
	
	/**
	 * Gets the number of edges of the graph.
	 *
	 * @return the number of edges of the graph.s
	 */
	public long getNumberOfEdges() {
		return structure.getNumberOfEdges();
	}
	
	/**
	 * Gets the outgoing edges of a given node in the graph, given a node id.
	 * If a node in the graph with the given id has any outgoing edges, an Iterator
	 * object of a list with this Node's outgoing Edge objects is returned. 
	 *
	 * @param id the id of the node from which the outgoing edges are wish to be returned.
	 * @return an Iterator object of the list of outgoing edges of the node with the given id.
	 */
	public Iterator<Edge> getOutEdges(long id) {
		return structure.getOutEdges(id);
	}
	
	/**
	 * Gets the incoming edges of a given node in the graph, given a node id.
	 * If a node in the graph with the given id has any incoming edges, an Iterator
	 * object of a list with this Node's incoming Edge objects is returned. 
	 *
	 * @param id the id of the node from which the incoming edges are wish to be returned.
	 * @return an Iterator object of the list of incoming edges of the node with the given id.
	 */
	public Iterator<Edge> getInEdges(long id) {
		return structure.getInEdges(id);
	}
	
	
	public GraphComponent getCard(String cardName) {
		return structure.getCard(cardName);
	}
	
	public void setCard(String cardName, GraphComponent card) {
		structure.setCard(cardName, card);
	}
	
	public Set<String> getAllCardNames() {
		return structure.getAllCardNames();
	}
	
	
	/**
	 * Gets the neighborhood from a node represented by a given id. If the graph 
	 * has any Node object with the given id, this function returns an Iterator
	 * object representing all the neighboring Node objects to the given node.
	 * The neighboring nodes are given according to the outgoing edges of the Node
	 * object of the graph that has the given id.
	 *
	 * @param id the id of a Node object in the graph.
	 * @return the neighborhood of the node with the given id, if this node exists in the graph.
	 * This neighborhood is represented by an Iterator object of a list of ids of nodes to 
	 * which the node given in the function points to.
	 */
	public Iterator<Long> getNeighborhood(final long id) {
		
		return new Iterator<Long>() {
			
			Iterator<Edge> iter = structure.getOutEdges(id);

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Long next() {
				Edge e = iter.next();
				return e.getFromNodeId() == id ? e.getToNodeId() : e.getFromNodeId();
			}
			
		};
	}
	
}
