package org.graphast.query.knn;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsKNN extends AbstractBoundsSearch{
	
	public BoundsKNN(Graph ga, String host, int index){
		super(host, index);
		//if(bounds.keySetSize() == 0){
			DijkstraGeneric d = new DijkstraGeneric(ga);
			for(int i = 0; i < ga.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = ga.getNodes().getInt(position);
				bounds.put(Long.toString(vid),  d.shortestPathPoi(vid, -1).getDistance());
			}
		//}
	}
	
	public BoundsKNN(String host, int index){
		super(host, index);
	}
}
