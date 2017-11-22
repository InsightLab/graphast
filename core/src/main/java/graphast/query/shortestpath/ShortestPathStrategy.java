package graphast.query.shortestpath;

import graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(long sourceId);
	DistanceVector run(long sourceId, long targetId);

}
