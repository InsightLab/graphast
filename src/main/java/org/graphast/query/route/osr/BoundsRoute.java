package org.graphast.query.route.osr;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import org.graphast.model.GraphBounds;
import org.graphast.query.model.AbstractBoundsSearch;
import org.graphast.query.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraVariableWeight;

public class BoundsRoute extends AbstractBoundsSearch {

	/**
	 * This constructor calculates the minimum path (we call this bound) from each vertex 
	 * passing by one PoI of each category.
	 * @param	graph	graph that will be used to find all paths according 
	 * 					to the above description
	 */
	public BoundsRoute(GraphBounds graph, short graphType){

		super();

		DijkstraVariableWeight d = new DijkstraVariableWeight(graph);
		IntSet categoriesIds = graph.getCategories();

//		System.out.println(categoriesIds);
		
		for(int i = 0; i < graph.getNumberOfNodes(); i++){

			long nodeId = graph.getNode(i).getId();

			/*
			 * The next line is going to return a collection of Bound containing the distance from this vid to 
			 * the set of categories passed by argument on the variable 'categories'.
			 */
			ObjectCollection<Bound> bound = d.shortestPathCategories(nodeId, categoriesIds, graphType);

			System.out.println("nodeId: " + graph.getNode(i).getId() + " Bound: " + bound);
			//The next line is going to associate the current vid to the bounds of the previous line.
			bounds.put(nodeId,  bound);

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
