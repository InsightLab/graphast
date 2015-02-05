package org.graphast.query.knn.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;


public class AbstractBoundsSearch implements BoundsSearch{
	protected Object2ObjectMap<Long, ObjectCollection<Bound>> bounds;

	public AbstractBoundsSearch() {
		this.bounds = new Object2ObjectOpenHashMap<Long, ObjectCollection<Bound>>();
	}

	public Object2ObjectMap<Long, ObjectCollection<Bound>> getBounds() {
		return bounds;
	}

	public void setBounds(Object2ObjectMap<Long, ObjectCollection<Bound>> bounds) {
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
