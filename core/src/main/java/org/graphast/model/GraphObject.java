package org.graphast.model;

public abstract class GraphObject {
	
	private Bundle b;
	
	public Bundle getBundle() {
		if (b == null) b = new Bundle();
		return b;
	}

}
