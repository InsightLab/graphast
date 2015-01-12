package org.graphast.query.route.shortestpath;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;

import org.graphast.model.Graphast;
/**
 * An abstract class that structure for any type of algorithm
 * that our framework may implement.
 * 
 * @author NEX2ME
 *
 */
public abstract class GraphastAlgorithms {

	public Graphast gr;
	
	public final IntBigArrayBigList nodes;
	public final IntBigArrayBigList edges;
	
	/**
	 * Constructs a GraphastAlgorithms with the passed parameter.
	 * @param fg
	 */
	public GraphastAlgorithms(Graphast gr) {
	
		this.gr = gr;
		this.nodes = gr.getNodes();
		this.edges = gr.getEdges();
	
	}
	
	/**
	 * Executes the algorithm.
	 */
	public abstract void execute();

}
