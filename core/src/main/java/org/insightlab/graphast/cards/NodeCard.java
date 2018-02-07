package org.insightlab.graphast.cards;

import org.insightlab.graphast.model.Node;

public abstract class NodeCard {

	private Node node = null;
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
}
