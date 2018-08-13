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

import org.insightlab.graphast.exceptions.DuplicatedNodeException;
import org.insightlab.graphast.model.components.EdgeComponent;
import org.insightlab.graphast.model.components.GraphComponent;
import org.insightlab.graphast.model.listeners.EdgeListener;
import org.insightlab.graphast.model.listeners.NodeListener;
import org.insightlab.graphast.structure.DefaultGraphStructure;
import org.insightlab.graphast.structure.GraphStructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The Graph class. It represents the model of a graph.
 * This class has a GraphStructure element as an attribute, and 
 * this element possesses functions and attributes to represent 
 * the state of a graph: Nodes, edges and functions do manage the
 * nodes and edges of the graph.
 * 
 */
public class Graph extends GraphObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3661942047360629183L;
	
	private long duplicatedNodesCounter = 0;
	
	private GraphStructure structure;

	private List<NodeListener> nodeListeners = new ArrayList<>();
	private List<EdgeListener> edgeListeners = new ArrayList<>();

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

	public void addNodeListener(NodeListener listener) {
		listener.setGraph(this);
		nodeListeners.add(listener);
	}

	public void addEdgeListener(EdgeListener listener) {
		listener.setGraph(this);
		edgeListeners.add(listener);
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
	
	public void addNodes(long ...ids) {
		for (long id : ids)
			this.addNode(id);
	}
	
	/**
	 * Adds a new node to the graph, given the Node object. If a node already
	 * exists with an id equal to the given node, a DuplicatedNodeException is thrown.
	 *
	 * @param n the new node to be added to the graph.
	 */
	public void addNode(Node n) {
		try {
			structure.addNode(n);
			for (NodeListener listener : nodeListeners)
				listener.onInsert(n);
		} catch (DuplicatedNodeException e) {
			duplicatedNodesCounter++;
		}
	}

	public Node removeNode(final long id) {
		Node n = structure.removeNode(id);
		for (NodeListener listener : nodeListeners)
			listener.onRemove(n);
		return n;
	}
	
	public long getDuplicatedNodesCounter() {
		return duplicatedNodesCounter;
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
		for (EdgeListener listener : edgeListeners)
			listener.onInsert(e);
	}

	public Edge removeEdge(final long id) {
		Edge e = structure.removeEdge(id);
		for (EdgeListener listener : edgeListeners)
			listener.onRemove(e);
		return e;
	}

	public Edge removeEdge(final long fromId, final long toId) {
		Edge e = structure.removeEdge(fromId, toId);
		for (EdgeListener listener : edgeListeners)
			listener.onRemove(e);
		return e;
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
	
	public Node getNode(final long id) {
		return structure.getNode(id);
	}
	
	/**
	 * It returns an Iterator object of the Node elements contained in this graph.
	 *
	 * @return the iterator of the list of nodes contained in this graph.
	 */
	public Iterator<Node> getNodeIterator() {
		return structure.existingNodesIterator();
	}
	
	public Iterable<Node> getNodes() {
		return structure.getNodes();
	}

	public Edge getEdge(final long id) {
		return structure.getEdge(id);
	}
	
	public Edge getEdge(long from, long to) {
		return structure.getEdge(from, to);
	}
	
	/**
	 * It returns an Iterator object of the Edge elements contained in this graph.
	 *
	 * @return the iterator of the list of edges contained in this graph.
	 */
	public Iterator<Edge> getEdgeIterator() {
		return structure.existingEdgesIterator();
	}
	
	public Iterable<Edge> getEdges() {
		return structure.getEdges();
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
	public Iterator<Edge> getOutEdgesIterator(long id) {
		return structure.getAllOutEdgesIterator(id);
	}
	
	public Iterable<Edge> getOutEdges(long id) {
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
	public Iterator<Edge> getInEdgesIterator(long id) {
		return structure.getAllInEdgesIterator(id);
	}
	
	public Iterable<Edge> getInEdges(long id) {
		return structure.getInEdges(id);
	}

	public <C extends GraphComponent> C getComponent(Class<C> componentClass) {
		return componentClass.cast(structure.getComponent(componentClass));
	}

	public void addComponent(GraphComponent component) {
		addComponent(component.getClass(), component);
	}

	public void addComponent(Class<? extends GraphComponent> key, GraphComponent component) {
		structure.addComponent(key, component);
		component.setGraph(this);
	}
	
	public Iterable<GraphComponent> getAllComponents() {
		return structure.getAllComponents();
	}
	
	public Set<Class<? extends GraphComponent>> getAllComponentNames() {
		return structure.getAllComponentClasses();
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
	public Iterator<Long> getNeighborhoodIterator(final long id) {
		
		return new Iterator<Long>() {
			
			Iterator<Edge> iter = structure.getAllOutEdgesIterator(id);

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

	public Iterable<Long> getNeighborhood(final long id) {
		return () -> getNeighborhoodIterator(id);
	}
}
