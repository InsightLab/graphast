package org.graphast.query.knn;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import org.graphast.enums.GraphBoundsType;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.AbstractBoundsSearchPoI;
import org.graphast.query.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsKNN extends AbstractBoundsSearchPoI{
	

	public BoundsKNN(GraphBounds graph,GraphBoundsType type){
			DijkstraGeneric d = new DijkstraGeneric(graph);
			for(int i = 0; i < graph.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = BigArrays.index(graph.getNodes().getInt(position), graph.getNodes().getInt(position + 1));
				Bound b = new Bound(vid,  d.shortestPathPoi(vid, -1, type).getCost());
				ObjectCollection<Bound> oc = new AbstractObjectCollection<Bound>();
				oc.add(b);
				bounds.put(vid, oc);
			}	
	}
}
