package org.graphast.query.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected Long2IntMap bounds;

	public Long2IntMap getBounds() {
		return bounds;
	}

	public void setBounds(Long2IntMap bounds) {
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
