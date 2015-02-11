package org.graphast.query.knn.model;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected Long2ShortMap bounds;

	public Long2ShortMap getBounds() {
		return bounds;
	}

	public void setBounds(Long2ShortMap bounds) {
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
