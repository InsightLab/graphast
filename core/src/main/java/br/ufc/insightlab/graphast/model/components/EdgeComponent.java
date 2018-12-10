package br.ufc.insightlab.graphast.model.components;

import br.ufc.insightlab.graphast.model.Edge;

import java.io.Serializable;

public abstract class EdgeComponent implements Component, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6405905013226850089L;
	
	private Edge edge = null;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
		onAttach();
	}
	
	public Edge getEdge() {
		return edge;
	}

	public void onAttach() {}

}
