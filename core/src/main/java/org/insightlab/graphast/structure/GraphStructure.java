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

package org.insightlab.graphast.structure;

import com.google.common.collect.Iterators;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

/**
 * The GraphStructure interface. This interface contains declarations of general methods
 * for different GraphStructure's implementations.
 */
public interface GraphStructure {

	long nodeIndex(final long nodeId);

	long edgeIndex(final long edgeId);
	
	/**
	 * Add a new node into the graph.
	 * @param node the node that will be added.
	 */
	void addNode(Node node);
	
	Node getNode(final long id);

	Node removeNode(Node n);

	default Node removeNode(final long id) {
		return removeNode( getNode(id) );
	}

	boolean isRemoved(Node n);
	
	/**
	 * Add a new edge into the graph.
	 * @param edge the edge that will be added into the graph.
	 */
	void addEdge(Edge edge);

	Edge getEdge(final long id);

	default Edge getEdge(long from, long to) {
		if (!(this.containsNode(from) && containsNode(to)))
			return null;
		for (Edge out : getOutEdges(from))
			if (out.getAdjacent(from) == to)
				return out;
		return null;
	}

	Edge removeEdge(Edge e);

	default Edge removeEdge(final long id) {
		return removeEdge( getEdge(id) );
	}

	default Edge removeEdge(final long fromId, final long toId) {
		return removeEdge( getEdge(fromId, toId) );
	}

	boolean isRemoved(Edge e);

	void updateAdjacency(Edge e);
	
	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 * @return true if Node with passed id is in the graph, false otherwise.
	 */
	boolean containsNode(final long id);

	boolean containsEdge(final long id);
	
	Iterator<Node> allNodesIterator();
	
	/**
	 * @return an iterator to graph's nodes.
	 */
	@Nonnull
	default Iterator<Node> existingNodesIterator() {
		return Iterators.filter(allNodesIterator(), n -> !isRemoved(n));
	}

	default Iterable<Node> getNodes() {
		return this::existingNodesIterator;
	}

	Iterator<Edge> allEdgesIterator();

	/**
	 * @return an iterator to graph's edges.
	 */
	@Nonnull
	default Iterator<Edge> existingEdgesIterator() {
		return Iterators.filter(allEdgesIterator(), e -> !isRemoved(e));
	}

	default Iterable<Edge> getEdges() {
		return this::existingEdgesIterator;
	}
	
	/**
	 * @return the number of graph's nodes.
	 */
	long getNumberOfNodes();
	
	/**
	 * @return the number of graph's edges.
	 */
	long getNumberOfEdges();

	/**
	 * @return the out edges of the node which has the given id.
	 * @param id the node's id.
	 */
	Iterator<Edge> getAllOutEdgesIterator(final long id);

	default Iterator<Edge> getExistingOutEdgesIterator(final long id) {
		return Iterators.filter(getAllOutEdgesIterator(id), e -> !isRemoved(e));
	}

	default Iterable<Edge> getOutEdges(final long id) {
		return () -> getExistingOutEdgesIterator(id);
	}

	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	Iterator<Edge> getAllInEdgesIterator(final long id);

	default Iterator<Edge> getExistingInEdgesIterator(final long id) {
		return Iterators.filter(getAllInEdgesIterator(id), e -> !isRemoved(e));
	}

	default Iterable<Edge> getInEdges(final long id) {
		return () -> getExistingInEdgesIterator(id);
	}
	
	
	Set<Class<? extends GraphComponent>> getAllComponentClasses();
	
	
	GraphComponent getComponent(Class<? extends GraphComponent> componentClass);
	
	void addComponent(Class<? extends  GraphComponent> key, GraphComponent component);

	default void addComponent(GraphComponent component) {
		addComponent(component.getClass(), component);
	}

	@Nonnull
	Iterator<GraphComponent> getAllComponentsIterator();

	default Iterable<GraphComponent> getAllComponents() {
		return this::getAllComponentsIterator;
	}
	
}
