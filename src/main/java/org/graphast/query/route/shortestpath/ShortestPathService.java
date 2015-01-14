package org.graphast.query.route.shortestpath;

import java.util.Date;

import org.graphast.model.GraphastNode;

public interface ShortestPathService {
	public int shortestPath(GraphastNode source, GraphastNode target);
	
	public int shortestPath(long source, long target);

	public int shortestPath(GraphastNode source, GraphastNode target, Date time);
	
	public int shortestPath(long source, long target, Date time);
}
