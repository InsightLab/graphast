package org.graphast.query.knn;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.AbstractBoundsSearch;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraVariableWeight;

public class BoundsKNN extends AbstractBoundsSearch{
	
	public BoundsKNN(Graph ga){
		super();
		//if(bounds.keySetSize() == 0){
			DijkstraVariableWeight d = new DijkstraVariableWeight(ga);
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
	
	public BoundsKNN(){
		super();
	}
}
