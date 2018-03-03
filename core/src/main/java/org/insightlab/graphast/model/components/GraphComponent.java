package org.insightlab.graphast.model.components;

import java.io.Serializable;

import org.insightlab.graphast.model.Graph;

public abstract class GraphComponent implements Component, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5846392764694019776L;
	
	private Graph graph = null;
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
}
