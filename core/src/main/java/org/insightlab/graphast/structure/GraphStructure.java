package org.insightlab.graphast.structure;

import java.util.Iterator;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;

public interface GraphStructure {
	
	long getNumberOfNodes();
	long getNumberOfEdges();

	void addNode(Node n);
	void addEdge(Edge e);
	
	boolean containsNode(final long id);
	
	Iterator<Node> nodeIterator();
	Iterator<Edge> edgeIterator();
	
	Iterator<Edge> getOutEdges(final long id);
	Iterator<Edge> getInEdges(final long id);
	
}
