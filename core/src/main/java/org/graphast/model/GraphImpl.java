package org.graphast.model;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.graphast.enums.CompressionType;
import org.graphast.enums.TimeType;
import org.graphast.geometry.BBox;
import org.graphast.geometry.PoI;
import org.graphast.geometry.PoICategory;
import org.graphast.geometry.Point;
import org.graphast.util.DistanceUtils;
import org.graphast.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

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

import java.io.Serializable;

public class GraphImpl implements Graph, GraphBounds, Serializable {

	/*
	 * Class that implements the two interfaces that build a graph
	 */
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected Long2LongMap nodeIndex = new Long2LongOpenHashMap();

	protected String directory;

	protected String absoluteDirectory;

	protected IntBigArrayBigList nodes;

	protected IntBigArrayBigList edges;

	protected ObjectBigList<String> nodesLabels;

	protected ObjectBigList<String> edgesLabels;

	protected IntBigArrayBigList edgesCosts;

	protected IntBigArrayBigList nodesCosts;

	protected IntBigArrayBigList points;

	protected int blockSize = 4096;

	protected int[] intCosts;

	protected CompressionType compressionType;

	protected TimeType timeType;

	protected int maxTime = 86400000;

	protected BBox bBox;
	
	/*
	 * Attributes that came from GraphBoundsImpl
	 */
	
	private static final long serialVersionUID = -6041223700543613773L;
	private Long2IntMap edgesUpperBound, edgesLowerBound;
	private Long2IntMap nodesUpperBound, nodesLowerBound;
	protected GraphBounds reverseGraph;

	/**
	 * Creates a Graph for the given directory passed as parameter.
	 * 
	 * This constructor will instantiate all lists needed to properly handle the
	 * information of a Graph, e.g. nodes, edges, labels, etc.
	 * 
	 * @param directory
	 *            Directory in which the graph is (or will be) persisted.
	 */
	public GraphImpl(String directory) {
		this(directory, CompressionType.GZIP_COMPRESSION, TimeType.MILLISECOND);
	}
	
	public GraphImpl() {
		
	}

