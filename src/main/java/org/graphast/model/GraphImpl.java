package org.graphast.model;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.enums.CompressionType;
import org.graphast.exception.GraphastException;
import org.graphast.geometry.Point;
import org.graphast.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphImpl implements Graph {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Long2LongMap nodeIndex = new Long2LongOpenHashMap();

	protected String directory;

	private IntBigArrayBigList nodes;

	private IntBigArrayBigList edges;

	private ObjectBigList<String> nodesLabels;

	private ObjectBigList<String> edgesLabels;

	private IntBigArrayBigList edgesCosts;

	private IntBigArrayBigList nodesCosts;

	private IntBigArrayBigList points;

	protected int blockSize = 4096;

	private static int secondsDay = 86400;

	private int[] intCosts;
	
	protected CompressionType compressionType;

	/**
	 * Creates a Graph for the given directory passed as parameter.
	 * 
	 * This constructor will instantiate all lists needed 
	 * to properly handle the information of a Graph, 
	 * e.g. nodes, edges, labels, etc.
	 * 
	 * @param directory Directory in which the graph is (or will be) persisted.
	 */
	public GraphImpl(String directory) {
		this(directory, CompressionType.GZIP_COMPRESSION);
	}
	
	public GraphImpl(String directory, CompressionType compressionType) {
		this.directory = directory;
		this.compressionType = compressionType;

		nodes = new IntBigArrayBigList();
		edges = new IntBigArrayBigList();
		nodesLabels = new ObjectBigArrayBigList<String>();
		edgesLabels = new ObjectBigArrayBigList<String>();
		nodesCosts = new IntBigArrayBigList();
		edgesCosts = new IntBigArrayBigList();
		points = new IntBigArrayBigList();

		nodeIndex.defaultReturnValue(-1);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#save()
	 */
	@Override
	public void save() throws IOException {
		FileUtils.saveIntList(directory + "/nodes", nodes, blockSize, compressionType);
		FileUtils.saveIntList(directory + "/edges", edges, blockSize, compressionType);
		FileUtils.saveStringList(directory + "/nodesLabels", nodesLabels, blockSize, compressionType);
		FileUtils.saveStringList(directory + "/edgesLabels", edgesLabels, blockSize, compressionType);
		FileUtils.saveIntList(directory + "/nodesCosts", nodesCosts, blockSize, compressionType);
		FileUtils.saveIntList(directory + "/edgesCosts", edgesCosts, blockSize, compressionType);
		FileUtils.saveIntList(directory + "/points", points, blockSize, compressionType);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#load()
	 */
	@Override
	public void load() throws IOException {
		nodes = FileUtils.loadIntList(directory + "/nodes", blockSize, compressionType);
		edges = FileUtils.loadIntList(directory + "/edges", blockSize, compressionType);
		nodesLabels = FileUtils.loadStringList(directory + "/nodesLabels", blockSize, compressionType);
		edgesLabels = FileUtils.loadStringList(directory + "/edgesLabels", blockSize, compressionType);
		nodesCosts = FileUtils.loadIntList(directory + "/nodesCosts", blockSize, compressionType);
		edgesCosts = FileUtils.loadIntList(directory + "/edgesCosts", blockSize, compressionType);
		points = FileUtils.loadIntList(directory + "/points", blockSize, compressionType);
		createNodeIndex();
	}

	private void createNodeIndex() {
		int numberOfNodes = getNumberOfNodes();
		NodeImpl node;
		for (int i=0; i < numberOfNodes; i++) {
			node = (NodeImpl)getNode(i);
			nodeIndex.put(BigArrays.index(node.getLatitudeConvertedToInt(), node.getLongitudeConvertedToInt()), (long) i);
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#addNode(org.graphast.model.GraphastNode)
	 */
	@Override
	public void addNode(Node n) {

		long id;

		NodeImpl node = (NodeImpl)n;

		long labelIndex = storeLabel(node.getLabel(), nodesLabels);
		node.setLabelIndex(labelIndex);
		long costsIndex = storeCosts(node.getCosts(), nodesCosts);
		node.setCostsIndex(costsIndex);
		node.setLabelIndex(labelIndex);

		synchronized (nodes) {
			id = nodes.size64() / Node.NODE_BLOCKSIZE;

			nodes.add(node.getExternalIdSegment());
			nodes.add(node.getExternalIdOffset());
			nodes.add(node.getCategory());
			nodes.add(node.getLatitudeConvertedToInt());
			nodes.add(node.getLongitudeConvertedToInt());
			nodes.add(node.getFirstEdgeSegment());
			nodes.add(node.getFirstEdgeOffset());
			nodes.add(node.getLabelIndexSegment());
			nodes.add(node.getLabelIndexOffset());
			nodes.add(node.getCostsIndexSegment());
			nodes.add(node.getCostsIndexOffset());
		}
		nodeIndex.put(BigArrays.index(node.getLatitudeConvertedToInt(), node.getLongitudeConvertedToInt()), (long) id);
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
	private long storeLabel(String label, ObjectBigList<String> labelList) {
		// Do not store a null label
		if (label == null) {
			return -1;
		}

		long labelId;

		synchronized (labelList) {
			labelId = labelList.size64();
			labelList.add(label);
		}
		return labelId;
	}

	//TODO Why we only update the latitude, longitude and FirstEdge? 
	//Wouldn't be better if we had a method that updates everything?
	/**
	 * This method will update the IntBigArrayBigList of nodes
	 * with need information of a passed GraphastNode.
	 * 
	 * @param n GraphastNode with the informations that must be updated.
	 */
	public void updateNodeInfo(Node n) {

		NodeImpl node = (NodeImpl)n;

		long position = node.getId() * Node.NODE_BLOCKSIZE;
		position = position + 3;

		synchronized(nodes){
			nodes.set(position++, node.getLatitudeConvertedToInt());
			nodes.set(position++, node.getLongitudeConvertedToInt());
			nodes.set(position++, node.getFirstEdgeSegment());
			nodes.set(position++, node.getFirstEdgeOffset());
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNode(long)
	 */
	@Override
	public Node getNode(long id) {

		long position = id * Node.NODE_BLOCKSIZE;
		NodeImpl node = new NodeImpl(
				BigArrays.index(nodes.getInt(position), nodes.getInt(position + 1)), // externalId
				nodes.getInt(position + 2), // category
				latLongToDouble(nodes.getInt(position + 3)), // latitude
				latLongToDouble(nodes.getInt(position + 4)), // longitude
				BigArrays.index(nodes.getInt(position + 5), nodes.getInt(position + 6)), // firstEdge
				BigArrays.index(nodes.getInt(position + 7), nodes.getInt(position + 8)), // labelIndex
				BigArrays.index(nodes.getInt(position + 9), nodes.getInt(position + 10)) // costIndex
				);

		node.setId(id);
		long labelIndex = node.getLabelIndex();
		if (labelIndex >= 0) {
			node.setLabel(getNodesLabels().get(labelIndex));
		}
		node.validate();

		return node;
	}

	//TODO Suggestion: delete this method and keep all these operations in  updateEdgeInfo
	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#setEdge(org.graphast.model.GraphastEdge, long)
	 */
	@Override
	public void setEdge(Edge e, long pos) {

		EdgeImpl edge = (EdgeImpl)e;

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
	public void addEdge(Edge e) {

		EdgeImpl edge = (EdgeImpl)e;
		long labelIndex = storeLabel(edge.getLabel(), edgesLabels);
		long costsIndex = storeCosts(edge.getCosts(), edgesCosts);
		long geometryIndex = storePoints(edge.getGeometry());
		edge.setCostsIndex(costsIndex);
		edge.setGeometryIndex(geometryIndex);
		edge.setLabelIndex(labelIndex);

		long id;

		synchronized (edges) {
			id = edges.size64() / Edge.EDGE_BLOCKSIZE;

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
	 * This method will store the passed list of costs in a
	 * ShortBigArrayBigList and return the position
	 * of this insertion.
	 * 
	 * @param	c	list of costs that will be stored
	 * @return	the costId (position where the cost was inserted).
	 */
	private long storeCosts(int[] c, IntBigArrayBigList costs) {
		if (c == null || c.length == 0) {
			return -1l;
		}

		long costId;

		synchronized (costs) {
			costId = costs.size64();
			costs.add(c.length);

			for (int i = 0; i < c.length; i++) {
				costs.add(c[i]);
			}
		}
		return costId;
	}

	/**
	 * This method will store the passed list of points
	 * in a IntBigArrayBigList and return the position
	 * of this insertion.
	 * 
	 * @param	listPoints	list of points that will be stored
	 * @return	the listId (position where the list was inserted).
	 */
	private long storePoints(List<Point> listPoints) {
		if (listPoints == null || listPoints.size() == 0) {
			return -1l;
		}

		long listId;

		synchronized (points) {
			listId = points.size64();
			points.add(listPoints.size());

			for (Point p : listPoints) {
				points.add(latLongToInt(p.getLatitude()));
				points.add(latLongToInt(p.getLongitude()));
			}
		}
		return listId;
	}

	/**
	 * This method will update the IntBigArrayBigList of edges
	 * with need information of a passed Edge.
	 * 
	 * @param edge Edge with the informations that must be updated.
	 */
	private void updateEdgeInfo(Edge edge) {

		long pos = edge.getId() * Edge.EDGE_BLOCKSIZE;
		setEdge(edge, pos);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateNeighborhood(org.graphast.model.GraphastEdge)
	 */
	@Override
	public void updateNeighborhood(Edge edge) {

		Node from = getNode(edge.getFromNode());
		from.validate();

		Node to = getNode(edge.getToNode());
		to.validate();

		long eid = edge.getId();

		updateNodeNeighborhood(from, eid);
		updateNodeNeighborhood(to, eid);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#updateNodeNeighborhood(org.graphast.model.GraphastNode, long)
	 */
	@Override
	public void updateNodeNeighborhood(Node n, long eid) {

		NodeImpl node = (NodeImpl)n;

		if (BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset()) == -1) {

			node.setFirstEdge(eid);
			updateNodeInfo(node);

		} else {


			long next = 0;
			EdgeImpl nextEdge = (EdgeImpl) getEdge(BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset()));

			while (next != -1) {				

				if (node.getId() == nextEdge.getFromNode()) {
					next = nextEdge.getFromNodeNextEdge();
				} else if (node.getId() == nextEdge.getToNode()) {
					next = nextEdge.getToNodeNextEdge();
				}
				if (next != -1) {
					nextEdge = (EdgeImpl)getEdge(next);
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
	public LongList getOutEdges(long nodeId) {

		LongList outEdges = new LongArrayList();
		NodeImpl v = (NodeImpl)getNode(nodeId);

		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset());
		Edge nextEdge = getEdge(firstEdgeId);
		long next = 0;

		while (next != -1) {

			if (nodeId == nextEdge.getFromNode()) {
				outEdges.add(nextEdge.getId());
				next = nextEdge.getFromNodeNextEdge();
			} else if (nodeId == nextEdge.getToNode()) {
				next = nextEdge.getToNodeNextEdge();
			}

			if (next != -1) {
				nextEdge = getEdge(next);
			}

		}

		return outEdges;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgesCosts(it.unimi.dsi.fastutil.longs.LongList, int)
	 */
	@Override
	public int[] getEdgesCosts(LongList edges, int time) {

		int[] costs = new int[edges.size()];
		Edge e;
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
	public IntBigArrayBigList getCosts() {

		return edgesCosts;
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
		NodeImpl v = (NodeImpl)getNode(vid);
		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset());
		Edge nextEdge = getEdge(firstEdgeId);
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
	public Edge getEdge(long id) {



		long pos = id * Edge.EDGE_BLOCKSIZE;

		long externalId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long fromId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long toId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long fromNodeNextEdge = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long toNodeNextEdge = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		int distance = edges.getInt(pos++);
		long costsIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long geometryIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long labelIndex = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));

		EdgeImpl edge = new EdgeImpl(externalId, fromId, toId,
				fromNodeNextEdge, toNodeNextEdge, distance, costsIndex,
				geometryIndex, labelIndex, null);

		edge.setId(id);
		if (labelIndex >= 0) {
			edge.setLabel(getEdgesLabels().get(labelIndex));
		}

		if (costsIndex >= 0) {
			edge.setCosts(getEdgeCostsByCostsIndex(costsIndex));
		}



		edge.validate();
		return edge;

	}

	@Override
	public int[] getEdgeCosts(long edgeId) {

		EdgeImpl edge = (EdgeImpl)getEdge(edgeId);
		long costsIndex = edge.getCostsIndex();

		if(costsIndex == -1 ) {
			return null;
		} else {
			return getEdgeCostsByCostsIndex(costsIndex);
		}
	}

	int[] getEdgeCostsByCostsIndex(long costsIndex) {

		int size = edgesCosts.getInt(costsIndex);
		int[] c = new int[size];
		int i = 0;
		while (size > 0) {
			costsIndex++;
			c[i] = edgesCosts.getInt(costsIndex);
			size--;
			i++;
		}
		return c;
	}


	public int[] getNodeCosts(long nodeId) {

		NodeImpl node = (NodeImpl)getNode(nodeId);
		long costsIndex = node.getCostsIndex();

		if(costsIndex == -1 ) {
			return null;
		} else {
			return getNodeCostsByCostsIndex(costsIndex);
		}
		
	}

	public int[] getNodeCostsByCostsIndex(long costsIndex) {

		int size = nodesCosts.getInt(costsIndex);
		int[] c = new int[size];
		int i = 0;
		while (size > 0) {
			costsIndex++;
			c[i] = nodesCosts.getInt(costsIndex);
			size--;
			i++;
		}
		return c;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgeCost(org.graphast.model.GraphastEdge, int)
	 */
	@Override
	public int getEdgeCost(Edge e, int time) {
		EdgeImpl edge = (EdgeImpl)e;
		long costsIndex = edge.getCostsIndex();
		if (costsIndex < 0) {
			throw new GraphastException("Edge without costs: " + edge);
		}
		int size = edgesCosts.getInt(costsIndex++);
		int intervalSize = secondsDay / size;
		long index = (long) (costsIndex + (time / intervalSize));

		return edgesCosts.getInt(index);

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgePoints(long)
	 */
	@Override
	public List<Point> getEdgePoints(long id) {
		EdgeImpl edge = (EdgeImpl)getEdge(id);
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
	public Long getNodeId(double latitude, double longitude) {

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
		long position =  id*Node.NODE_BLOCKSIZE;
		long labelIndex = BigArrays.index(nodes.getInt(position + 7), nodes.getInt(position + 8));
		return nodesLabels.get(labelIndex);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getEdgeLabel(long)
	 */
	@Override
	public String getEdgeLabel(long id) {
		return edgesLabels.size64() > 0 ? edgesLabels.get(id) : null;
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

	ObjectBigList<String> getNodesLabels() {
		return nodesLabels;
	}

	void setNodesLabels(ObjectBigList<String> labels) {
		this.nodesLabels = labels;
	}

	ObjectBigList<String> getEdgesLabels() {
		return edgesLabels;
	}

	void setEdgesLabels(ObjectBigList<String> labels) {
		this.edgesLabels = labels;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#logNodes()
	 */
	@Override
	public void logNodes() {
		for (int i = 0; i < nodes.size64() / Node.NODE_BLOCKSIZE; i++) {
			logger.info(getNode(i).toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#logEdges()
	 */
	@Override
	public void logEdges() {

		for (long i = 0; i < (edges.size64() / Edge.EDGE_BLOCKSIZE); i++) {
			logger.info(getEdge(i).toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNumberOfNodes()
	 */
	@Override
	public int getNumberOfNodes(){
		return (int) getNodes().size64()/Node.NODE_BLOCKSIZE;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graphast#getNumberOfEdges()
	 */
	@Override
	public int getNumberOfEdges(){
		return (int) getEdges().size64()/Edge.EDGE_BLOCKSIZE;
	}

	public Long2IntMap accessNeighborhood(Node v){
		Long2IntMap neig = new Long2IntOpenHashMap();
		for (Long e : this.getOutEdges( v.getId())) {
			Edge edge = this.getEdge(e);
			long vNeig =  edge.getToNode();
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

	public boolean hasNode(long id) {
		try {
			long position = id * Node.NODE_BLOCKSIZE;
			if(nodes.contains(position)) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(NullPointerException e) {
			return false;
		}
	}

	public boolean hasNode(Node n) {

		NodeImpl node = (NodeImpl)n;

		try {
			if(nodeIndex.containsKey(BigArrays.index(node.getLatitudeConvertedToInt(), node.getLongitudeConvertedToInt()))) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(NullPointerException e) {
			return false;
		}
	}

	@Override
	public boolean hasNode(double latitude, double longitude) {
		int lat = latLongToInt(latitude);
		int lon = latLongToInt(longitude);
		try {
			if(nodeIndex.containsKey(BigArrays.index(lat, lon))) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(NullPointerException e) {
			return false;
		}
	}
	@Override
	public Node addPoi(long id, double lat, double lon, int category,
			LinearFunction[] costs) {
		int[] intCosts = linearFunctionArrayToCostIntArray(costs);
		Node poi = new NodeImpl(id, category, lat, lon, 0l, 0l, 0l, intCosts);
		this.addNode(poi);
		return poi;
	}

	public int poiGetCost(long vid, int time){
		int i = 0;
		LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
		while(lf[i].getEndInterval() <= time){
			i++;
		}
		return lf[i].calculateCost(time);
	}

	public int poiGetCost(long vid){
		LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
		return lf[0].calculateCost(0);
	}

	public int[] getPoiCost(long vid){
		return getNodeCosts(vid);
	}

	public LinearFunction[] convertToLinearFunction(int[] costs){
		LinearFunction[] result = new LinearFunction[costs.length];
		int interval = (60*60*24)/costs.length;
		int startInterval = 0;
		int endInterval = interval;
		for(int i = 0; i < costs.length; i++){
			result[i] = new LinearFunction(startInterval, costs[i], endInterval, costs[i]);
			startInterval = endInterval;
			endInterval = endInterval + interval;
		}
		return result;
	}

	private int[] linearFunctionArrayToCostIntArray(LinearFunction[] linearFunction) {
		intCosts = null;
		for(int i = 0; i < linearFunction.length; i++) {
			intCosts[i] = linearFunction[i].getStartCost(); 
		}
		return intCosts;

	}

	public int getMaximunCostValue(int[] costs) {

		if(costs==null) {
			throw new IllegalArgumentException("Costs can not be null.");
		}


		int max = costs[0];

		for (int i = 0; i < costs.length; i++) {

			if (costs[i] > max) {
				max = costs[i];
			}
		}

		return max;
	}

	public int getMinimunCostValue(int[] costs) {

		int min = costs[0];

		for (int i = 0; i < costs.length; i++) {

			if (costs[i] < min) {
				min = costs[i];
			}
		}

		return min;
	}

	public boolean isPoi(long vid){
		return getNode(vid).getCategory() >= 0;
	}

	public Node getPoi(long vid){
		Node v = getNode(vid);
		if(v.getCategory() < 0)	return null;
		else	return v;
	}
	
	//TODO Verify if this access is correct
	@Override
	public IntSet getCategories(){
		IntSet categories = new IntOpenHashSet();
		
		for(int i = 0; i < getNumberOfNodes(); i++) {
			long position = i*Node.NODE_BLOCKSIZE;
			int category = getNodes().getInt(position+2);
			categories.add(category);
//			long position = i*Node.NODE_BLOCKSIZE;
//			long vid = ga.getNodes().getInt(position);
//			bounds.put(vid,  d.shortestPathPoi(vid, -1).getDistance());
		}	
		
		return categories;
	}

	@Override
	public CompressionType getCompressionType() {
		return compressionType;
	}

	@Override
	public void setCompressionType(CompressionType compressionType) {
		this.compressionType = compressionType;
	}
	
	

}
