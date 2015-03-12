package org.graphast.query.model;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public class AbstractBoundsSearchPoI implements BoundsSearchPoI{
	
	protected Long2ObjectMap<ObjectCollection<Bound>> bounds;

	public AbstractBoundsSearchPoI() {
		
		this.bounds = new Long2ObjectOpenHashMap<ObjectCollection<Bound>>();
	
	}

	public Long2ObjectMap<ObjectCollection<Bound>> getBounds() {
		return bounds;
	}

	public void setBounds(Long2ObjectMap<ObjectCollection<Bound>> bounds) {
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