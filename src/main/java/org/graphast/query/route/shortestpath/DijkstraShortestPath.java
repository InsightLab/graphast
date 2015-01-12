package org.graphast.query.route.shortestpath;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import org.graphast.model.Graphast;

/**
 * An abstract class that handle DijkstraShortestPath.
 * 
 * @author NEX2ME
 */
public abstract class DijkstraShortestPath extends ShortestPathAlgorithms {

	public LongSet settledNodes;
	public LongSet unsettledNodes;
	
	public Long2LongMap predecessors;
	public Long2IntMap distance;
	
	/**
	 * Constructs a DijkstraShortestPath based on the source and target nodes of a 
	 * given Graphast gr.
	 * 
	 * @param	source	node that will be used as an origin for this kind of algorithm
	 * @param	target	node that will be used as a destination for this kind of algorithm
	 * @param	gr	Graphast
	 */
	public DijkstraShortestPath(Graphast gr, long source, long target) {
		
		super(gr, source,target);
		
		settledNodes = new LongOpenHashSet(); 
		unsettledNodes = new LongOpenHashSet();

		predecessors = new Long2LongOpenHashMap();
		distance = new Long2IntOpenHashMap();
	
	}
	
	public abstract void execute();
	
}
