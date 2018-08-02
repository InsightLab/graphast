package org.insightlab.graphast.model.components;

import org.insightlab.graphast.model.Edge;

import java.io.Serializable;

public abstract class EdgeComponent implements Component, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6405905013226850089L;
	
	private Edge edge = null;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	
	public Edge getEdge() {
		return edge;
	}

}
