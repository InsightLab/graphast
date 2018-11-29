package br.ufc.insightlab.model.components;

import br.ufc.insightlab.model.Graph;

import java.io.Serializable;

public abstract class GraphComponent implements Component, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5846392764694019776L;
	
	private Graph graph = null;
	
	public void setGraph(Graph graph) {
		this.graph = graph;
		onAttach();
	}
	
	public Graph getGraph() {
		return graph;
	}

	public void onAttach() {}

}
