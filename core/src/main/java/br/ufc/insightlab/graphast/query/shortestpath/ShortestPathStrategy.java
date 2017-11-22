package br.ufc.insightlab.graphast.query.shortestpath;

import br.ufc.insightlab.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(long sourceId);
	DistanceVector run(long sourceId, long targetId);

}
