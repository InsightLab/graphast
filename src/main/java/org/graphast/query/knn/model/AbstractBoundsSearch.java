package org.graphast.query.knn.model;

import java.util.HashMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected HashMap<Long, Integer> bounds;

	public AbstractBoundsSearch(long host, int index) {
		this.bounds = new HashMap<Long, Integer>();
	}

	public HashMap<Long, Integer> getBounds() {
		return bounds;
	}

	public void setBounds(HashMap<Long, Integer> bounds) {
		this.bounds = bounds;		
	}
	
	public String toString(){
		String str = "";
		for(long s : bounds.keySet()){
			str += s + ":" + bounds.get(s) + "\n";
		}
		return str;
	}
}
