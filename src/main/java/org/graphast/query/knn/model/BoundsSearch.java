package org.graphast.query.knn.model;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;



public interface BoundsSearch {
	public Long2ShortMap getBounds();
	
	public void setBounds(Long2ShortMap bounds) ;
}
