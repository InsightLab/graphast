package org.graphast.query.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbstractBoundsSearchPoI implements BoundsSearchPoI{
	
	protected Map<Long, Collection<Bound>> bounds;

	public AbstractBoundsSearchPoI() {
		this.bounds = new HashMap<Long, Collection<Bound>>();
	}

	public Map<Long, Collection<Bound>> getBounds() {
		return bounds;
	}

	public void setBounds(Map<Long, Collection<Bound>> bounds) {
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