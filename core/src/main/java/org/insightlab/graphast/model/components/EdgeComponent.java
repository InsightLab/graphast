package org.insightlab.graphast.model.components;

import java.io.Serializable;

import org.insightlab.graphast.model.Edge;

public abstract class EdgeComponent implements Serializable {
	
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
