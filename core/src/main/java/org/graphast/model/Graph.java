package org.graphast.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Graph {
	
	private Integer nextId = 0;

	private HashMap<Integer, Integer> idMapping = new HashMap<>();
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	
	public Graph() {
		
	}
	
	private Integer getNextId() {
		nextId++;
		return nextId - 1;
	}
	
	public void addNode(Node n) {
		idMapping.put(n.getId(), getNextId());
		nodes.add(n);
	}
	
	public void addNodes(Node ...nodes) {
		for (Node n : nodes)
			this.addNode(n);
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
		e.getFromNode().addEdge(e);
		e.getToNode().addEdge(e);
	}
	
	public void addEdges(Edge ...edges) {
		for (Edge e : edges)
			this.addEdge(e);
	}
	
	public Node getNode(int id) {
		return nodes.get(idMapping.get(id));
	}
	
	public Iterator<Node> nodeIterator() {
		return nodes.iterator();
	}
	
	public Iterator<Edge> edgeIterator() {
		return edges.iterator();
	}
	
}
