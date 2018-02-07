package org.insightlab.graphast.cards;

import org.insightlab.graphast.model.Graph;

public abstract class GraphCard {

	private Graph graph = null;
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
}
