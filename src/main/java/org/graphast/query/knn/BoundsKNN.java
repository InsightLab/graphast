package org.graphast.query.knn;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsKNN extends AbstractBoundsSearch{
	
	public BoundsKNN(Graph ga){
		//if(bounds.keySetSize() == 0){
			DijkstraGeneric d = new DijkstraGeneric(ga);
			for(int i = 0; i < ga.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = ga.getNodes().getInt(position);
				bounds.put(vid,  d.shortestPathPoi(vid, -1).getDistance());
			}	
//			Dijkstra dcw = new DijkstraConstantWeight(ga);
//			for(int i = 0; i < ga.getNumberOfNodes(); i++){
//				long position = i*Node.NODE_BLOCKSIZE;
//				long vid = ga.getNodes().getInt(position);
//				bounds.put(Long.toString(vid),  dcw.shortestPathPoi(vid, -1).toString());
//			}
	}
	
	public BoundsKNN(long host, int index){
		super(host, index);
	}
}
