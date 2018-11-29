package br.ufc.insightlab.query.map_matching;

import br.ufc.insightlab.model.Edge;
import br.ufc.insightlab.model.Node;

public interface MapMatchingStrategy {

	public abstract Node getNearestNode(double lat, double lng);
	
	public abstract Edge getNearestEdge(double lat, double lng);
	
}
