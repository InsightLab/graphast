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

import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntSet;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.LongIntMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import org.insightlab.graphast.exceptions.DuplicatedEdgeException;
import org.insightlab.graphast.exceptions.DuplicatedNodeException;
import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class implements a default graph structure using the interface GraphStructure.
 *
 */
public class DefaultGraphStructure implements GraphStructure {

	private static final int OUT_EDGES = 0;
	private static final int IN_EDGES = 1;
	
	private Map<Class<? extends GraphComponent>, GraphComponent> graphComponents = null;
	
	private Integer nextNodeId = 0;
	private Integer nextEdgeId = 0;

	private LongIntMap nodeIdMapping = new LongIntHashMap();
	private LongIntMap edgeIdMapping = new LongIntHashMap();
	
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();

	private ArrayList<IntSet[]> adjacency = new ArrayList<>();

	@Override
	public void updateAdjacency(Edge e) {
		updateAdjacency(e, edgeIdMapping.get(e.getId()));
	}

	private void updateAdjacency(Edge e, int eIndex) {
		int fromIndex = nodeIdMapping.get(e.getFromNodeId());
		int toIndex = nodeIdMapping.get(e.getToNodeId());
		IntSet[] fromAdj = adjacency.get(fromIndex);
		IntSet[] toAdj = adjacency.get(toIndex);

		fromAdj[0].add(eIndex);
		toAdj[1].add(eIndex);

		if (e.isBidirectional()) {
			fromAdj[1].add(eIndex);
			toAdj[0].add(eIndex);
		} else {
			fromAdj[1].removeAll(eIndex);
			toAdj[0].removeAll(eIndex);
		}
	}

	/**
	 * Add a new node into the graph.
	 * @param node the node that will be added.
	 */
	public void addNode(Node node) {

		long nId = node.getId();
		
		if (this.containsNode(nId))
			throw new DuplicatedNodeException(nId);
		
		nodeIdMapping.put(nId, nextNodeId++);
		nodes.add(node);

		adjacency.add(new IntHashSet[] {new IntHashSet(), new IntHashSet()});
	}
	
	/**
	 * Add a new edge into the graph.
	 * @param e the edge that will be added into the graph.
	 */
	public void addEdge(Edge e) {

		if (!containsNode(e.getFromNodeId())) {
			throw new NodeNotFoundException(e.getFromNodeId());
		}
		
		if (!containsNode(e.getToNodeId())) {
			throw new NodeNotFoundException(e.getToNodeId());
		}

		if (this.containsEdge(e.getId()))
			throw new DuplicatedEdgeException(e.getId());

		int eIndex = nextEdgeId++;

		edgeIdMapping.put(e.getId(), eIndex);

		updateAdjacency(e, eIndex);
		
		edges.add(e);
		
	}

	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 */
	@Override
	public boolean containsNode(long id) {
		return nodeIdMapping.containsKey(id) && !nodes.get(nodeIdMapping.get(id)).isRemoved();
	}

	@Override
	public boolean containsEdge(long id) {
		return edgeIdMapping.containsKey(id) && !edges.get(edgeIdMapping.get(id)).isRemoved();
	}
	
	/**
	 * @return an iterator to graph's nodes.
	 */
	public Iterator<Node> allNodesIterator() {
		return nodes.iterator();
	}
	
	/**
	 * @return an iterator to graph's edges.
	 */
	public Iterator<Edge> allEdgesIterator() {
		return edges.iterator();
	}
	
	/**
	 * @return the number of graph's nodes.
	 */
	@Override
	public long getNumberOfNodes() {
		return nodes.size();
	}
	
	/**
	 * @return the number of graph's edges.
	 */
	@Override
	public long getNumberOfEdges() {
		return edges.size();
	}
	
	/**
	 * returns the node which has the given id.
	 * @param id the node's id.
	 * @return the node
	 */
	public Node getNode(final long id) {
		return nodes.get(nodeIdMapping.get(id));
	}

	public Edge getEdge(final long id) {
		return edges.get(edgeIdMapping.get(id));
	}

	private Iterator<Edge> getAdjacencyIterator(final long id, int outOrIn) {
		return new Iterator<Edge>() {
			private Iterator<IntCursor> iter = adjacency.get(nodeIdMapping.get(id))[outOrIn].iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Edge next() {
				return edges.get(iter.next().value);
			}
		};
	}
	
	/**
	 * @param id the node's id.
	 * @return the out edges of the node which has the given id.
	 */
	public Iterator<Edge> getAllOutEdgesIterator(final long id) {
		return getAdjacencyIterator(id, OUT_EDGES);
	}
	
	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	public Iterator<Edge> getAllInEdgesIterator(final long id) {
		return getAdjacencyIterator(id, IN_EDGES);
	}



	@Override
	public void addComponent(Class<? extends GraphComponent> key, GraphComponent component) {
		if (graphComponents == null)
			graphComponents = new HashMap<>();
		graphComponents.put(key, component);
	}
	
	@Override
	public GraphComponent getComponent(Class<? extends GraphComponent> componentClass) {
		if (graphComponents == null || !graphComponents.containsKey(componentClass))
			return null;
		return graphComponents.get(componentClass);
	}

	@Override
	public Set<Class<? extends GraphComponent>> getAllComponentClasses() {
		if (graphComponents == null)
			return null;
		return graphComponents.keySet();
	}

	@Override
	@Nonnull
	public Iterator<GraphComponent> getAllComponentsIterator() {
		return (graphComponents != null) ? 
				graphComponents.values().iterator() :
				new Iterator<GraphComponent>() {
					@Override
					public boolean hasNext() {
						return false;
					}
					@Override
					public GraphComponent next() {
						return null;
					}
				};
	}

}
