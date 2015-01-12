package org.graphast.query.route.shortestpath2;

import org.graphast.model.Graphast;

import com.graphhopper.util.DistanceCalc;

public abstract class AbstractShortestPathService implements ShortestPathService{

	protected Graphast graph;
	
	protected int maxTime;
	protected static int wasRemoved = -1;
	protected DistanceCalc distance;
	
	public AbstractShortestPathService(Graphast graph){
		
		this.graph = graph;
	
	}
	
}
