package org.graphast.query.knn;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
<<<<<<< HEAD
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;
=======
import org.graphast.query.route.shortestpath.dijkstra.DijkstraShortestPath;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraShortestPathConstantWeight;
>>>>>>> d3f2979cb38156569c6e8745e5fc0c8bdb0b09fa

public class BoundsKNN extends AbstractBoundsSearch{
	
	public BoundsKNN(Graph ga, String host, int index){
		super(host, index);
		//if(bounds.keySetSize() == 0){
<<<<<<< HEAD
			DijkstraGeneric d = new DijkstraGeneric(ga);
			for(int i = 0; i < ga.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = ga.getNodes().getInt(position);
				bounds.put(Long.toString(vid),  d.shortestPathPoi(vid, -1).getDistance());
=======
			DijkstraShortestPath d = new DijkstraShortestPathConstantWeight(ga);
			for(int i = 0; i < ga.getNumberOfNodes(); i++){
				long position = i*Node.NODE_BLOCKSIZE;
				long vid = ga.getNodes().getInt(position);
				bounds.put(Long.toString(vid),  d.shortestPathPoi(vid, -1).toString());
>>>>>>> d3f2979cb38156569c6e8745e5fc0c8bdb0b09fa
			}
		//}
	}
	
	public BoundsKNN(String host, int index){
		super(host, index);
	}
}
