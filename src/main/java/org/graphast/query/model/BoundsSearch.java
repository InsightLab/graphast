package org.graphast.query.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;



public interface BoundsSearch {
	public Long2IntMap getBounds();
	
	public void setBounds(Long2IntMap bounds) ;
}
