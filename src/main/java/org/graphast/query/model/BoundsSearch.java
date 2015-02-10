package org.graphast.query.model;

import java.util.HashMap;



public interface BoundsSearch {
	public HashMap<Long, Integer> getBounds();
	
	public void setBounds(HashMap<Long, Integer> bounds) ;
}