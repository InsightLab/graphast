package org.graphast.query.route.osr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.graphast.exception.GraphastException;
import org.graphast.model.GraphBounds;
import org.graphast.query.model.AbstractBoundsSearchPoI;
import org.graphast.query.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;

import it.unimi.dsi.fastutil.ints.IntSet;

public class BoundsRoute extends AbstractBoundsSearchPoI implements Serializable {

	private static final long serialVersionUID = 3156968902457731581L;

	private GraphBounds graph;
	private short graphType;
	
	/**
	 * @param	graph	graph that will be used to find all paths according 
	 * 					to the above description
	 * @param graphType description of the graph type, 0 = normal graph, 1 = lower bound graph
	 * 					2 = upper bound graph.
	 */
	public BoundsRoute(GraphBounds graph, short graphType){
		super();
		this.graph = graph;
		this.graphType = graphType;
	}

	/**
	 * Calculates the minimum path (we call this bound) from each vertex 
	 * passing by one PoI of each category.
	 */
	public void createBounds() {
		DijkstraLinearFunction d = new DijkstraLinearFunction(graph);
		IntSet categoriesIds = graph.getCategories();

		for(int i = 0; i < graph.getNumberOfNodes(); i++){

			long nodeId = graph.getNode(i).getId();

			/*
			 * The next line is going to return a collection of Bound containing the distance from this vid to 
			 * the set of categories passed by argument on the variable 'categories'.
			 */
			List<Bound> bound = d.shortestPathCategories(nodeId, categoriesIds, graphType);

			//The next line is going to associate the current vid to the bounds of the previous line.
			bounds.put(nodeId,  bound);
		} 
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
	
	@SuppressWarnings("unchecked")
	public void load() {
		ObjectInputStream in = null;
		try {
			String dir = graph.getAbsoluteDirectory();
			in = new ObjectInputStream(new FileInputStream(dir + "/bounds" + graphType));
			this.bounds = (Map<Long, List<Bound>>) in.readObject();
			in.close();	
		} catch (Exception e) {
			throw new GraphastException(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new GraphastException(e.getMessage(), e);
				}
			}
		}
		
	}
	
	public void save() {
		ObjectOutputStream out = null;
		try {
			String dir = graph.getAbsoluteDirectory();
			out = new ObjectOutputStream(new FileOutputStream(dir + "/bounds" + graphType));
			out.writeObject(bounds);
			out.close();	
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new GraphastException(e.getMessage(), e);
				}
			}
		}
	}

}
