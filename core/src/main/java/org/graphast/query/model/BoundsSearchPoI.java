package org.graphast.query.model;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public interface BoundsSearchPoI {

	public Long2ObjectMap<ObjectCollection<Bound>> getBounds();

	public void setBounds(Long2ObjectMap<ObjectCollection<Bound>> bounds);

}
