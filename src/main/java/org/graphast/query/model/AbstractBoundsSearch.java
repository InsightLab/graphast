package org.graphast.query.model;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public class AbstractBoundsSearch implements BoundsSearch{
	
	protected Long2ObjectMap<ObjectCollection<Bound>> bounds;

	public AbstractBoundsSearch() {
		
		this.bounds = new Long2ObjectOpenHashMap<ObjectCollection<Bound>>();
	
	}

	public Long2ObjectMap<ObjectCollection<Bound>> getBounds() {
		return bounds;
	}

	public void setBounds(Long2ObjectMap<ObjectCollection<Bound>> bounds) {
=======
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
>>>>>>> b10e3396bc7f230d16e8c0b9286680cfecb04cce
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
