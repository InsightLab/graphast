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

package br.ufc.insightlab.graphast.structure;

import br.ufc.insightlab.graphast.exceptions.DuplicatedEdgeException;
import br.ufc.insightlab.graphast.exceptions.DuplicatedNodeException;
import br.ufc.insightlab.graphast.exceptions.NodeNotFoundException;
import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Node;
import br.ufc.insightlab.graphast.model.components.GraphComponent;
import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.cursors.IntCursor;

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
	
	private int nextNodeId = 0;
	private int nextEdgeId = 0;

	private LongIntMap nodeIdMapping = new LongIntHashMap();
	private LongIntMap edgeIdMapping = new LongIntHashMap();

	private IntArrayList aliveNodes = new IntArrayList();
	private IntArrayList aliveEdges = new IntArrayList();
	
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();

	private int numberOfNodes = 0;
	private int numberOfEdges = 0;

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

	@Override
	public long nodeIndex(long nodeId) {
		return nodeIdMapping.get(nodeId);
	}

	@Override
	public long edgeIndex(long edgeId) {
		return edgeIdMapping.get(edgeId);
	}

	/**
	 * Add a new node into the graph.
	 * @param node the node that will be added.
	 */
	public void addNode(Node node) {

		long nId = node.getId();
		
		if (this.containsNode(nId))
			throw new DuplicatedNodeException(nId);
		int index = nextNodeId++;
		nodeIdMapping.put(nId, index);

		int aliveIndex = index >> 5;
		int aliveSubIndex = index & 31;

		if (aliveIndex >= aliveNodes.size())
			aliveNodes.add(0);

		aliveNodes.set(aliveIndex, aliveNodes.get(aliveIndex) | (byte)(1 << aliveSubIndex));

		adjacency.add(new IntHashSet[] {new IntHashSet(), new IntHashSet()});

		nodes.add(node);
		numberOfNodes++;
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

		int aliveIndex = eIndex >> 5;
		int aliveSubIndex = eIndex & 31;

		if (aliveIndex >= aliveEdges.size())
			aliveEdges.add(0);

		aliveEdges.set(aliveIndex, aliveEdges.get(aliveIndex) | (1 << aliveSubIndex));

		updateAdjacency(e, eIndex);
		
		edges.add(e);
		numberOfEdges++;
	}

	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 */
	@Override
	public boolean containsNode(long id) {
		return nodeIdMapping.containsKey(id) && !isRemoved(nodes.get(nodeIdMapping.get(id)));
	}

	@Override
	public boolean containsEdge(long id) {
		return edgeIdMapping.containsKey(id) && !isRemoved(edges.get(edgeIdMapping.get(id)));
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
		return numberOfNodes;
	}
	
	/**
	 * @return the number of graph's edges.
	 */
	@Override
	public long getNumberOfEdges() {
		return numberOfEdges;
	}
	
	/**
	 * returns the node which has the given id.
	 * @param id the node's id.
	 * @return the node
	 */
	public Node getNode(final long id) {
		return nodes.get(nodeIdMapping.get(id));
	}

	@Override
	public Node removeNode(Node n) {
		int index = nodeIdMapping.get(n.getId());

		int aliveIndex = index >> 5;
		int aliveSubIndex = index & 31;

		aliveNodes.set(aliveIndex, aliveNodes.get(aliveIndex) & ~(1 << aliveSubIndex));

		for (Edge e : getOutEdges(n.getId())) removeEdge(e);
		for (Edge e : getInEdges(n.getId())) removeEdge(e);

		numberOfNodes--;

		return n;
	}

	@Override
	public boolean isRemoved(Node n) {
		int index = nodeIdMapping.get(n.getId());

		int aliveIndex = index >> 5;
		int aliveSubIndex = index & 31;

		return (aliveNodes.get(aliveIndex) & (1 << aliveSubIndex)) == 0;
	}

	public Edge getEdge(final long id) {
		return edges.get(edgeIdMapping.get(id));
	}

	@Override
	public Edge removeEdge(Edge e) {
		int index = edgeIdMapping.get(e.getId());

		int aliveIndex = index >> 5;
		int aliveSubIndex = index & 31;

		aliveEdges.set(aliveIndex, aliveEdges.get(aliveIndex) & ~(1 << aliveSubIndex));

		numberOfEdges--;

		return e;
	}

	@Override
	public boolean isRemoved(Edge e) {
		int index = edgeIdMapping.get(e.getId());

		int aliveIndex = index >> 5;
		int aliveSubIndex = index & 31;

		return (aliveEdges.get(aliveIndex) & (1 << aliveSubIndex)) == 0;
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
