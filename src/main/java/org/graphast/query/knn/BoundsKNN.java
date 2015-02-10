package org.graphast.query.knn;

import org.graphast.enums.GraphBoundsType;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.AbstractBoundsSearch;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsKNN extends AbstractBoundsSearch{
	
	public BoundsKNN(GraphBounds graph, int categoryId, GraphBoundsType type){
			DijkstraGeneric d = new DijkstraGeneric(graph);
			for(int i = 0; i < graph.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = graph.getNodes().getInt(position);
				
				if(type.equals(GraphBoundsType.NORMAL)) {
					bounds.put(vid,  d.shortestPathPoi(vid, categoryId).getCost());
				}
				else if (type.equals(GraphBoundsType.LOWER)) {
					bounds.put(vid,  d.shortestLowerPathPoi(vid, categoryId).getCost());
				}
				else {
					bounds.put(vid,  d.shortestUpperPathPoi(vid, categoryId).getCost());
				}
			}	
	}
}
