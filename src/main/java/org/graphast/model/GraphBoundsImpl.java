package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.io.IOException;

import org.graphast.enums.CompressionType;
import org.graphast.util.FileUtils;

public class GraphBoundsImpl extends GraphImpl implements GraphBounds {

	private Long2IntMap upperBound;
	private Long2IntMap lowerBound;

	public GraphBoundsImpl(String directory) {
		this(directory, CompressionType.GZIP_COMPRESSION);
	}

	public GraphBoundsImpl(String directory, CompressionType compressionType) {
		super(directory, compressionType);
		upperBound = new Long2IntOpenHashMap();
		lowerBound = new Long2IntOpenHashMap();

	}

	public void save() throws IOException {
		super.save();
		FileUtils.saveLong2IntMap(directory + "/upperBound", upperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(directory + "/lowerBound", lowerBound, blockSize, compressionType);
	}


	public void load() throws IOException {
		super.load();
		FileUtils.loadLong2IntMap(directory + "/upperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(directory + "/lowerBound", blockSize, compressionType);
	}

	public void createLowerBounds() {
		int numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(long i=0; i<numberOfEdges; i++) {
			edge = super.getEdge(i);
			lowerBound.put((long)edge.getId(), getMinimunCostValue(edge.getCosts()));
		}
	}

	public void createUpperBounds() {

		int numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(int i=0; i<numberOfEdges; i++) {
			edge = getEdge(i);
			upperBound.put((long)edge.getId(), getMaximunCostValue(edge.getCosts()));
		}
	}
	
	/**
	 * 
	 * @param v
	 * @param graphType The type of graph the will be used to retrieve costs needed. 0 = Regular Costs; 1 = Lower Bound Costs;
	 * 					3 = Upper Bound Costs.
	 * @return
	 */
	public Long2IntMap accessNeighborhood(Node v, short graphType){

		Long2IntMap neighbors = new Long2IntOpenHashMap();
		int cost;
		
		for (Long e : this.getOutEdges(v.getId()) ) {

			Edge edge = this.getEdge(e);
			long neighborNodeId =  edge.getToNode();
			
			if(graphType == 0) {
				cost =  edge.getDistance();
			} else if(graphType == 1) {
				cost = getLowerBound().get(edge.getId());
			} else {
				cost = getUpperBound().get(edge.getId());
			}
			
			if(!neighbors.containsKey(neighborNodeId)) {
				neighbors.put(neighborNodeId, cost);
			}else{
				if(neighbors.get(neighborNodeId) > cost){
					neighbors.put(neighborNodeId, cost);
				}
			}
		}

		return neighbors;

	}	

	@Override
	public void createBounds() {
		createUpperBounds();
		createLowerBounds();
	}

	@Override
	public Long2IntMap getUpperBound() {
		return upperBound;
	}

	@Override
	public Long2IntMap getLowerBound() {
		return lowerBound;
	}

}
