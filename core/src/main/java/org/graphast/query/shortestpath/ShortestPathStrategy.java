package org.graphast.query.shortestpath;

import org.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(int sourceId);
	DistanceVector run(int sourceId, int targetId);

}
