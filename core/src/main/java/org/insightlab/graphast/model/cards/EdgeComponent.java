package org.insightlab.graphast.model.cards;

import org.insightlab.graphast.model.Edge;

public abstract class EdgeComponent {
	
	private Edge edge = null;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	
	public Edge getEdge() {
		return edge;
	}

}
