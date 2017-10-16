package org.graphast.storage;

import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

public interface GraphStorage {
	
	public int getNumberOfNodes();
	public int getNumberOfEdges();

	public void addNode(Node n);
	public void addEdge(Edge e);
	
	public Node getNode(final int id);
	public Iterator<Node> nodeIterator();
	public Iterator<Edge> edgeIterator();
	
	public Iterator<Edge> getOutEdges(final Node n);
	public Iterator<Edge> getInEdges(final Node n);
	
}
