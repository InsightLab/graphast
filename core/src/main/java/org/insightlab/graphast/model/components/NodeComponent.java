package org.insightlab.graphast.model.components;

import java.io.Serializable;

import org.insightlab.graphast.model.Node;

public abstract class NodeComponent implements Component, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7380836164433719107L;
	
	private Node node = null;
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
}
