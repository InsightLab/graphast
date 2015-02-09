package org.graphast.query.route.osr;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
import org.graphast.query.knn.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsRoute extends AbstractBoundsSearch {

	//TODO DOUBLE CHECK THIS CONSTRUCTOR!
	public BoundsRoute(Graph graph){
		super();
		DijkstraGeneric d = new DijkstraGeneric(graph);
		IntSet categories = graph.getCategories();
		
		for(int i = 0; i < graph.getNumberOfNodes(); i++){
			long position = i*Node.NODE_BLOCKSIZE;
			long vid = graph.getNodes().getInt(position);
			//The next line is going to return a collection of Bound containing the distance from this vid to 
			// the set of categories passed by argument on the variable 'categories'.
			ObjectCollection<Bound> bound = d.shortestPathCategories(vid, categories);
			
			//The next line is going to associate the current vid to the bounds of the previous line.
			bounds.put(vid,  bound);
		} 
	
	}
	
	public BoundsRoute(){
		super();
	}
	
	public Bound getBound(int id, int category){
		if(bounds.containsKey(id)){
			
			ObjectCollection<Bound> bound = bounds.get(id);
			
			for(Bound boundIterator : bound) {
				if(boundIterator.getId() == category) {
					return boundIterator;
				}
			}
			
		}
		return new Bound();
	}
	
}
