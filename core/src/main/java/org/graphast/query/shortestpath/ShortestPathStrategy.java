package org.graphast.query.shortestpath;

import org.graphast.model.Node;
import org.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(Node source);
	DistanceVector run(Node source, Node target);

}
