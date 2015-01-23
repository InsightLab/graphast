package org.graphast.query.route.shortestpath;

import java.util.Date;

import org.graphast.model.Node;

public interface ShortestPathService {
	public int shortestPath(Node source, Node target);
	
	public int shortestPath(long source, long target);

	public int shortestPath(Node source, Node target, Date time);
	
	public int shortestPath(long source, long target, Date time);
}
