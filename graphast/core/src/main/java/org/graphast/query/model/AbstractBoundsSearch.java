package org.graphast.query.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

@Deprecated
public class AbstractBoundsSearch implements BoundsSearch{
	protected Long2IntMap bounds;

	public AbstractBoundsSearch() {
		bounds = new Long2IntOpenHashMap();
	}
	
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