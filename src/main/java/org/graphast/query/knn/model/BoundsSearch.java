package org.graphast.query.knn.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public interface BoundsSearch {
	
	public Object2ObjectMap<Long, ObjectCollection<Bound>> getBounds();
	
	public void setBounds(Object2ObjectMap<Long, ObjectCollection<Bound>> bounds) ;

}
