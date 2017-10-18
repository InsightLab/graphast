package org.graphast.model;

public class Node extends GraphObject {
	
	private int id;
	
	public Node(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) return false;
		return ((Node)obj).getId() == id;
 	}
	
	@Override
	public int hashCode() {
		return id;
	}

}
