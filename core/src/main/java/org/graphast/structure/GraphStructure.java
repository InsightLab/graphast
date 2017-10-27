package org.graphast.structure;

import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

public interface GraphStructure {
	
	int getNumberOfNodes();
	int getNumberOfEdges();

	void addNode(Node n);
	void addEdge(Edge e);
	
	Iterator<Node> nodeIterator();
	Iterator<Edge> edgeIterator();
	
	Iterator<Edge> getOutEdges(final int id);
	Iterator<Edge> getInEdges(final int id);
	
}
