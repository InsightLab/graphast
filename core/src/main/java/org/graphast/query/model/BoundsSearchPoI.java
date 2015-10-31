package org.graphast.query.model;

import java.util.Collection;
import java.util.Map;

public interface BoundsSearchPoI {

	public Map<Long, Collection<Bound>> getBounds();

	public void setBounds(Map<Long, Collection<Bound>> bounds);

}
