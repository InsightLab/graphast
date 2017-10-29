package org.graphast.query.shortestpath;

import org.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(long sourceId);
	DistanceVector run(long sourceId, long targetId);

}
