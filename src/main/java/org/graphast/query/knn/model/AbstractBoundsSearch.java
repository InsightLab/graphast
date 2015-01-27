package org.graphast.query.knn.model;

import java.util.HashMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected HashMap<String, Integer> bounds;

	public AbstractBoundsSearch(String host, int index) {
		this.bounds = new HashMap<String, Integer>();
	}

	public HashMap<String, Integer> getBounds() {
		return bounds;
	}

	public void setBounds(HashMap<String, Integer> bounds) {
		this.bounds = bounds;		
	}
	
	public String toString(){
		String str = "";
		for(String s : bounds.keySet()){
			str += s + ":" + bounds.get(s) + "\n";
		}
		return str;
	}
}
