package org.graphast.query.shortestpath;

import org.graphast.model.Node;
import org.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	public DistanceVector run(Node source);
	public DistanceVector run(Node source, Node target);

}
