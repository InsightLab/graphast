package org.graphast.query.knn;

import it.unimi.dsi.fastutil.BigArrays;

import org.graphast.enums.GraphBoundsType;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.AbstractBoundsSearch;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsKNNTC extends AbstractBoundsSearch{
	
	public BoundsKNNTC(GraphBounds graph, GraphBoundsType type){
			DijkstraGeneric d = new DijkstraGeneric(graph);
			for(int i = 0; i < graph.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = BigArrays.index(graph.getNodes().getInt(position), graph.getNodes().getInt(position + 1));
					bounds.put(vid,  d.shortestTS(vid, type).getCost());
			}
	}		
}
