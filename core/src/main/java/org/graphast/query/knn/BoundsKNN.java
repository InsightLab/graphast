package org.graphast.query.knn;

import java.util.ArrayList;
import java.util.List;

import org.graphast.enums.GraphBoundsType;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.AbstractBoundsSearchPoI;
import org.graphast.query.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

import it.unimi.dsi.fastutil.BigArrays;

public class BoundsKNN extends AbstractBoundsSearchPoI{
	

	public BoundsKNN(GraphBounds graph,GraphBoundsType type){
			DijkstraGeneric d = new DijkstraGeneric(graph);
			for(int i = 0; i < graph.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = BigArrays.index(graph.getNodes().getInt(position), graph.getNodes().getInt(position + 1));
				Bound b = new Bound(vid,  d.shortestPathPoi(vid, -1, type).getCost());
				List<Bound> oc = new ArrayList<Bound>();
				oc.add(b);
				bounds.put(vid, oc);
			}	
	}
}
