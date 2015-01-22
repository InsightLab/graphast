package org.graphast.query.route.shortestpath2;

import org.graphast.model.Graph;
import org.graphast.model.Node;

/**
 * A subclass of GraphastAlgorithm, the ShortestPathAlgorithms will
 * handle the source node and the target node needed for this kind of
 * algorithm.
 * 
 * @author NEX2ME
 */
public abstract class ShortestPathAlgorithms extends GraphastAlgorithms{

	public Node source;
	public Node target;
	
	/**
	 * Constructs a ShortestPathAlgorithms with source and target nodes
	 * @param 	source	node that will be used as a source
	 * @param 	target	node that will be used as a target
	 * @param 	gr		Graphast where a path will be searched
	 */
	public ShortestPathAlgorithms(Graph gr, long source, long target) {
	
		super(gr);
		
		 
		
		this.source = gr.getNode(source);
		this.target = gr.getNode(target);
	
	}
	
	public abstract void execute();

}
