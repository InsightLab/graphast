package org.graphast.query.model;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public interface BoundsSearch {
	
	public Long2ObjectMap<ObjectCollection<Bound>> getBounds();
	
	public void setBounds(Long2ObjectMap<ObjectCollection<Bound>> bounds);

}
=======
import it.unimi.dsi.fastutil.longs.Long2IntMap;

public interface BoundsSearch {
	public Long2IntMap getBounds();
	
	public void setBounds(Long2IntMap bounds);
}
>>>>>>> b10e3396bc7f230d16e8c0b9286680cfecb04cce