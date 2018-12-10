package br.ufc.insightlab.graphast.query.map_matching;

import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Node;

public interface MapMatchingStrategy {

	public abstract Node getNearestNode(double lat, double lng);
	
	public abstract Edge getNearestEdge(double lat, double lng);
	
}
