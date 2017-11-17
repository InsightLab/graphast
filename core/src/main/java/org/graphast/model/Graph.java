package org.graphast.model;

import java.util.Iterator;

import org.graphast.structure.DefaultGraphStructure;
import org.graphast.structure.GraphStructure;

public class Graph extends GraphObject {
	
	private GraphStructure structure;
	
	public Graph() {
		this(new DefaultGraphStructure());
	}
	
	public Graph(GraphStructure structure) {
		this.structure = structure;
	}
	
	public long getNumberOfNodes() {
		return structure.getNumberOfNodes();
	}
	
	public long getNumberOfEdges() {
		return structure.getNumberOfEdges();
	}
	
	public void addNode(long id) {
		this.addNode(new Node(id));
	}
	
	public void addNode(Node n) {
		if (!structure.containsNode(n.getId()))
			structure.addNode(n);
	}
	
	public void addNodes(Node ...nodes) {
		for (Node n : nodes)
			this.addNode(n);
	}
	
	public boolean containsNode(long id) {
		return structure.containsNode(id);
	}
	
	public void addEdge(Edge e) {
		structure.addEdge(e);
	}
	
	public void addEdges(Edge ...edges) {
		for (Edge e : edges)
			this.addEdge(e);
	}
	
	public Iterator<Node> nodeIterator() {
		return structure.nodeIterator();
	}
	
	public Iterator<Edge> edgeIterator() {
		return structure.edgeIterator();
	}
	
	public Iterator<Edge> getOutEdges(long id) {
		return structure.getOutEdges(id);
	}
	
	public Iterator<Edge> getInEdges(long id) {
		return structure.getInEdges(id);
	}
	
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
