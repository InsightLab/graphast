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

import org.insightlab.graphast.exceptions.DuplicatedNodeException;
import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;

import java.util.*;

/**
 * This class implements a default graph structure using the interface GraphStructure.
 *
 */
public class DefaultGraphStructure implements GraphStructure {
	
	private Map<Class<? extends GraphComponent>, GraphComponent> graphComponents = null;
	
	private Integer nextId = 0;
	
	private HashMap<Long, Integer> idMapping = new HashMap<>();
	
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	
	private ArrayList<ArrayList<Edge>> outEdges = new ArrayList<>();
	private ArrayList<ArrayList<Edge>> inEdges  = new ArrayList<>();
	
	/**
	 * Add a new adjacency between two vertices.
	 * @param id the first vertex of the adjacency.
	 * @param e edge that represents the adjacency between the vertices.
	 */
	private void addAdjacency(long id, Edge e) {
		
		int nodeId = idMapping.get(id);
		
		if (e.isBidirectional()) {
			inEdges.get(nodeId).add(e);
			outEdges.get(nodeId).add(e);
		}
		else if (e.getFromNodeId() == id) {
			outEdges.get(nodeId).add(e);
		}
		else if (e.getToNodeId() == id) {
			inEdges.get(nodeId).add(e);
		}
		
	}

	/**
	 * Add a new node into the graph.
	 * @param node the node that will be added.
	 */
	public void addNode(Node node) {
		
		if (idMapping.get(node.getId()) != null)
			throw new DuplicatedNodeException(node.getId());
		
		idMapping.put(node.getId(), nextId++);
		nodes.add(node);
		
		outEdges.add(new ArrayList<Edge>());
		inEdges.add(new ArrayList<Edge>());
	
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
		
		addAdjacency(e.getFromNodeId(), e);
		addAdjacency(e.getToNodeId(),   e);
		
		edges.add(e);
		
	}
	
	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 */
	@Override
	public boolean containsNode(long id) {
		return idMapping.containsKey(id);
	}
	
	/**
	 * @return an iterator to graph's nodes.
	 */
	public Iterator<Node> nodeIterator() {
		return nodes.iterator();
	}
	
	/**
	 * @return an iterator to graph's edges.
	 */
	public Iterator<Edge> edgeIterator() {
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
		return nodes.get(idMapping.get(id));
	}
	
	/**
	 * @param id the node's id.
	 * @return the out edges of the node which has the given id.
	 */
	public Iterator<Edge> getOutEdges(final long id) {
		return outEdges.get(idMapping.get(id)).iterator();
	}
	
	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	public Iterator<Edge> getInEdges(final long id) {
		return inEdges.get(idMapping.get(id)).iterator();
	}

	@Override
	public void addComponent(GraphComponent component) {
		if (graphComponents == null)
			graphComponents = new HashMap<>();
		graphComponents.put(component.getClass(), component);
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
	public Iterator<GraphComponent> getAllComponents() {
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
