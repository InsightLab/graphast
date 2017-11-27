package org.insightlab.graphast.query.shortestpath;

import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(long sourceId) throws NodeNotFoundException;
	DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException;;

}
