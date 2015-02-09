package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.io.IOException;

import org.graphast.enums.CompressionType;
import org.graphast.util.FileUtils;

public class GraphBoundsImpl extends GraphImpl implements GraphBounds {

<<<<<<< HEAD
	private Long2IntMap upperBound;
	private Long2IntMap lowerBound;

=======
	private Long2ShortMap upperBound;
	private Long2ShortMap lowerBound;
	
>>>>>>> de970cdd38205dff3483c22b1ce3b33a228020a2
	public GraphBoundsImpl(String directory) {
		this(directory, CompressionType.GZIP_COMPRESSION);
	}

	public GraphBoundsImpl(String directory, CompressionType compressionType) {
		super(directory, compressionType);
<<<<<<< HEAD
		upperBound = new Long2IntOpenHashMap();
		lowerBound = new Long2IntOpenHashMap();
=======
		upperBound = new Long2ShortOpenHashMap();
		lowerBound = new Long2ShortOpenHashMap();
>>>>>>> de970cdd38205dff3483c22b1ce3b33a228020a2

	}

	public void save() throws IOException {
		super.save();
<<<<<<< HEAD
		FileUtils.saveLong2IntMap(directory + "/upperBound", upperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(directory + "/lowerBound", lowerBound, blockSize, compressionType);
=======
		FileUtils.saveLong2ShortMap(directory + "/upperBound", upperBound, blockSize, compressionType);
		FileUtils.saveLong2ShortMap(directory + "/lowerBound", lowerBound, blockSize, compressionType);
>>>>>>> de970cdd38205dff3483c22b1ce3b33a228020a2
	}


	public void load() throws IOException {
		super.load();
<<<<<<< HEAD
		FileUtils.loadLong2IntMap(directory + "/upperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(directory + "/lowerBound", blockSize, compressionType);
=======
		FileUtils.loadLong2ShortMap(directory + "/upperBound", blockSize, compressionType);
		FileUtils.loadLong2ShortMap(directory + "/lowerBound", blockSize, compressionType);
>>>>>>> de970cdd38205dff3483c22b1ce3b33a228020a2
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
