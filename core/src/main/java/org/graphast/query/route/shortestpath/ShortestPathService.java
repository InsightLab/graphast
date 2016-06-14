package org.graphast.query.route.shortestpath;

import java.util.Date;

import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.Path;

public interface ShortestPathService {
	
	public Path shortestPath(Node source, Node target);
	
	public Path shortestPath(long source, long target);

	public Path shortestPath(Node source, Node target, Date time);
	
	public Path shortestPath(long source, long target, Date time);
	
	public Path shortestPath(Node source, Node target, Node skippedNode);
	
	public Path shortestPath(long source, long target, Node skippedNode);
	
	public Path shortestPath(long source, long target, Date time, Node skippedNode);

	public void setMaxVisitedNodes(int maxVisitedNodes);
	
//	public Int2ObjectMap<Path> shortestPath(Node source);

}
