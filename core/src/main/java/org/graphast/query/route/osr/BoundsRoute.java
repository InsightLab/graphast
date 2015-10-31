package org.graphast.query.route.osr;

import java.io.Serializable;
import java.util.Collection;

import org.graphast.model.GraphBounds;
import org.graphast.query.model.AbstractBoundsSearchPoI;
import org.graphast.query.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;

import it.unimi.dsi.fastutil.ints.IntSet;

public class BoundsRoute extends AbstractBoundsSearchPoI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3156968902457731581L;

	/**
	 * This constructor calculates the minimum path (we call this bound) from each vertex 
	 * passing by one PoI of each category.
	 * @param	graph	graph that will be used to find all paths according 
	 * 					to the above description
	 * @param graphType description of the graph type, 0 = normal graph, 1 = lower bound graph
	 * 					2 = upper bound graph.
	 */
	public BoundsRoute(GraphBounds graph, short graphType){

		super();

		DijkstraLinearFunction d = new DijkstraLinearFunction(graph);
		IntSet categoriesIds = graph.getCategories();

		for(int i = 0; i < graph.getNumberOfNodes(); i++){

			long nodeId = graph.getNode(i).getId();

			/*
			 * The next line is going to return a collection of Bound containing the distance from this vid to 
			 * the set of categories passed by argument on the variable 'categories'.
			 */
			Collection<Bound> bound = d.shortestPathCategories(nodeId, categoriesIds, graphType);

			//The next line is going to associate the current vid to the bounds of the previous line.
			bounds.put(nodeId,  bound);

		} 

	}

	public BoundsRoute(){
		super();
	}

	public Bound getBound(int id, int category){
		if(bounds.containsKey(id)){

			Collection<Bound> bound = bounds.get(id);

			for(Bound boundIterator : bound) {
				if(boundIterator.getId() == category) {
					return boundIterator;
				}
			}

		}
		return new Bound();
	}

}
