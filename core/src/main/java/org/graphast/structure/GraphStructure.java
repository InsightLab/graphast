package org.graphast.structure;

import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

public interface GraphStructure {
	
	int getNumberOfNodes();
	int getNumberOfEdges();

	void addNode(Node n);
	void addEdge(Edge e);
	
	Node getNode(final int id);
	Iterator<Node> nodeIterator();
	Iterator<Edge> edgeIterator();
	
	Iterator<Edge> getOutEdges(final Node n);
	Iterator<Edge> getInEdges(final Node n);
	
}
