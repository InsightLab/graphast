package org.insightlab.graphast.model.components;

import org.insightlab.graphast.model.Node;

public abstract class NodeComponent {

	private Node node = null;
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
}
