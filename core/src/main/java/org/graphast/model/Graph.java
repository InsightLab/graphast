package org.graphast.model;

import java.util.Iterator;

import org.graphast.structure.DefaultGraphStructure;
import org.graphast.structure.GraphStructure;

public class Graph {
	
	private GraphStructure storage;
	
	public Graph() {
		this(new DefaultGraphStructure());
	}
	
	public Graph(GraphStructure storage) {
		this.storage = storage;
	}
	
	public int getNumberOfNodes() {
		return storage.getNumberOfNodes();
	}
	
	public int getNumberOfEdges() {
		return storage.getNumberOfEdges();
	}
	
	public void addNode(Node n) {
		storage.addNode(n);
	}
	
	public void addNodes(Node ...nodes) {
		for (Node n : nodes)
			this.addNode(n);
	}
	
	public void addEdge(Edge e) {
		storage.addEdge(e);
	}
	
	public void addEdges(Edge ...edges) {
		for (Edge e : edges)
			this.addEdge(e);
	}
	
	public Node getNode(int id) {
		return storage.getNode(id);
	}
	
	public Iterator<Node> nodeIterator() {
		return storage.nodeIterator();
	}
	
	public Iterator<Edge> edgeIterator() {
		return storage.edgeIterator();
	}
	
	public Iterator<Edge> getOutEdges(Node n) {
		return storage.getOutEdges(n);
	}
	
	public Iterator<Edge> getInEdges(Node n) {
		return storage.getInEdges(n);
	}
	
	public Iterator<Node> getNeighborhood(final Node n) {
		return new Iterator<Node>() {
			Iterator<Edge> iter = storage.getOutEdges(n);

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Node next() {
				Edge e = iter.next();
				return e.getFromNode().equals(n) ? e.getToNode() : e.getFromNode();
			}
		};
	}
	
}
