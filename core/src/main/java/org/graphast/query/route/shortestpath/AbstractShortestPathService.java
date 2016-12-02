package org.graphast.query.route.shortestpath;

import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import com.graphhopper.util.DistanceCalc;

/**Abstract class to determinate the basic structure for any type of shortestPath algorithm*/
public abstract class AbstractShortestPathService implements ShortestPathService{

	protected Graph graph;
	protected GraphBounds graphBounds;
	
	protected int maxTime;
	protected static int wasRemoved = -1;
	protected DistanceCalc distance;

	public AbstractShortestPathService(GraphBounds graphBounds){
		this.graph = graphBounds;
		this.graphBounds = graphBounds;
	}

	public AbstractShortestPathService(Graph graph){
		this.graph = graph;
	}
}
