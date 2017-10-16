package org.graphast.model;

import java.util.Iterator;

import org.graphast.storage.DefaultGraphStorage;
import org.graphast.storage.GraphStorage;

public class Graph {
	
	private GraphStorage storage;
	
	public Graph() {
		this(new DefaultGraphStorage());
	}
	
	public Graph(GraphStorage storage) {
		this.storage = storage;
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
				return (e.getFromNode().equals(n)) ? e.getToNode() : e.getFromNode();
			}
		};
	}
	
}
