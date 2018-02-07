package org.insightlab.graphast.cards;

import org.insightlab.graphast.model.Edge;

public abstract class EdgeCard {
	
	private Edge edge = null;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	
	public Edge getEdge() {
		return edge;
	}

}
