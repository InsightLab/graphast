package org.graphast.model;

public class Node extends GraphObject {
	
	private long id;
	
	public Node(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) return false;
		return ((Node)obj).getId() == id;
 	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

}
