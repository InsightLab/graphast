package br.ufc.insightlab.graphast.model.components;

import br.ufc.insightlab.graphast.model.Node;

import java.io.Serializable;

public abstract class NodeComponent implements Component, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7380836164433719107L;
	
	private Node node = null;
	
	public void setNode(Node node) {
		this.node = node;
		onAttach();
	}
	
	public Node getNode() {
		return node;
	}

	public void onAttach() {}
}
