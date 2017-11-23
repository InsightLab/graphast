package br.ufc.insightlab.graphast.query.shortestpath;

import br.ufc.insightlab.graphast.exceptions.NodeNotFoundException;
import br.ufc.insightlab.graphast.query.utils.DistanceVector;

public interface ShortestPathStrategy {
	
	DistanceVector run(long sourceId) throws NodeNotFoundException;
	DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException;;

}
