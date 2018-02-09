package org.insightlab.graphast.model.components;

import org.insightlab.graphast.model.Graph;

public abstract class GraphComponent {

	private Graph graph = null;
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
}
