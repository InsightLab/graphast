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
	
	public void addNode(Node n) {
		structure.addNode(n);
	}
	
	public void addNodes(Node ...nodes) {
		for (Node n : nodes)
			this.addNode(n);
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
	
	public Iterator<Edge> getOutEdges(int id) {
		return structure.getOutEdges(id);
	}
	
	public Iterator<Edge> getInEdges(int id) {
		return structure.getInEdges(id);
	}
	
	public Iterator<Integer> getNeighborhood(final int id) {
		return new Iterator<Integer>() {
			Iterator<Edge> iter = structure.getOutEdges(id);

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Integer next() {
				Edge e = iter.next();
				return e.getFromNodeId() == id ? e.getToNodeId() : e.getFromNodeId();
			}
		};
	}
	
}
