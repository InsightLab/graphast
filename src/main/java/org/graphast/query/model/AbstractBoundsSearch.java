package org.graphast.query.model;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected Long2IntMap bounds;

	public AbstractBoundsSearch() {
		bounds = new Long2IntOpenHashMap();
	}
	
	public Long2IntMap getBounds() {
		return bounds;
	}

	public void setBounds(Long2IntMap bounds) {
=======
import it.unimi.dsi.fastutil.longs.Long2ShortMap;


public class AbstractBoundsSearch implements BoundsSearch{
	protected Long2ShortMap bounds;

	public Long2ShortMap getBounds() {
		return bounds;
	}

	public void setBounds(Long2ShortMap bounds) {
>>>>>>> modification on Bounds of KNN. need of cost modification
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
