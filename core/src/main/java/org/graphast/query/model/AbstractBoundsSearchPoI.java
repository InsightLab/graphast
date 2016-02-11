package org.graphast.query.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractBoundsSearchPoI implements BoundsSearchPoI{
	
	protected Map<Long, List<Bound>> bounds;

	public AbstractBoundsSearchPoI() {
		this.bounds = new HashMap<Long, List<Bound>>();
	}

	public Map<Long, List<Bound>> getBounds() {
		return bounds;
	}

	public void setBounds(Map<Long, List<Bound>> bounds) {
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