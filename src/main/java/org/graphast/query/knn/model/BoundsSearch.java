package org.graphast.query.knn.model;

import java.util.HashMap;



public interface BoundsSearch {
	public HashMap<String, Integer> getBounds();
	
	public void setBounds(HashMap<String, Integer> bounds) ;
}
