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

import org.insightlab.graphast.exceptions.DuplicatedEdgeException;
import org.insightlab.graphast.exceptions.DuplicatedNodeException;
import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;

import org.apache.mahout.math.map.AbstractLongIntMap;
import org.apache.mahout.math.map.OpenLongIntHashMap;
import org.apache.mahout.math.list.IntArrayList;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class implements a default graph structure using the interface GraphStructure.
 *
 */
public class DefaultGraphStructure implements GraphStructure {
	
	private Map<Class<? extends GraphComponent>, GraphComponent> graphComponents = null;
	
	private Integer nextNodeId = 0;
	private Integer nextEdgeId = 0;

	private AbstractLongIntMap nodeIdMapping = new OpenLongIntHashMap();
	private AbstractLongIntMap edgeIdMapping = new OpenLongIntHashMap();
	
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();

	private ArrayList<IntArrayList> outEdges = new ArrayList<>();
	private ArrayList<IntArrayList> inEdges  = new ArrayList<>();
	
	/**
	 * Add a new adjacency between two vertices.
	 * @param nId the first vertex of the adjacency.
	 * @param e edge that represents the adjacency between the vertices.
	 */
	private void addAdjacency(long nId, int eIndex, Edge e) {
		
		int nIndex = nodeIdMapping.get(nId);
		
		if (e.isBidirectional()) {
			inEdges.get(nIndex).add(eIndex);
			outEdges.get(nIndex).add(eIndex);
		}
		else if (e.getFromNodeId() == nId) {
			outEdges.get(nIndex).add(eIndex);
		}
		else if (e.getToNodeId() == nId) {
			inEdges.get(nIndex).add(eIndex);
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
		
		outEdges.add(new IntArrayList());
		inEdges.add(new IntArrayList());
	
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
		
		addAdjacency(e.getFromNodeId(), eIndex, e);
		addAdjacency(e.getToNodeId(),   eIndex, e);
		
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
	
	/**
	 * @param id the node's id.
	 * @return the out edges of the node which has the given id.
	 */
	public Iterator<Edge> getAllOutEdgesIterator(final long id) {
		return new Iterator<Edge>() {
			private int index = 0;
			private IntArrayList list = outEdges.get(nodeIdMapping.get(id));

			@Override
			public boolean hasNext() {
				return index < list.size();
			}

			@Override
			public Edge next() {
				return edges.get(list.get(index++));
			}
		};
	}
	
	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	public Iterator<Edge> getAllInEdgesIterator(final long id) {
		return new Iterator<Edge>() {
			private int index = 0;
			private IntArrayList list = inEdges.get(nodeIdMapping.get(id));

			@Override
			public boolean hasNext() {
				return index < list.size();
			}

			@Override
			public Edge next() {
				return edges.get(list.get(index++));
			}
		};
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
