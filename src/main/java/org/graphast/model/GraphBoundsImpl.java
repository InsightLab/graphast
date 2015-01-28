package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;

import java.io.IOException;

import org.graphast.util.FileUtils;

public class GraphBoundsImpl extends GraphImpl implements GraphBounds {

	private Long2ShortMap upperBound;
	private Long2ShortMap lowerBound;
	

	public GraphBoundsImpl(String directory) {
		super(directory);
		upperBound = new Long2ShortOpenHashMap();
		lowerBound = new Long2ShortOpenHashMap();

	}
	
	public void save() throws IOException {
		super.save();
		FileUtils.saveLong2ShortMap(directory + "/upperBound", upperBound, blockSize);
		FileUtils.saveLong2ShortMap(directory + "/lowerBound", lowerBound, blockSize);
	}

	
	public void load() throws IOException {
		super.load();
		FileUtils.loadLong2ShortMap(directory + "/upperBound", blockSize);
		FileUtils.loadLong2ShortMap(directory + "/lowerBound", blockSize);
	}
	
	public void createLowerBounds() {
		int numberOfEdges = getNumberOfEdges();
		Edge edge; 
		
		for(int i=0; i<numberOfEdges; i++) {
			edge = getEdge(i);
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
	
	@Override
	public void createBounds() {
		createUpperBounds();
		createLowerBounds();
	}
	
	@Override
	public Long2ShortMap getUpperBound() {
		return upperBound;
	}

	@Override
	public Long2ShortMap getLowerBound() {
		return lowerBound;
	}

}
