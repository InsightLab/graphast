package org.graphast.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

public class DefaultGraphStorage implements GraphStorage {
	
	private Integer nextId = 0;
	
	private HashMap<Integer, Integer> idMapping = new HashMap<>();
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	private ArrayList<ArrayList<Edge>> outEdges = new ArrayList<>();
	private ArrayList<ArrayList<Edge>> inEdges = new ArrayList<>();
	
	private Integer getNextId() {
		return nextId++;
	}
	
	@Override
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	@Override
	public int getNumberOfEdges() {
		return edges.size();
	}
	
	public void addNode(Node n) {
		idMapping.put(n.getId(), getNextId());
		nodes.add(n);
		outEdges.add(new ArrayList<Edge>());
		inEdges.add(new ArrayList<Edge>());
	}
	
	private void addEdgeInNode(Edge e, Node n) {
		int nodeId = idMapping.get(n.getId());
		if (e.isBidirectional()) {
			inEdges.get(nodeId).add(e);
			outEdges.get(nodeId).add(e);
		}
		else if (e.getFromNode().getId() == n.getId()) {
			outEdges.get(nodeId).add(e);
		}
		else if (e.getToNode().getId() == n.getId()) {
			inEdges.get(nodeId).add(e);
		}
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
		addEdgeInNode(e, e.getFromNode());
		addEdgeInNode(e, e.getToNode());
	}
	
	public Node getNode(final int id) {
		return nodes.get(idMapping.get(id));
	}
	
	public Iterator<Node> nodeIterator() {
		return nodes.iterator();
	}
	
	public Iterator<Edge> edgeIterator() {
		return edges.iterator();
	}
	
	public Iterator<Edge> getOutEdges(final Node n) {
		return outEdges.get(idMapping.get(n.getId())).iterator();
	}
	
	public Iterator<Edge> getInEdges(final Node n) {
		return inEdges.get(idMapping.get(n.getId())).iterator();
	}

}