	public GraphImpl(String directory, CompressionType compressionType, TimeType timeType) {
		setDirectory(directory);
		this.compressionType = compressionType;
		setTimeType(timeType);

		nodes = new IntBigArrayBigList();
		edges = new IntBigArrayBigList();
		nodesLabels = new ObjectBigArrayBigList<String>();
		edgesLabels = new ObjectBigArrayBigList<String>();
		nodesCosts = new IntBigArrayBigList();
		edgesCosts = new IntBigArrayBigList();
		points = new IntBigArrayBigList();

		nodeIndex.defaultReturnValue(-1);
		
		edgesUpperBound = new Long2IntOpenHashMap();
		edgesLowerBound = new Long2IntOpenHashMap();
		nodesUpperBound = new Long2IntOpenHashMap();
		nodesLowerBound = new Long2IntOpenHashMap();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#save()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#save()
	 */
	@Override
	public void save() {
		FileUtils.saveIntList(absoluteDirectory + "/nodes", nodes, blockSize,
				compressionType);
		FileUtils.saveIntList(absoluteDirectory + "/edges", edges, blockSize,
				compressionType);
		FileUtils.saveStringList(absoluteDirectory + "/nodesLabels", nodesLabels,
				blockSize, compressionType);
		FileUtils.saveStringList(absoluteDirectory + "/edgesLabels", edgesLabels,
				blockSize, compressionType);
		FileUtils.saveIntList(absoluteDirectory + "/nodesCosts", nodesCosts, blockSize,
				compressionType);
		FileUtils.saveIntList(absoluteDirectory + "/edgesCosts", edgesCosts, blockSize,
				compressionType);
		FileUtils.saveIntList(absoluteDirectory + "/points", points, blockSize,
				compressionType);
		FileUtils.saveLong2IntMap(absoluteDirectory + "/edgesUpperBound", edgesUpperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(absoluteDirectory + "/edgesLowerBound", edgesLowerBound, blockSize, compressionType);

		FileUtils.saveLong2IntMap(absoluteDirectory + "/nodesUpperBound", nodesUpperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(absoluteDirectory + "/nodesLowerBound", nodesLowerBound, blockSize, compressionType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#load()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#load()
	 */
	@Override
	public void load() {
		nodes = FileUtils.loadIntList(absoluteDirectory + "/nodes", blockSize,
				compressionType);
		edges = FileUtils.loadIntList(absoluteDirectory + "/edges", blockSize,
				compressionType);
		nodesLabels = FileUtils.loadStringList(absoluteDirectory + "/nodesLabels",
				blockSize, compressionType);
		edgesLabels = FileUtils.loadStringList(absoluteDirectory + "/edgesLabels",
				blockSize, compressionType);
		nodesCosts = FileUtils.loadIntList(absoluteDirectory + "/nodesCosts",
				blockSize, compressionType);
		edgesCosts = FileUtils.loadIntList(absoluteDirectory + "/edgesCosts",
				blockSize, compressionType);
		points = FileUtils.loadIntList(absoluteDirectory + "/points", 
				blockSize, compressionType);
		
		FileUtils.loadLong2IntMap(absoluteDirectory + "/edgesUpperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(absoluteDirectory + "/edgesLowerBound", blockSize, compressionType);

		FileUtils.loadLong2IntMap(absoluteDirectory + "/nodesUpperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(absoluteDirectory + "/nodesLowerBound", blockSize, compressionType);
		
		createNodeIndex();
		findBBox();
		log.info("nodes: {}", this.getNumberOfNodes());
		log.info("edges: {}", this.getNumberOfEdges());
	}

	private void createNodeIndex() {
		long numberOfNodes = getNumberOfNodes();
		NodeImpl node;
		for (int i = 0; i < numberOfNodes; i++) {
			node = (NodeImpl) getNode(i);
			nodeIndex.put(
					BigArrays.index(node.getLatitudeConvertedToInt(),
							node.getLongitudeConvertedToInt()), (long) i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#addNode(org.graphast.model.GraphastNode)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#addNode(org.graphast.model.Node)
	 */
	@Override
	public void addNode(Node n) {

		long id;

		NodeImpl node = (NodeImpl) n;

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
		nodeIndex.put(
				BigArrays.index(node.getLatitudeConvertedToInt(),
						node.getLongitudeConvertedToInt()), (long) id);
		node.setId(id);
	}



	// TODO Why we only update the latitude, longitude and FirstEdge?
	// Wouldn't be better if we had a method that updates everything?
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#updateNodeInfo(org.graphast.model.Node)
	 */
	@Override
	public void updateNodeInfo(Node n) {

		NodeImpl node = (NodeImpl) n;

		long labelIndex = storeLabel(node.getLabel(), nodesLabels);
		node.setLabelIndex(labelIndex);
		long costsIndex = storeCosts(node.getCosts(), nodesCosts);
		node.setCostsIndex(costsIndex);

		long position = node.getId() * Node.NODE_BLOCKSIZE;
		position = position + 2;

		synchronized (nodes) {
			nodes.set(position++, node.getCategory());
			nodes.set(position++, node.getLatitudeConvertedToInt());
			nodes.set(position++, node.getLongitudeConvertedToInt());
			nodes.set(position++, node.getFirstEdgeSegment());
			nodes.set(position++, node.getFirstEdgeOffset());
			nodes.set(position++, node.getLabelIndexSegment());
			nodes.set(position++, node.getLabelIndexOffset());
			nodes.set(position++, node.getCostsIndexSegment());
			nodes.set(position++, node.getCostsIndexOffset());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNode(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNode(long)
	 */
	@Override
	public Node getNode(long id) {

		long position = id * Node.NODE_BLOCKSIZE;
		Node node = new NodeImpl(BigArrays.index(nodes.getInt(position),
				nodes.getInt(position + 1)), // externalId
				nodes.getInt(position + 2), // category
				latLongToDouble(nodes.getInt(position + 3)), // latitude
				latLongToDouble(nodes.getInt(position + 4)), // longitude
				BigArrays.index(nodes.getInt(position + 5),
						nodes.getInt(position + 6)), // firstEdge
				BigArrays.index(nodes.getInt(position + 7),
						nodes.getInt(position + 8)), // labelIndex
				BigArrays.index(nodes.getInt(position + 9),
						nodes.getInt(position + 10)) // costIndex
				);

		node.setId(id);
		long labelIndex = node.getLabelIndex();
		if (labelIndex >= 0) {
			node.setLabel(getNodesLabels().get(labelIndex));
		}

		long costsIndex = node.getCostsIndex();
		if (costsIndex >= 0) {
			node.setCosts(getNodeCostsByCostsIndex(costsIndex));
		}

		node.validate();

		return node;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdge(long, long)
	 */
	@Override
	public Edge getEdge(long originNodeId, long destinationNodeId) {

		List<Edge> listOfPossibleEdges = new ArrayList<Edge>();

		for(Long edgeId : this.getOutEdges(originNodeId)) {
			Edge candidateEdge = this.getEdge(edgeId);

			if(candidateEdge.getToNode()==destinationNodeId) {
				listOfPossibleEdges.add(candidateEdge);
			}
		}

		Edge resultEdge = null;

		if(listOfPossibleEdges.size() != 0) {

			resultEdge = listOfPossibleEdges.get(0); 

		} else {
			return null;
		}

		for(Edge possibleResult : listOfPossibleEdges) {

			if(resultEdge.getDistance()>possibleResult.getDistance()) {
				resultEdge = possibleResult;
			}

		}

		return resultEdge;
	}


	// TODO Suggestion: delete this method and keep all these operations in
	// updateEdgeInfo
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#setEdge(org.graphast.model.GraphastEdge,
	 * long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdge(org.graphast.model.Edge, long)
	 */
	@Override
	public void setEdge(Edge e, long pos) {

		EdgeImpl edge = (EdgeImpl) e;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#addEdge(org.graphast.model.GraphastEdge)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#addEdge(org.graphast.model.Edge)
	 */
	@Override
	public void addEdge(Edge e) {
		// fromNode can be equal to toNode in an edge 
		// Previously this caused infinity loops in updateNeighborhood method.
		EdgeImpl edge = (EdgeImpl) e;
		long labelIndex = storeLabel(edge.getLabel(), edgesLabels);
		long costsIndex = storeCosts(edge.getCosts(), edgesCosts);
		long geometryIndex = storePoints(edge.getGeometry(), points);
		edge.setLabelIndex(labelIndex);
		edge.setCostsIndex(costsIndex);
		edge.setGeometryIndex(geometryIndex);

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
	 * This method will store the passed list of costs in a ShortBigArrayBigList
	 * and return the position of this insertion.
	 * 
	 * @param c
	 *            list of costs that will be stored
	 * @return the costId (position where the cost was inserted).
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
	 * This method will store the passed label in a ObjectBigList of Strings and
	 * return the position of this insertion.
	 * 
	 * @param label
	 *            String that will be added into the ObjectBigList.
	 * @return the labelId (position where the label was inserted).
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

	/**
	 * This method will store the passed list of points in a IntBigArrayBigList
	 * and return the position of this insertion.
	 * 
	 * @param listPoints
	 *            list of points that will be stored
	 * @return the listId (position where the list was inserted).
	 */
	private long storePoints(List<Point> listPoints, IntBigArrayBigList points) {
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
	 * This method will update the IntBigArrayBigList of edges with need
	 * information of a passed Edge.
	 * 
	 * @param edge
	 *            Edge with the informations that must be updated.
	 */
	private void updateEdgeInfo(Edge edge) {

		long pos = edge.getId() * Edge.EDGE_BLOCKSIZE;
		setEdge(edge, pos);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#updateNeighborhood(org.graphast.model.
	 * GraphastEdge)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#updateNeighborhood(org.graphast.model.Edge)
	 */
	@Override
	public void updateNeighborhood(Edge edge) {

		Node from = getNode(edge.getFromNode());
		from.validate();

		Node to = getNode(edge.getToNode());
		to.validate();

		long eid = edge.getId();

		updateNodeNeighborhood(from, eid);
		if (! from.getId().equals(to.getId())) {   // Avoid a problem (infinite loop) with double update if from == to
			updateNodeNeighborhood(to, eid);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphast.model.Graphast#updateNodeNeighborhood(org.graphast.model
	 * .GraphastNode, long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#updateNodeNeighborhood(org.graphast.model.Node, long)
	 */
	@Override
	public void updateNodeNeighborhood(Node n, long eid) {

		NodeImpl node = (NodeImpl) n;
		long firstEdge = BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset());
		
		if (firstEdge == -1) {   // First edge related to node n is still undefined
			node.setFirstEdge(eid);
			updateNodeInfo(node);
			
		} else {                // First edge related to node n has already been defined

			long nextEdgeId = BigArrays.index(node.getFirstEdgeSegment(), node.getFirstEdgeOffset());
			EdgeImpl nextEdge = (EdgeImpl) getEdge(nextEdgeId);

			while (nextEdgeId != -1) {

				if (node.getId() == nextEdge.getFromNode()) {
					nextEdgeId = nextEdge.getFromNodeNextEdge();
				} else if (node.getId() == nextEdge.getToNode()) {
					nextEdgeId = nextEdge.getToNodeNextEdge();
				}
				if (nextEdgeId != -1) {
					nextEdge = (EdgeImpl) getEdge(nextEdgeId);
				}
			}

			if (node.getId() == nextEdge.getFromNode()) {
				nextEdge.setFromNodeNextEdge(eid);
			} 
			if (node.getId() == nextEdge.getToNode()) {
				nextEdge.setToNodeNextEdge(eid);
			}

			updateEdgeInfo(nextEdge);
			//this.printInternalEdgeRepresentation();  // Used only to debug the internal graph representation
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutEdges(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getOutEdges(long)
	 */
	@Override
	public LongList getOutEdges(long nodeId) {

		LongList outEdges = new LongArrayList();
		NodeImpl v = (NodeImpl) getNode(nodeId);

		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(),
				v.getFirstEdgeOffset());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutEdges(long)
	 */
	@Override
	public LongList getInEdges(long nodeId) {

		LongList inEdges = new LongArrayList();
		NodeImpl v = (NodeImpl) getNode(nodeId);

		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(),
				v.getFirstEdgeOffset());
		Edge nextEdge = getEdge(firstEdgeId);
		long next = 0;

		while (next != -1) {

			if (nodeId == nextEdge.getToNode()) {
				inEdges.add(nextEdge.getId());
				next = nextEdge.getToNodeNextEdge();
			} else if (nodeId == nextEdge.getFromNode()) {
				next = nextEdge.getFromNodeNextEdge();
			}

			if (next != -1) {
				nextEdge = getEdge(next);
			}
		}
		return inEdges;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphast.model.Graphast#getEdgesCosts(it.unimi.dsi.fastutil.longs
	 * .LongList, int)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgesCosts(it.unimi.dsi.fastutil.longs.LongList, int)
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

	/**
	 * This method returns all costs of all edges stored in a BigArrayBigList.
	 * 
	 * @return all costs of all edges
	 */
	IntBigArrayBigList getCosts() {

		return edgesCosts;
	}

	/**
	 * This method returns all costs of all nodes stored in a BigArrayBigList.
	 * 
	 * @return all costs of all nodes
	 */
	public IntBigArrayBigList getNodesCosts() {

		return nodesCosts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutNeighbors(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getOutNeighbors(long)
	 */
	@Override
	public LongList getOutNeighbors(long vid) {
		return getOutNeighbors(vid, 0, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutNeighborsAndCosts(long, int)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getOutNeighborsAndCosts(long, int)
	 */
	@Override
	public LongList getOutNeighborsAndCosts(long vid, int time) {
		return getOutNeighbors(vid, time, true);
	}

	private LongList getOutNeighbors(long vid, int time, boolean getCosts) {
		LongList neighborsCosts = new LongArrayList();
		NodeImpl v = (NodeImpl) getNode(vid);
		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(),
				v.getFirstEdgeOffset());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutNeighbors(long)
	 */
	@Override
	public LongList getInNeighbors(long vid) {
		return getInNeighbors(vid, 0, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getOutNeighborsAndCosts(long, int)
	 */
	@Override
	public LongList getInNeighborsAndCosts(long vid, int time) {
		return getInNeighbors(vid, time, true);
	}

	private LongList getInNeighbors(long vid, int time, boolean getCosts) {
		LongList neighborsCosts = new LongArrayList();
		NodeImpl v = (NodeImpl) getNode(vid);
		long firstEdgeId = BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset());

		Edge nextEdge = getEdge(firstEdgeId);
		long next = 0;
		
		while (next != -1) {
			if (vid == nextEdge.getToNode()) {
				neighborsCosts.add(nextEdge.getFromNode());

				if (getCosts) {
					neighborsCosts.add(getEdgeCost(nextEdge, time));
				}
				next = nextEdge.getToNodeNextEdge();
			} else if (vid == nextEdge.getFromNode()) {
				next = nextEdge.getFromNodeNextEdge();
			}
			if (next != -1) {
				nextEdge = getEdge(next);
			}
		}

		return neighborsCosts;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdge(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdge(long)
	 */
	@Override
	public Edge getEdge(long id) {

		long pos = id * Edge.EDGE_BLOCKSIZE;

		long externalId = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));
		long fromId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long toId = BigArrays.index(edges.getInt(pos++), edges.getInt(pos++));
		long fromNodeNextEdge = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));
		long toNodeNextEdge = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));
		int distance = edges.getInt(pos++);
		long costsIndex = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));
		long geometryIndex = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));
		long labelIndex = BigArrays.index(edges.getInt(pos++),
				edges.getInt(pos++));

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

		if (geometryIndex >= 0) {

			edge.setGeometry(getGeometryByGeometryIndex(geometryIndex));

		}

		edge.validate();
		return edge;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgeCosts(long)
	 */
	@Override
	public int[] getEdgeCosts(long edgeId) {

		EdgeImpl edge = (EdgeImpl) getEdge(edgeId);
		long costsIndex = edge.getCostsIndex();

		if (costsIndex == -1) {
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

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodeCosts(long)
	 */
	@Override
	public int[] getNodeCosts(long nodeId) {

		NodeImpl node = (NodeImpl) getNode(nodeId);
		long costsIndex = node.getCostsIndex();

		if (costsIndex == -1) {
			return null;
		} else {
			return getNodeCostsByCostsIndex(costsIndex);
		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodeCostsByCostsIndex(long)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphast.model.Graphast#getEdgeCost(org.graphast.model.GraphastEdge,
	 * int)
	 */
	// TODO getEdgeCost
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgeCost(org.graphast.model.Edge, int)
	 */
	@Override
	public Integer getEdgeCost(Edge e, int time) {
		EdgeImpl edge = (EdgeImpl) e;
		long costsIndex = edge.getCostsIndex();
		if (costsIndex < 0) {
			return null;
		}
		int size = edgesCosts.getInt(costsIndex++);

		int intervalSize = (maxTime / size);
		long index = (long) (costsIndex + (time / intervalSize));

		return edgesCosts.getInt(index);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdgePoints(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getGeometry(long)
	 */
	@Override
	public List<Point> getGeometry(long id) {
		EdgeImpl edge = (EdgeImpl) getEdge(id);
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
	 * @see org.graphast.model.Graph#getGeometryByGeometryIndex(long)
	 */
	@Override
	public List<Point> getGeometryByGeometryIndex(long geometryIndex) {

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

	Long getNodeId(int latitude, int longitude) {

		Long result = nodeIndex.get(BigArrays.index(latitude, longitude));

		if (result != -1) {

			return result;

		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNode(double, double)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodeId(double, double)
	 */
	@Override
	public Long getNodeId(double latitude, double longitude) {

		int lat, lon;

		lat = latLongToInt(latitude);
		lon = latLongToInt(longitude);
		if (getNodeId(lat, lon) == null) {
			return getNearestNode(latitude, longitude).getId();
		}
		else {
			return getNodeId(lat, lon);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNodeLabel(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodeLabel(long)
	 */
	@Override
	public String getNodeLabel(long id) {
		Node node = this.getNode(id);
		return node.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdgeLabel(long)
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgeLabel(long)
	 */
	@Override
	public String getEdgeLabel(long id) {
		Edge edge = this.getEdge(id);
		return edge.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNodes()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodes()
	 */
	@Override
	public IntBigArrayBigList getNodes() {
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getEdges()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdges()
	 */
	@Override
	public IntBigArrayBigList getEdges() {
		return edges;
	}

	public ObjectBigList<String> getNodesLabels() {
		return nodesLabels;
	}

	public void setNodesLabels(ObjectBigList<String> labels) {
		this.nodesLabels = labels;
	}

	public ObjectBigList<String> getEdgesLabels() {
		return edgesLabels;
	}

	public void setEdgesLabels(ObjectBigList<String> labels) {
		this.edgesLabels = labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#logNodes()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#logNodes()
	 */
	@Override
	public void logNodes() {
		for (int i = 0; i < nodes.size64() / Node.NODE_BLOCKSIZE; i++) {
			log.info(getNode(i).toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#logEdges()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#logEdges()
	 */
	@Override
	public void logEdges() {

		for (long i = 0; i < (edges.size64() / Edge.EDGE_BLOCKSIZE); i++) {
			log.info(getEdge(i).toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNumberOfNodes()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNumberOfNodes()
	 */
	@Override
	public long getNumberOfNodes() {
		return getNodes().size64() / Node.NODE_BLOCKSIZE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphast.model.Graphast#getNumberOfEdges()
	 */
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNumberOfEdges()
	 */
	@Override
	public long getNumberOfEdges() {
		return getEdges().size64() / Edge.EDGE_BLOCKSIZE;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#accessNeighborhood(org.graphast.model.Node)
	 */
	@Override
	public Long2IntMap accessNeighborhood(Node v) {

		Long2IntMap neighbors = new Long2IntOpenHashMap();

		for (Long e : this.getOutEdges(v.getId())) {

			Edge edge = this.getEdge(e);
			long neighborNodeId = edge.getToNode();
			int cost = edge.getDistance();
			if (!neighbors.containsKey(neighborNodeId)) {
				neighbors.put(neighborNodeId, cost);
			} else {
				if (neighbors.get(neighborNodeId) > cost) {
					neighbors.put(neighborNodeId, cost);
				}
			}
		}

		return neighbors;

	}
	
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#accessNeighborhood(org.graphast.model.Node)
	 */
	@Override
	public Long2IntMap accessIngoingNeighborhood(Node v) {

		Long2IntMap ingoingNeighbors = new Long2IntOpenHashMap();

		for (Long e : this.getInEdges(v.getId())) {

			Edge edge = this.getEdge(e);
			long neighborNodeId = edge.getFromNode();
			int cost = edge.getDistance();
			if (!ingoingNeighbors.containsKey(neighborNodeId)) {
				ingoingNeighbors.put(neighborNodeId, cost);
			} else {
				if (ingoingNeighbors.get(neighborNodeId) > cost) {
					ingoingNeighbors.put(neighborNodeId, cost);
				}
			}
		}

		return ingoingNeighbors;

	}

	// TODO Reimplement this method
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#accessNeighborhood(org.graphast.model.Node, int)
	 */
	@Override
	public HashMap<Node, Integer> accessNeighborhood(Node v, int time) {

		HashMap<Node, Integer> neig = new HashMap<Node, Integer>();
		for (Long e : this.getOutEdges(v.getId())) {
			Edge edge = this.getEdge(e);
			long vNeig = edge.getToNode();
			int cost = getEdgeCost(edge, time);
			// int cost = edge.getDistance();
			if (!neig.containsKey(vNeig)) {

				neig.put(getNode(vNeig), cost);
			} else {
				if (neig.get(vNeig) > cost) {
					neig.put(getNode(vNeig), cost);
				}
			}
		}

		return neig;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#hasNode(long)
	 */
	@Override
	public boolean hasNode(long id) {
		try {
			long position = id * Node.NODE_BLOCKSIZE;
			if (nodes.contains(position)) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#hasNode(org.graphast.model.Node)
	 */
	@Override
	public boolean hasNode(Node n) {

		NodeImpl node = (NodeImpl) n;

		try {
			if (nodeIndex.containsKey(BigArrays.index(
					node.getLatitudeConvertedToInt(),
					node.getLongitudeConvertedToInt()))) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#hasNode(double, double)
	 */
	@Override
	public boolean hasNode(double latitude, double longitude) {
		int lat = latLongToInt(latitude);
		int lon = latLongToInt(longitude);
		try {
			if (nodeIndex.containsKey(BigArrays.index(lat, lon))) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#addPoi(long, double, double, int, org.graphast.model.LinearFunction[])
	 */
	@Override
	public Node addPoi(long id, double lat, double lon, int category,
			LinearFunction[] costs) {
		int[] intCosts = linearFunctionArrayToCostIntArray(costs);
		Node poi = new NodeImpl(id, category, lat, lon, 0l, 0l, 0l, intCosts);
		this.addNode(poi);
		return poi;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#addPoi(long, double, double, int)
	 */
	@Override
	public Node addPoi(long id, double lat, double lon, int category) {
		Node poi = new NodeImpl(id, lat, lon, category);
		this.addNode(poi);
		return poi;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#poiGetCost(long, int)
	 */
	@Override
	public int poiGetCost(long vid, int time) {
		int i = 0;
		LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
		while (lf[i].getEndInterval() <= time) {
			i++;
		}
		return lf[i].calculateCost(time);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#poiGetCost(long)
	 */
	@Override
	public int poiGetCost(long vid) {
		LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
		return lf[0].calculateCost(0);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPoiCost(long)
	 */
	@Override
	public int[] getPoiCost(long vid) {
		return getNodeCosts(vid);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#convertToLinearFunction(int[])
	 */
	@Override
	public LinearFunction[] convertToLinearFunction(int[] costs) {
		int tetoCosts = (int)Math.ceil(costs.length / 2.0);
		LinearFunction[] result = new LinearFunction[tetoCosts];
		int interval = maxTime / (tetoCosts);
		int startInterval = 0; 
		int endInterval = interval;
		for (int i = 0; i < tetoCosts; i++) {
			result[i] = new LinearFunction(startInterval, costs[i],
					endInterval, costs[i]);
			startInterval = endInterval;
			endInterval = endInterval + interval;
		}

		return result;
	}

	int[] linearFunctionArrayToCostIntArray(LinearFunction[] linearFunction) {
		intCosts = new int[linearFunction.length];
		for (int i = 0; i < linearFunction.length; i++) {
			intCosts[i] = (linearFunction[i].getEndCost() + linearFunction[i].getStartCost())/2;
		}
		return intCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getMaximunCostValue(int[])
	 */
	@Override
	public int getMaximunCostValue(int[] costs) {

		if (costs == null) {
			// throw new IllegalArgumentException("Costs can not be null.");
			return -1;
		}

		int max = costs[0];

		for (int i = 0; i < costs.length; i++) {

			if (costs[i] > max) {
				max = costs[i];
			}
		}

		return max;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getMinimunCostValue(int[])
	 */
	@Override
	public int getMinimunCostValue(int[] costs) {

		if (costs == null) {
			// throw new IllegalArgumentException("Costs can not be null.");
			return -1;
		}

		int min = costs[0];

		for (int i = 0; i < costs.length; i++) {

			if (costs[i] < min) {
				min = costs[i];
			}
		}

		return min;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#isPoi(long)
	 */
	@Override
	public boolean isPoi(long vid) {
		return getNode(vid).getCategory() >= 0;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPoi(long)
	 */
	@Override
	public Node getPoi(long vid) {
		Node v = getNode(vid);
		if (v.getCategory() < 0)
			return null;
		else
			return v;
	}

	// TODO Verify if this access is correct
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getCategories()
	 */
	@Override
	public IntSet getCategories() {
		IntSet categories = new IntOpenHashSet();

		for (int i = 0; i < getNumberOfNodes(); i++) {
			long position = i * Node.NODE_BLOCKSIZE;
			int category = getNodes().getInt(position + 2);
			if (category != -1) {

				categories.add(category);
			}
			// long position = i*Node.NODE_BLOCKSIZE;
			// long vid = ga.getNodes().getInt(position);
			// bounds.put(vid, d.shortestPathPoi(vid, -1).getDistance());
		}

		return categories;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getCompressionType()
	 */
	@Override
	public CompressionType getCompressionType() {
		return compressionType;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setCompressionType(org.graphast.enums.CompressionType)
	 */
	@Override
	public void setCompressionType(CompressionType compressionType) {
		this.compressionType = compressionType;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#reverseGraph()
	 */
	@Override
	public void reverseGraph() {

		for (long i = 0; i < (edges.size64() / Edge.EDGE_BLOCKSIZE); i++) {

			long pos = i * Edge.EDGE_BLOCKSIZE;

			int externalIdSegment = edges.getInt(pos++);
			int externalIdOffset = edges.getInt(pos++);
			int fromNodeSegment = edges.getInt(pos++);
			int fromNodeOffset = edges.getInt(pos++);
			int toNodeSegment = edges.getInt(pos++);
			int toNodeOffset = edges.getInt(pos++);
			int fromNodeNextEdgeSegment = edges.getInt(pos++);
			int fromNodeNextEdgeOffset = edges.getInt(pos++);
			int toNodeNextEdgeSegment = edges.getInt(pos++);
			int toNodeNextEdgeOffset = edges.getInt(pos++);

			pos = i * Edge.EDGE_BLOCKSIZE;
			edges.set(pos++, externalIdSegment);
			edges.set(pos++, externalIdOffset);
			edges.set(pos++, toNodeSegment);
			edges.set(pos++, toNodeOffset);
			edges.set(pos++, fromNodeSegment);
			edges.set(pos++, fromNodeOffset);
			edges.set(pos++, toNodeNextEdgeSegment);
			edges.set(pos++, toNodeNextEdgeOffset);
			edges.set(pos++, fromNodeNextEdgeSegment);
			edges.set(pos++, fromNodeNextEdgeOffset);

		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getMaxTime()
	 */
	@Override
	public int getMaxTime() {
		return maxTime;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setMaxTime(int)
	 */
	@Override
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getTimeType()
	 */
	@Override
	public TimeType getTimeType() {
		return timeType;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setTimeType(org.graphast.enums.TimeType)
	 */
	@Override
	public void setTimeType(TimeType timeType) {
		this.timeType = timeType;

		if(timeType == TimeType.MILLISECOND) {
			maxTime = 86400000;
		} else if(timeType == TimeType.SECOND){
			maxTime = 86400;
		} else if(timeType == TimeType.MINUTE) {
			maxTime = 1440;
		} else {
			maxTime = 24;
		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdgeCosts(long, int[])
	 */
	@Override
	public void setEdgeCosts(long edgeId, int[] costs) {

		EdgeImpl edge = (EdgeImpl) getEdge(edgeId);
		edge.setCosts(costs);

		/*
		 * In this part of the method setEdgeCosts, we're multiplying the
		 * edgeCostsSize by -1 because we can optimize the entire array of costs
		 * by shifting the unused positions (we'll know that a sequence of
		 * positions is not being used by the minus sign in front of the
		 * edgeCostsSize).
		 */
		long costsIndex = edge.getCostsIndex();
		if (costsIndex != -1) {
			int edgeCostsSize = edgesCosts.getInt(costsIndex);
			edgesCosts.set(costsIndex, -edgeCostsSize);
		}
		long position = edge.getId() * Edge.EDGE_BLOCKSIZE;
		costsIndex = storeCosts(edge.getCosts(), edgesCosts);
		edge.setCostsIndex(costsIndex);

		position = position + 11;

		synchronized (edges) {

			edges.set(position++, edge.getCostsSegment());
			edges.set(position++, edge.getCostsOffset());

		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodeCosts(long, int[])
	 */
	@Override
	public void setNodeCosts(long nodeId, int[] costs) {

		NodeImpl node = (NodeImpl) getNode(nodeId);
		node.setCosts(costs);

		/*
		 * In this part of the method setNodeCosts, we're multiplying the
		 * nodeCostsSize by -1 because we can optimize the entire array of costs
		 * by shifting the unused positions (we'll know that a sequence of
		 * positions is not being used by the minus sign in front of the
		 * nodeCostsSize).
		 */
		long costsIndex = node.getCostsIndex();
		if (costsIndex != -1) {
			int nodeCostsSize = nodesCosts.getInt(costsIndex);
			nodesCosts.set(costsIndex, -nodeCostsSize);
		}
		long position = node.getId() * Node.NODE_BLOCKSIZE;
		costsIndex = storeCosts(node.getCosts(), nodesCosts);
		node.setCostsIndex(costsIndex);

		position = position + 9;

		synchronized (nodes) {

			nodes.set(position++, node.getCostsIndexSegment());
			nodes.set(position++, node.getCostsIndexOffset());

		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getArrival(int, int)
	 */
	@Override
	public int getArrival(int dt, int tt) {
		int arrivalTime = dt + tt;

		arrivalTime = arrivalTime % maxTime;
		return arrivalTime;
	}

	// TODO This method must be improved. It should use a spatial index to 
	// be much more efficient.
	// See rtree implementation in: https://github.com/davidmoten/rtree 
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNearestNode(double, double)
	 */
	@Override
	public Node getNearestNode (double latitude, double longitude) {
		StopWatch sw = new StopWatch();
		sw.start();

		Node point = new NodeImpl();
		point.setLatitude(latitude);
		point.setLongitude(longitude);
		Node nearestNode = getNode(nodes.get(0));
		Node currentNode;
		double currentDistance;
		double nearestDistance = DistanceUtils.distanceLatLong(point, nearestNode);
		for (long i = 1; i<getNumberOfNodes(); i++) {
			currentNode = getNode(i);
			currentDistance = DistanceUtils.distanceLatLong(point, currentNode);
			if (currentDistance < nearestDistance) {
				nearestNode = currentNode;
				nearestDistance = currentDistance;
			}
		}

		sw.stop();
		//log.debug("Execution Time of getNearestNode(): {}ms", sw.getTime());

		return nearestNode;
	}

	@Override
	public boolean equals(Graph obj) {
		if((obj.getNumberOfNodes() == this.getNumberOfNodes()) && (obj.getNumberOfEdges() == this.getNumberOfEdges())) {
			for(int i = 0; i < this.getNumberOfNodes(); i++) {
				if(!obj.getNode(i).equals(this.getNode(i))) {
					return false;
				}
			}
			for(int i = 0; i < this.getNumberOfEdges(); i++) {
				if(!obj.getEdge(i).equals(this.getEdge(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodeCategory(long, int)
	 */
	@Override
	public void setNodeCategory(long nodeId, int category) {
		long position = nodeId * Node.NODE_BLOCKSIZE;
		getNodes().set(position+2, category);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdgeGeometry(long, java.util.List)
	 */
	@Override
	public void setEdgeGeometry(long edgeId, List<Point> geometry) {
		EdgeImpl e = (EdgeImpl) this.getEdge(edgeId);
		e.setGeometry(geometry);
		long geometryIndex = storePoints(e.getGeometry(), points);
		e.setGeometryIndex(geometryIndex);
		this.updateEdgeInfo(e);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getBBox()
	 */
	@Override
	public BBox getBBox() {
		if (bBox == null) {
			findBBox();
		}
		return bBox;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setBBox(org.graphast.geometry.BBox)
	 */
	@Override
	public void setBBox(BBox bBox) {
		this.bBox = bBox;
	}

	private void findBBox() {
		Node node = this.getNode(0);
		Node minLatNode = null, minLongNode = null, maxLatNode = null, maxLongNode = null;
		BBox bBox = new BBox(node.getLatitude(), node.getLongitude(), node.getLatitude(), node.getLongitude());

		for (long i = 1; i < this.getNumberOfNodes(); i++) {
			node = this.getNode(i);
			if (node.getLatitude() < bBox.getMinLatitude()) {
				minLatNode = node;
				bBox.setMinLatitude(node.getLatitude());
			}
			if (node.getLatitude() > bBox.getMaxLatitude()) {
				minLongNode = node;
				bBox.setMaxLatitude(node.getLatitude());
			}
			if (node.getLongitude() < bBox.getMinLongitude()) {
				maxLatNode = node;
				bBox.setMinLongitude(node.getLongitude());
			}
			if (node.getLongitude() > bBox.getMaxLongitude()) {
				maxLongNode = node;
				bBox.setMaxLongitude(node.getLongitude());
			}
		}
		if (minLatNode != null && maxLatNode != null && minLongNode != null && maxLongNode != null) {
			log.debug("minLatitude: {},{}", minLatNode.getLatitude(), minLatNode.getLongitude());
			log.debug("maxLatitude: {},{}", maxLatNode.getLatitude(), maxLatNode.getLongitude());
			log.debug("minLongitude: {},{}", minLongNode.getLatitude(), minLongNode.getLongitude());
			log.debug("maxLongitude: {},{}", maxLongNode.getLatitude(), maxLongNode.getLongitude());
		}
		setBBox(bBox);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPOIs()
	 */
	@Override
	public List<PoI> getPOIs() {
		return getPOIs(null);
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPOIs(java.lang.Integer)
	 */
	@Override
	public List<PoI> getPOIs(Integer categoryId) {
		List<PoI> result = new ArrayList<>();
		for (long i = 0; i < this.getNumberOfNodes(); i++) {
			Node n = this.getNode(i);
			if ((categoryId == null && n.getCategory() >= 0) || 
					(categoryId != null && n.getCategory() == categoryId)) {
				PoICategory poiCategory = new PoICategory(n.getCategory());
				result.add(new PoI(n.getLabel(), n.getLatitude(), n.getLongitude(), poiCategory));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPOICategories()
	 */
	@Override
	public List<Integer> getPOICategories() {
		List<Integer> result = new ArrayList<Integer>();
		for (long i = 0; i < this.getNumberOfNodes(); i++) {
			Node n = this.getNode(i);
			if ( n.getCategory() >= 0 && (! result.contains(n.getCategory())) ) {
				result.add(n.getCategory());
			}
		}
		Collections.sort(result);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getDirectory()
	 */
	@Override
	public String getDirectory() {
		return directory;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getAbsoluteDirectory()
	 */
	@Override
	public String getAbsoluteDirectory() {
		return absoluteDirectory;
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setDirectory(java.lang.String)
	 */
	@Override
	public void setDirectory(String directory) {
		this.absoluteDirectory = FileUtils.getAbsolutePath(directory);
		this.directory = directory;
	}
	
	/*
	 * A partir daqui estão os métodos adicionais vindos da antiga classe GraphBoundsImpl:
	 * 
	 * Métodos save,load apenas foram adicionados para os mesmo métodos na GrahImpl
	 * Métodos accessNeighborhood, poiGetCost foi criado mais uma versão
	 * Métodos getPoiCost e getNodeCost eram idênticos então foram mantidos
	 */

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#createEdgesLowerBounds()
	 */
	@Override
	public void createEdgesLowerBounds() {
		long numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(long i=0; i<numberOfEdges; i++) {
			edge = getEdge(i);
			edgesLowerBound.put((long)edge.getId(), getMinimunCostValue(edge.getCosts()));
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#createEdgesUpperBounds()
	 */
	@Override
	public void createEdgesUpperBounds() {

		long numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(int i=0; i<numberOfEdges; i++) {
			edge = getEdge(i);
			edgesUpperBound.put((long)edge.getId(), getMaximunCostValue(edge.getCosts()));
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#createNodesLowerBounds()
	 */
	@Override
	public void createNodesLowerBounds() {
		long numberOfNodes = getNumberOfNodes();
		Node node; 

		for(long i=0; i<numberOfNodes; i++) {
			node = getNode(i);
			nodesLowerBound.put((long)node.getId(), getMinimunCostValue(node.getCosts()));
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#createNodesUpperBounds()
	 */
	@Override
	public void createNodesUpperBounds() {

		long numberOfNodes = getNumberOfNodes();
		Node node; 

		for(int i=0; i<numberOfNodes; i++) {
			node = getNode(i);
			nodesUpperBound.put((long)node.getId(), getMaximunCostValue(node.getCosts()));
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#accessNeighborhood(org.graphast.model.Node, short, int)
	 */
	@Override
	public Long2IntMap accessNeighborhood(Node v, short graphType, int time){

		Long2IntMap neighbors = new Long2IntOpenHashMap();
		int cost;

		for (Long e : this.getOutEdges(v.getId()) ) {

			Edge edge = this.getEdge(e);
			long neighborNodeId =  edge.getToNode();

			if(graphType == 0) {
				cost = this.getEdgeCost(edge, time);
			} else if(graphType == 1) {
				cost = getEdgesLowerBound().get(edge.getId());
			} else {
				cost = getEdgesUpperBound().get(edge.getId());
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

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#poiGetCost(long, short)
	 */
	@Override
	public int poiGetCost(long vid, short graphType){

		if(graphType == 0) {
			LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
			return lf[0].calculateCost(0);
		} else if(graphType == 1){
			int[] nodeLowerBound = new int[] {getNodesLowerBound().get(vid), getNodesLowerBound().get(vid)};
			LinearFunction[] lf = convertToLinearFunction(nodeLowerBound);
			return lf[0].calculateCost(0);
		} else {
			int[] nodeUpperBound = new int[] {getNodesUpperBound().get(vid), getNodesUpperBound().get(vid)};
			LinearFunction[] lf = convertToLinearFunction(nodeUpperBound);
			return lf[0].calculateCost(0);
		}
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#createBounds()
	 */
	@Override
	public void createBounds() {
		createEdgesUpperBounds();
		createEdgesLowerBounds();
		createNodesUpperBounds();
		createNodesLowerBounds();
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgesUpperBound()
	 */
	@Override
	public Long2IntMap getEdgesUpperBound() {
		return edgesUpperBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgesLowerBound()
	 */
	@Override
	public Long2IntMap getEdgesLowerBound() {
		return edgesLowerBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodesUpperBound()
	 */
	@Override
	public Long2IntMap getNodesUpperBound() {
		return nodesUpperBound;
	}
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgeLowerCost(long)
	 */
	@Override
	public int getEdgeLowerCost(long id){
		return edgesLowerBound.get(id);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodesLowerBound()
	 */
	@Override
	public Long2IntMap getNodesLowerBound() {
		return nodesLowerBound;
	}
	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgeUpperCost(long)
	 */
	@Override
	public int getEdgeUpperCost(long id){
		return edgesUpperBound.get(id);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getReverseGraphBounds()
	 */
	@Override
	public GraphBounds getReverseGraphBounds() {
		if (this.reverseGraph == null) {
			try {
				// load existent reverse graph
				reverseGraph = new GraphImpl(this.directory + "/reverse");
				reverseGraph.load();
			} catch (Exception e) {
				// creates a new reverse graph
				reverseGraph = new GraphImpl(this.directory);
				reverseGraph.load();
				reverseGraph.reverseGraph();
				reverseGraph.setDirectory(this.directory + "/reverse");
				reverseGraph.save();
			}
		}
		return this.reverseGraph;
	}
	
	@Override
	public Graph getReverseGraph() {
		if (this.reverseGraph == null) {
			try {
				// load existent reverse graph
				reverseGraph = new GraphImpl(this.directory + "/reverse");
				reverseGraph.load();
			} catch (Exception e) {
				// creates a new reverse graph
				reverseGraph = new GraphImpl(this.directory);
				reverseGraph.load();
				reverseGraph.reverseGraph();
				reverseGraph.setDirectory(this.directory + "/reverse");
				reverseGraph.save();
			}
		}
		return this.reverseGraph;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getLog()
	 */
	@Override
	public Logger getLog() {
		return log;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getNodeIndex()
	 */
	@Override
	public Long2LongMap getNodeIndex() {
		return nodeIndex;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getEdgesCosts()
	 */
	@Override
	public IntBigArrayBigList getEdgesCosts() {
		return edgesCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getPoints()
	 */
	@Override
	public IntBigArrayBigList getPoints() {
		return points;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getBlockSize()
	 */
	@Override
	public int getBlockSize() {
		return blockSize;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getIntCosts()
	 */
	@Override
	public int[] getIntCosts() {
		return intCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#getbBox()
	 */
	@Override
	public BBox getbBox() {
		return bBox;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setLog(org.slf4j.Logger)
	 */
	@Override
	public void setLog(Logger log) {
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodeIndex(it.unimi.dsi.fastutil.longs.Long2LongMap)
	 */
	@Override
	public void setNodeIndex(Long2LongMap nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setAbsoluteDirectory(java.lang.String)
	 */
	@Override
	public void setAbsoluteDirectory(String absoluteDirectory) {
		this.absoluteDirectory = absoluteDirectory;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodes(it.unimi.dsi.fastutil.ints.IntBigArrayBigList)
	 */
	@Override
	public void setNodes(IntBigArrayBigList nodes) {
		this.nodes = nodes;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdges(it.unimi.dsi.fastutil.ints.IntBigArrayBigList)
	 */
	@Override
	public void setEdges(IntBigArrayBigList edges) {
		this.edges = edges;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdgesCosts(it.unimi.dsi.fastutil.ints.IntBigArrayBigList)
	 */
	@Override
	public void setEdgesCosts(IntBigArrayBigList edgesCosts) {
		this.edgesCosts = edgesCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodesCosts(it.unimi.dsi.fastutil.ints.IntBigArrayBigList)
	 */
	@Override
	public void setNodesCosts(IntBigArrayBigList nodesCosts) {
		this.nodesCosts = nodesCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setPoints(it.unimi.dsi.fastutil.ints.IntBigArrayBigList)
	 */
	@Override
	public void setPoints(IntBigArrayBigList points) {
		this.points = points;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setBlockSize(int)
	 */
	@Override
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setIntCosts(int[])
	 */
	@Override
	public void setIntCosts(int[] intCosts) {
		this.intCosts = intCosts;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setbBox(org.graphast.geometry.BBox)
	 */
	@Override
	public void setbBox(BBox bBox) {
		this.bBox = bBox;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdgesUpperBound(it.unimi.dsi.fastutil.longs.Long2IntMap)
	 */
	@Override
	public void setEdgesUpperBound(Long2IntMap edgesUpperBound) {
		this.edgesUpperBound = edgesUpperBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setEdgesLowerBound(it.unimi.dsi.fastutil.longs.Long2IntMap)
	 */
	@Override
	public void setEdgesLowerBound(Long2IntMap edgesLowerBound) {
		this.edgesLowerBound = edgesLowerBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodesUpperBound(it.unimi.dsi.fastutil.longs.Long2IntMap)
	 */
	@Override
	public void setNodesUpperBound(Long2IntMap nodesUpperBound) {
		this.nodesUpperBound = nodesUpperBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setNodesLowerBound(it.unimi.dsi.fastutil.longs.Long2IntMap)
	 */
	@Override
	public void setNodesLowerBound(Long2IntMap nodesLowerBound) {
		this.nodesLowerBound = nodesLowerBound;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Graph#setReverseGraph(org.graphast.model.GraphBounds)
	 */
	@Override
	public void setReverseGraph(GraphBounds reverseGraph) {
		this.reverseGraph = reverseGraph;
	}
	
	
	
	/**
	 * This is an utility method to print the internal representation of the edges in Graphast.
	 */
	public void printInternalEdgeRepresentation() {
		long numberOfEdges = this.getNumberOfEdges();
		System.out.println("EdgeId\tFromNode\tToNode\tFromNodeNextEdge\tToNodeNextEdge");
		for (long i = 0; i < numberOfEdges; i++) {
			Edge edge = this.getEdge(i);
			System.out.print(i);
			System.out.print("\t");
			System.out.print(edge.getFromNode());
			System.out.print("\t");
			System.out.print(edge.getToNode());
			System.out.print("\t");
			System.out.print(edge.getFromNodeNextEdge());
			System.out.print("\t");
			System.out.println(edge.getToNodeNextEdge());
		}
	}

}
