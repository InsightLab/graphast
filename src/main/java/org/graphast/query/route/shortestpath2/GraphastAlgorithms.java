package org.graphast.query.route.shortestpath2;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;

import org.graphast.model.Graph;
/**
 * An abstract class that structure for any type of algorithm
 * that our framework may implement.
 * 
 * @author NEX2ME
 *
 */
public abstract class GraphastAlgorithms {

	public Graph graph;
	
	public final IntBigArrayBigList nodes;
	public final IntBigArrayBigList edges;
	
	/**
	 * Constructs a GraphastAlgorithms with the passed parameter.
	 * @param graph Graph used by the algorithm.
	 */
	public GraphastAlgorithms(Graph graph) {
	
		this.graph = graph;
		this.nodes = graph.getNodes();
		this.edges = graph.getEdges();
	
	}
	
	/**
	 * Executes the algorithm.
	 */
	public abstract void execute();

}
