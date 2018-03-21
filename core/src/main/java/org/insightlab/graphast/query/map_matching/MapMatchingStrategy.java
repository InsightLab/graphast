package org.insightlab.graphast.query.map_matching;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;

public interface MapMatchingStrategy {

	public abstract Node getNearestNode(double lat, double lng);
	
	public abstract Edge getNearestEdge(double lat, double lng);
	
}
