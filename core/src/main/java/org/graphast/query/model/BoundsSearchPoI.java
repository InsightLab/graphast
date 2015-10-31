package org.graphast.query.model;

import java.util.List;
import java.util.Map;

public interface BoundsSearchPoI {

	public Map<Long, List<Bound>> getBounds();

	public void setBounds(Map<Long, List<Bound>> bounds);

}
