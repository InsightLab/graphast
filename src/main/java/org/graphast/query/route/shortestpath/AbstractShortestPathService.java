package org.graphast.query.route.shortestpath;

import org.graphast.model.Graph;

import com.graphhopper.util.DistanceCalc;

public abstract class AbstractShortestPathService implements ShortestPathService{

	protected Graph graph;
	
	protected int maxTime;
	protected static int wasRemoved = -1;
	protected DistanceCalc distance;
	
	public AbstractShortestPathService(Graph graph){
		
		this.graph = graph;
	
	}
	
}
