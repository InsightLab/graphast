package org.graphast.query.route.shortestpath2;

import java.util.Date;

import org.graphast.model.GraphastNode;

public interface ShortestPathService {

	int shortestPath(GraphastNode source, GraphastNode target);
	
	int shortestPath(GraphastNode source, GraphastNode target, Date time);
	
}
