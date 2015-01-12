package org.graphast.model;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.shorts.ShortBigArrayBigList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.exception.GraphastException;
import org.graphast.geometry.Point;
import org.graphast.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphastImpl implements Graphast {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Long2LongMap nodeIndex = new Long2LongOpenHashMap();

	private String directory;

	private IntBigArrayBigList nodes;

	private IntBigArrayBigList edges;

	private ObjectBigList<String> nodesLabels;

	private ObjectBigList<String> edgesLabels;

	private ShortBigArrayBigList costs;

	private IntBigArrayBigList points;

	private int blockSize = 4096;

	private static int secondsDay = 86400;

	/**
	 * Creates a Graphast for the given directory passed as parameter.
	 * 
	 * This constructor will instantiate all lists needed 
	 * to properly handle the information of a Graphast, 
	 * e.g. nodes, edges, labels, etc.
	 * 
	 * @param directory
	 */
	public GraphastImpl(String directory) {

		this.directory = directory;

		nodes = new IntBigArrayBigList();
		edges = new IntBigArrayBigList();
		nodesLabels = new ObjectBigArrayBigList<String>();
		edgesLabels = new ObjectBigArrayBigList<String>();
		costs = new ShortBigArrayBigList();
		points = new IntBigArrayBigList();

		nodeIndex.defaultReturnValue(-1);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#save()
	 */
	@Override
	public void save() throws IOException {

		FileUtils.saveIntList(directory + "/nodes", nodes, blockSize);
		FileUtils.saveIntList(directory + "/edges", edges, blockSize);
		FileUtils.saveStringList(directory + "/nodesLabels", nodesLabels, blockSize);
		FileUtils.saveStringList(directory + "/edgesLabels", edgesLabels, blockSize);
		FileUtils.saveShortList(directory + "/costs", costs, blockSize);
		FileUtils.saveIntList(directory + "/points", points, blockSize);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#load()
	 */
	@Override
	public void load() throws IOException {

		nodes = FileUtils.loadIntList(directory + "/nodes", blockSize);
		edges = FileUtils.loadIntList(directory + "/edges", blockSize);
		nodesLabels = FileUtils.loadStringList(directory + "/nodesLabels", blockSize);
		edgesLabels = FileUtils.loadStringList(directory + "/edgesLabels", blockSize);
		costs = FileUtils.loadShortList(directory + "/costs", blockSize);
		points = FileUtils.loadIntList(directory + "/points", blockSize);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#addNode(org.graphast.model.GraphastNode)
	 */
	@Override
	public void addNode(GraphastNode node) {

		long id;

		long labelIndex = storeNodeLabel(node.getLabel());
		node.setLabelIndex(labelIndex);

		synchronized (nodes) {

			id = nodes.size64() / GraphastNode.NODE_BLOCKSIZE;

			nodes.add(node.getExternalIdSegment());
			nodes.add(node.getExternalIdOffset());
			nodes.add(node.getCategory());
			nodes.add(node.getLatitude());
			nodes.add(node.getLongitude());
			nodes.add(node.getFirstEdgeSegment());
			nodes.add(node.getFirstEdgeOffset());
			nodes.add(node.getLabelIndexSegment());
			nodes.add(node.getLabelIndexOffset());
			nodes.add(node.getCostsIndexSegment());
			nodes.add(node.getCostsIndexOffset());

		}

		nodeIndex.put(BigArrays.index(node.getLatitude(), node.getLongitude()),
				(long) id);

		node.setId(id);

	}

	/**
	 * This method will store the passed label in a
	 * ObjectBigList of Strings and return the position
	 * of this insertion.
	 * 
	 * @param	label	String that will be added into
	 * 					the ObjectBigList.
	 * @return	the labelId (position where the label was inserted).
	 */
	private long storeNodeLabel(String label) {

		long labelId;

		synchronized (nodesLabels) {

			labelId = nodesLabels.size64();
			nodesLabels.add(label);

		}

		return labelId;

	}

	//TODO Why we only update the latitude, longitude and FirstEdge? 
	//Wouldn't be better if we had a method that updates everything?
	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateNodeInfo(org.graphast.model.GraphastNode)
	 */
	@Override
	public void updateNodeInfo(GraphastNode node) {

		long position = node.getId() * GraphastNode.NODE_BLOCKSIZE;
		position = position + 3;

		synchronized(nodes){

			nodes.set(position++, node.getLatitude());
			nodes.set(position++, node.getLongitude());
			nodes.set(position++, node.getFirstEdgeSegment());
			nodes.set(position++, node.getFirstEdgeOffset());

		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNode(long)
	 */
	@Override
	public GraphastNode getNode(long id) {

		long position = id * GraphastNode.NODE_BLOCKSIZE;
		GraphastNode node = new GraphastNode(
				BigArrays.index(nodes.getInt(position), nodes.getInt(position + 1)), // externalId
				nodes.getInt(position + 2), // category
				latLongToDouble(nodes.getInt(position + 3)), // latitude
				latLongToDouble(nodes.getInt(position + 4)), // longitude
				BigArrays.index(nodes.getInt(position + 5), nodes.getInt(position + 6)), // firstEdge
				BigArrays.index(nodes.getInt(position + 7), nodes.getInt(position + 8)), // labelIndex
				BigArrays.index(nodes.getInt(position + 9), nodes.getInt(position + 10)) // costIndex
				);

		node.setId(id);
		node.validate();

		return node;

	}

	//TODO Suggestion: delete this method and keep all these operations in  updateEdgeInfo
	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#setEdge(org.graphast.model.GraphastEdge, long)
	 */
	@Override
	public void setEdge(GraphastEdge edge, long pos) {

		synchronized (edges) {

			edges.set(pos++, edge.getExternalIdSegment());
			edges.set(pos++, edge.getExternalIdOffset());
			edges.set(pos++, edge.getFromNodeSegment());
			edges.set(pos++, edge.getFromNodeOffset());
			edges.set(pos++, edge.getToNodeSegment());
			edges.set(pos++, edge.getToNodeOffset());
			edges.set(pos++, edge.getFromNodeNextEdgeSegment());
			edges.set(pos++, edge.getFromNodeNextEdgeOffset());
			edges.set(pos++, edge.getToNodeNextEdgeSegment());
			edges.set(pos++, edge.getToNodeNextEdgeOffset());
			edges.set(pos++, edge.getDistance());
			edges.set(pos++, edge.getCostsSegment());
			edges.set(pos++, edge.getCostsOffset());
			edges.set(pos++, edge.getGeometrySegment());
			edges.set(pos++, edge.getGeometryOffset());
			edges.set(pos++, edge.getLabelIndexSegment());
			edges.set(pos++, edge.getLabelIndexOffset());

		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#addEdge(org.graphast.model.GraphastEdge)
	 */
	@Override
	public void addEdge(GraphastEdge edge) {

		long labelIndex = storeEdgeLabel(edge.getLabel());
		long costsIndex = storeCosts(edge.getCosts());
		long geometryIndex = storePoints(edge.getGeometry());

		edge.setCostsIndex(costsIndex);
		edge.setGeometryIndex(geometryIndex);
		edge.setLabelIndex(labelIndex);

		long id;

		synchronized (edges) {

			id = edges.size64() / GraphastEdge.EDGE_BLOCKSIZE;

			edges.add(edge.getExternalIdSegment());
			edges.add(edge.getExternalIdOffset());
			edges.add(edge.getFromNodeSegment());
			edges.add(edge.getFromNodeOffset());
			edges.add(edge.getToNodeSegment());
			edges.add(edge.getToNodeOffset());
			edges.add(edge.getFromNodeNextEdgeSegment());
			edges.add(edge.getFromNodeNextEdgeOffset());
			edges.add(edge.getToNodeNextEdgeSegment());
			edges.add(edge.getToNodeNextEdgeOffset());
			edges.add(edge.getDistance());
			edges.add(edge.getCostsSegment());
			edges.add(edge.getCostsOffset());
			edges.add(edge.getGeometrySegment());
			edges.add(edge.getGeometryOffset());
			edges.add(edge.getLabelIndexSegment());
			edges.add(edge.getLabelIndexOffset());	

		}

		edge.setId(id);

		updateNeighborhood(edge);

	}

	/**
	 * This method will store the passed label in a
	 * ObjectBigList of Strings and return the position
	 * of this insertion.
	 * 
	 * @param	label	String that will be added into
	 * 					the ObjectBigList.
	 * @return	the labelId (position where the label was inserted).
	 */
	private long storeEdgeLabel(String label) {

		long labelId;

		synchronized (edgesLabels) {

			labelId = edgesLabels.size64();
			edgesLabels.add(label);

		}

		return labelId;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#storeCosts(short[])
	 */
	@Override
	public long storeCosts(short[] c) {

		if (c == null || c.length == 0) {

			return -1l;

		}

		long costId;

		synchronized (costs) {

			costId = costs.size64();
			costs.add((short) c.length);

			for (int i = 0; i < c.length; i++) {

				costs.add(c[i]);

			}

		}

		return costId;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#storePoints(java.util.List)
	 */
	@Override
	public long storePoints(List<Point> listPoints) {

		if (listPoints == null || listPoints.size() == 0) {

			return -1l;

		}

		long listId;

		synchronized (points) {

			listId = points.size64();
			points.add((short) listPoints.size());

			for (Point p : listPoints) {

				points.add(latLongToInt(p.getLatitude()));
				points.add(latLongToInt(p.getLongitude()));

			}

		}

		return listId;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateEdgeInfo(org.graphast.model.GraphastEdge)
	 */
	@Override
	public void updateEdgeInfo(GraphastEdge edge) {

		long pos = edge.getId() * GraphastEdge.EDGE_BLOCKSIZE;
		setEdge(edge, pos);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateNeighborhood(org.graphast.model.GraphastEdge)
	 */
	@Override
	public void updateNeighborhood(GraphastEdge edge) {

		GraphastNode from = getNode(edge.getFromNode());
		from.validate();

		GraphastNode to = getNode(edge.getToNode());
		to.validate();

		long eid = edge.getId();

		updateNodeNeighborhood(from, eid);
		updateNodeNeighborhood(to, eid);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateNodeNeighborhood(org.graphast.model.GraphastNode, long)
	 */
	@Override
	public void updateNodeNeighborhood(GraphastNode node, long eid) {

		if (BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset()) == -1) {

			node.setFirstEdge(eid);
			updateNodeInfo(node);

		} else {

			long next = 0;
			GraphastEdge nextEdge = getEdge(BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset()));

			while (next != -1) {				

				if (node.getId() == nextEdge.getFromNode()) {
					next = nextEdge.getFromNodeNextEdge();
				} else if (node.getId() == nextEdge.getToNode()) {
					next = nextEdge.getToNodeNextEdge();
				}
				if (next != -1) {
					nextEdge = getEdge(next);
				}
			}

			if (node.getId() == nextEdge.getFromNode()) {
				nextEdge.setFromNodeNextEdge(eid);
			} else if (node.getId() == nextEdge.getToNode()) {
				nextEdge.setToNodeNextEdge(eid);
			}

			updateEdgeInfo(nextEdge);

		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getOutEdges(long)
	 */
	@Override
	public LongList getOutEdges(long vid) {

		LongList outEdges = new LongArrayList();
		GraphastNode v = getNode(vid);

		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset());
		GraphastEdge nextEdge = getEdge(firstEdgeId);
		long next = 0;

		while (next != -1) {

			if (vid == nextEdge.getFromNode()) {
				outEdges.add(nextEdge.getId());
				next = nextEdge.getFromNodeNextEdge();
			} else if (vid == nextEdge.getToNode()) {
				next = nextEdge.getToNodeNextEdge();
			}

			if (next != -1) {
				nextEdge = getEdge(next);
			}

		}

		return outEdges;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getCosts(it.unimi.dsi.fastutil.longs.LongList, int)
	 */
	@Override
	public short[] getCosts(LongList edges, int time) {

		short[] costs = new short[edges.size()];
		GraphastEdge e;
		for (int i = 0; i < edges.size(); i++) {
			e = getEdge(edges.get(i));
			costs[i] = getEdgeCost(e, time);
		}
		return costs;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getCosts()
	 */
	@Override
	public ShortBigArrayBigList getCosts() {

		return costs;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getOutNeighbors(long)
	 */
	@Override
	public LongList getOutNeighbors(long vid) {
		return getOutNeighbors(vid, 0, false);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getOutNeighborsAndCosts(long, int)
	 */
	@Override
	public LongList getOutNeighborsAndCosts(long vid, int time) {
		return getOutNeighbors(vid, time, true);
	}

	private LongList getOutNeighbors(long vid, int time, boolean getCosts) {
		LongList neighborsCosts = new LongArrayList();
		GraphastNode v = getNode(vid);
		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset());
		GraphastEdge nextEdge = getEdge(firstEdgeId);
		long next = 0;
		while (next != -1) {
			if (vid == nextEdge.getFromNode()) {
				neighborsCosts.add(nextEdge.getToNode());

				if (getCosts) {
					neighborsCosts.add(getEdgeCost(nextEdge, time));
				}
				next = nextEdge.getFromNodeNextEdge();
			} else if (vid == nextEdge.getToNode()) {
				next = nextEdge.getToNodeNextEdge();
			}
			if (next != -1) {
				nextEdge = getEdge(next);
			}
		}

		return neighborsCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdge(long)
	 */
	@Override
	public GraphastEdge getEdge(long id) {

		long pos = id * GraphastEdge.EDGE_BLOCKSIZE;

		long externalId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long fromId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long toId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long fromNodeNextEdge = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long toNodeNextEdge = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		int distance = edges.getInt(pos++);
		long costsIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long geometryIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long labelIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));

		GraphastEdge edge = new GraphastEdge(externalId, fromId, toId,
				fromNodeNextEdge, toNodeNextEdge, distance, costsIndex,
				geometryIndex, labelIndex, null);
		edge.setId(id);
		edge.validate();
		return edge;
		
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgeCosts(long)
	 */
	@Override
	public short[] getEdgeCosts(long id) {
		
		GraphastEdge edge = getEdge(id);
		long costsIndex = edge.getCostsIndex();
		short size = costs.getShort(costsIndex++);
		short[] c = new short[size];
		int i = 0;
		while (size > 0) {
			c[i++] = costs.getShort(costsIndex++);
			size--;
		}
		return c;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgeCost(org.graphast.model.GraphastEdge, int)
	 */
	@Override
	public short getEdgeCost(GraphastEdge edge, int time) {
		
		long costsIndex = edge.getCostsIndex();
		if (costsIndex < 0) {
			throw new GraphastException("Edge without costs: " + edge);
		}
		short size = costs.getShort(costsIndex++);
		int intervalSize = secondsDay / size;
		long index = (long) (costsIndex + (time / intervalSize));
		
		return costs.getShort(index);
	
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgePoints(long)
	 */
	@Override
	public List<Point> getEdgePoints(long id) {
		GraphastEdge edge = getEdge(id);
		long geometryIndex = edge.getGeometryIndex();
		int size = points.getInt(geometryIndex++);
		List<Point> listPoints = new ArrayList<Point>(size);
		while (size > 0) {
			listPoints.add(new Point(latLongToDouble(points
					.getInt(geometryIndex++)), latLongToDouble(points
							.getInt(geometryIndex++))));
			size--;
		}
		return listPoints;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNodeId(int, int)
	 */
	@Override
	public Long getNodeId(int latitude, int longitude) {

		Long result = nodeIndex.get(BigArrays.index(latitude, longitude));

		if (result != -1) {

			return result;

		}

		return null;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNode(double, double)
	 */
	@Override
	public Long getNode(double latitude, double longitude) {

		int lat, lon;

		lat = latLongToInt(latitude);
		lon = latLongToInt(longitude);

		return getNodeId(lat, lon);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNodeLabel(long)
	 */
	@Override
	public String getNodeLabel(long id) {
		return nodesLabels.get(id);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgeLabel(long)
	 */
	@Override
	public String getEdgeLabel(long id) {
		return edgesLabels.get(id);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNodes()
	 */
	@Override
	public IntBigArrayBigList getNodes() {
		return nodes;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdges()
	 */
	@Override
	public IntBigArrayBigList getEdges() {
		return edges;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getLabels()
	 */
	@Override
	public ObjectBigList<String> getLabels() {
		return edgesLabels;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#setLabels(it.unimi.dsi.fastutil.objects.ObjectBigList)
	 */
	@Override
	public void setLabels(ObjectBigList<String> labels) {
		this.edgesLabels = labels;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#logNodes()
	 */
	@Override
	public void logNodes() {
		for (int i = 0; i < nodes.size64() / GraphastNode.NODE_BLOCKSIZE; i++) {
			logger.info(getNode(i).toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#logEdges()
	 */
	@Override
	public void logEdges() {

		for (long i = 0; i < (edges.size64() / GraphastEdge.EDGE_BLOCKSIZE); i++) {
			logger.info(getEdge(i).toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNumberOfNodes()
	 */
	@Override
	public int getNumberOfNodes(){
		
		return (int) getNodes().size64()/GraphastNode.NODE_BLOCKSIZE;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNumberOfEdges()
	 */
	@Override
	public int getNumberOfEdges(){
		
		return (int) getEdges().size64()/GraphastEdge.EDGE_BLOCKSIZE;

	}

	public Long2IntMap accessNeighborhood(GraphastNode v){
		Long2IntMap neig = new Long2IntOpenHashMap();
		for (Long e : this.getOutEdges( v.getId())) {
			GraphastEdge edge = this.getEdge(e);
			long vNeig =  edge.getFromNode();
			int cost =  edge.getDistance();
			if(!neig.containsKey(vNeig)){
				neig.put(vNeig, cost);
			}else{
				if(neig.get(vNeig) > cost){
					neig.put(vNeig, cost);
				}
			}
		}
		
		return neig;
	
	}
	
	
}
