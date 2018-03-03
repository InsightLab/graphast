package org.insightlab.graphast.query.map_matching;

import org.insightlab.graphast.model.Node;

public interface MapMatchingStrategy {

	public abstract Node getNearestNode(double lat, double lng);
	
	public abstract Node getNearestEdge(double lat, double lng);
	
}
