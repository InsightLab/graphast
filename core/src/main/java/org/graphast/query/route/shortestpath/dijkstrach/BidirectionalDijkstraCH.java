package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalDijkstraCH {

	protected static int wasSettled = -1;
	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Node source;
	Node target;

	Path path;

	PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
	PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();

	HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
	HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode;

	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfTotalSettleNodes = 0;

	// ---NearestNeighborAccess
	StopWatch totalNearestNeighborSW = new StopWatch();
	int numberOfTotalNeighborsAccess = 0;
	int averageTotalNeighborsAccessTime = 0;

	StopWatch forwardNearestNeighborSW = new StopWatch();
	int numberOfForwardNeighborsAccess = 0;
	int averageForwardNeighborsAccessTime = 0;

	StopWatch backwardNearestNeighborSW = new StopWatch();
	int numberOfBackwardNeighborsAccess = 0;
	int averageBackwardNeighborsAccessTime = 0;

	// ---ExpandingVertexTime
	StopWatch totalExpantingVertexSW = new StopWatch();
	double totalExpandingVertexTime = 0;
	double averageTotalExpandingVertexTime = 0;

	StopWatch forwardExpandingVertexSW = new StopWatch();
	double numberOfForwardExpandingVertex = 0;
	double averageForwardExpandingVertexTime = 0;

	StopWatch backwardExpandingVertexSW = new StopWatch();
	double numberOfBackwardExpandingVertex = 0;
	double averageBackwardExpandingVertexTime = 0;
	
	// ---SearchDirection
	
	StopWatch forwardSearchSW = new StopWatch();
	double numberOfForwardSearches = 0;
	double averageForwardSearchTime = 0;
	
	StopWatch backwardSearchSW = new StopWatch();
	double numberOfBackwardSearches = 0;
	double averageBackwardSearchTime = 0;

	private CHGraph graph;

	public BidirectionalDijkstraCH(CHGraph graph) {

		this.graph = graph;

	}

	/**
	 * Bidirectional Dijkstra algorithm modified to deal with the Contraction
	 * Hierarchies speed up technique.
	 * 
	 * @param source
	 *            source node
	 * @param target
	 *            target node
	 */
	public Path execute(Node source, Node target) {

		this.setSource(source);
		this.setTarget(target);

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);

		while (!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {

			// Condition to alternate between forward and backward search
			if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {

				forwardSearchSW.start();
				forwardSearch();
				forwardSearchSW.stop();
				numberOfForwardSearches++;

			} else {
				
				backwardSearchSW.start();
				backwardSearch();
				backwardSearchSW.stop();
				numberOfBackwardSearches++;

			}

			if (path != null) {

				return path;

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void forwardSearch() {

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		numberOfForwardSettleNodes += 1;

		// Stopping criteria of Bidirectional search
		if (backwardsSettleNodes.containsKey(forwardsRemovedNode.getId())) {

			meetingNode = forwardsRemovedNode;
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();

			validateForwardMeetingNode();

			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		forwardExpandingVertexSW.start();
		expandVertexForward(forwardsRemovedNode, forwardsUnsettleNodes, forwardsParentNodes, forwardsSettleNodes);
		forwardExpandingVertexSW.stop();

		numberOfForwardExpandingVertex++;

	}

	private void backwardSearch() {

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		numberOfBackwardSettleNodes += 1;

		// Stopping criteria of Bidirectional search
		if (forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {
			
			meetingNode = backwardsRemovedNode;
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();

			validateBackwardMeetingNode();

			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		backwardExpandingVertexSW.start();
		expandVertexBackward(backwardsRemovedNode, backwardsUnsettleNodes, backwardsParentNodes,
				backwardsSettleNodes);
		backwardExpandingVertexSW.stop();

		numberOfBackwardExpandingVertex++;
	
	}

	private void validateForwardMeetingNode() {

		for (Entry<Long, Integer> candidateNode : forwardsSettleNodes.entrySet()) {

			for (DistanceEntry entry : backwardsUnsettleNodes) {

				if (entry.getId() == candidateNode.getKey() && (backwardsSettleNodes.get(forwardsRemovedNode.getId())
						+ forwardsSettleNodes.get(forwardsRemovedNode.getId()) > entry.getDistance()
								+ forwardsSettleNodes.get(candidateNode.getKey()))) {

					forwardsParentNodes.remove(meetingNode.getId());
					meetingNode = new DistanceEntry(candidateNode.getKey(), entry.getDistance(),
							backwardsParentNodes.get(candidateNode.getKey()).getId());

				}
			}
		}

	}

	private void validateBackwardMeetingNode() {

		for (Entry<Long, Integer> candidateNode : backwardsSettleNodes.entrySet()) {

			for (DistanceEntry entry : forwardsUnsettleNodes) {

				if (entry.getId() == candidateNode.getKey() && (forwardsSettleNodes.get(backwardsRemovedNode.getId())
						+ backwardsSettleNodes.get(backwardsRemovedNode.getId()) > entry.getDistance()
								+ backwardsSettleNodes.get(candidateNode.getKey()))) {

					backwardsParentNodes.remove(meetingNode.getId());
					meetingNode = new DistanceEntry(candidateNode.getKey(), entry.getDistance(),
							forwardsParentNodes.get(candidateNode.getKey()).getId());

				}
			}
		}

	}

	private void expandVertexForward(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes) {

		forwardNearestNeighborSW.start();
		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removedNode.getId()));
		forwardNearestNeighborSW.stop();
		numberOfForwardNeighborsAccess++;
		averageForwardNeighborsAccessTime += forwardNearestNeighborSW.getNanos();

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()),
					removedNode.getId());

			Edge edge;
			int distance;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, forwardDirection);

				addParent(vid, distance, edge, parentNodes, settleNodes, removedNode);

			} else {

				int cost = settleNodes.get(vid);
				distance = settleNodes.get(removedNode.getId()) + neighbors.get(vid);

				if (cost != wasSettled && cost > distance) {

					unsettleNodes.remove(newEntry);
					unsettleNodes.offer(newEntry);
					settleNodes.remove(newEntry.getId());
					settleNodes.put(newEntry.getId(), distance);

					parentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(removedNode.getId(), vid, distance, forwardDirection);
					parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
		}
	}

	private void addParent(long vid, int distance, Edge edge, HashMap<Long, RouteEntry> parentNodes,
			HashMap<Long, Integer> settleNodes, DistanceEntry removedNode) {

		if (parentNodes.containsKey(vid)) {
			if (parentNodes.get(vid).getCost() + settleNodes.get(parentNodes.get(vid).getId()) > distance
					+ settleNodes.get(removedNode.getId())) {
				parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));
			}
		} else {
			parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));
		}

	}

	private void expandVertexBackward(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes) {

		backwardNearestNeighborSW.start();
		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(removedNode.getId()));
		backwardNearestNeighborSW.stop();
		numberOfBackwardNeighborsAccess++;
		averageBackwardNeighborsAccessTime += backwardNearestNeighborSW.getNanos();

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()),
					removedNode.getId());

			Edge edge;
			int distance;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, backwardDirection);

				addParent(vid, distance, edge, parentNodes, settleNodes, removedNode);

			} else {

				int cost = settleNodes.get(vid);
				distance = settleNodes.get(removedNode.getId()) + neighbors.get(vid);

				if (cost != wasSettled && cost > distance) {

					unsettleNodes.remove(newEntry);
					unsettleNodes.offer(newEntry);
					settleNodes.remove(newEntry.getId());
					settleNodes.put(newEntry.getId(), distance);

					parentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(removedNode.getId(), vid, distance, backwardDirection);
					parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
		}
	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		RouteEntry nextForwardParent = forwardsParentNodes.get(meetingNode.getId());

		resultListOfParents.put(meetingNode.getId(), nextForwardParent);

		while (forwardsParentNodes.get(nextForwardParent.getId()) != null) {

			resultListOfParents.put(nextForwardParent.getId(), forwardsParentNodes.get(nextForwardParent.getId()));

			nextForwardParent = forwardsParentNodes.get(nextForwardParent.getId());

		}

		RouteEntry nextBackwardsParent = new RouteEntry(meetingNode.getId(),
				backwardsParentNodes.get(meetingNode.getId()).getCost(),
				backwardsParentNodes.get(meetingNode.getId()).getEdgeId(),
				backwardsParentNodes.get(meetingNode.getId()).getLabel());

		resultListOfParents.put(backwardsParentNodes.get(meetingNode.getId()).getId(), nextBackwardsParent);

		long nextNodeId = backwardsParentNodes.get(meetingNode.getId()).getId();

		while (backwardsParentNodes.get(nextNodeId) != null) {
			nextBackwardsParent = new RouteEntry(nextNodeId, backwardsParentNodes.get(nextNodeId).getCost(),
					backwardsParentNodes.get(nextNodeId).getEdgeId(), backwardsParentNodes.get(nextNodeId).getLabel());
			nextNodeId = backwardsParentNodes.get(nextNodeId).getId();

			resultListOfParents.put(nextNodeId, nextBackwardsParent);

		}

		return resultListOfParents;

	}

	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance, boolean expandingDirection) {

		if (expandingDirection) {

			return getEdgeForwards(fromNodeId, toNodeId, distance);

		} else {

			return getEdgeBackwards(fromNodeId, toNodeId, distance);

		}

	}

	private CHEdge getEdgeForwards(long fromNodeId, long toNodeId, int distance) {

		CHEdge edge = null;

		for (Long outEdge : this.graph.getOutEdges(fromNodeId)) {
			edge = this.graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}

		return edge;

	}

	private CHEdge getEdgeBackwards(long fromNodeId, long toNodeId, int distance) {

		CHEdge edge = null;

		for (Long inEdge : this.graph.getInEdges(fromNodeId)) {
			edge = this.graph.getEdge(inEdge);
			if ((int) edge.getFromNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}

		CHEdge returningEdge = new CHEdgeImpl(edge);

		returningEdge.setFromNode(fromNodeId);
		returningEdge.setToNode(toNodeId);

		return returningEdge;

	}

	public static int getWasSettled() {
		return wasSettled;
	}

	public static void setWasSettled(int wasSettled) {
		BidirectionalDijkstraCH.wasSettled = wasSettled;
	}

	public static boolean isForwardDirection() {
		return forwardDirection;
	}

	public static void setForwardDirection(boolean forwardDirection) {
		BidirectionalDijkstraCH.forwardDirection = forwardDirection;
	}

	public static boolean isBackwardDirection() {
		return backwardDirection;
	}

	public static void setBackwardDirection(boolean backwardDirection) {
		BidirectionalDijkstraCH.backwardDirection = backwardDirection;
	}

	public int getNumberOfForwardSettleNodes() {
		return numberOfForwardSettleNodes;
	}

	public void setNumberOfForwardSettleNodes(int numberOfForwardSettleNodes) {
		this.numberOfForwardSettleNodes = numberOfForwardSettleNodes;
	}

	public int getNumberOfBackwardSettleNodes() {
		return numberOfBackwardSettleNodes;
	}

	public void setNumberOfBackwardSettleNodes(int numberOfBackwardSettleNodes) {
		this.numberOfBackwardSettleNodes = numberOfBackwardSettleNodes;
	}

	public int getNumberOfTotalSettleNodes() {
		return numberOfTotalSettleNodes;
	}

	public void setNumberOfTotalSettleNodes(int numberOfTotalSettleNodes) {
		this.numberOfTotalSettleNodes = numberOfTotalSettleNodes;
	}

	public StopWatch getTotalNearestNeighborSW() {
		return totalNearestNeighborSW;
	}

	public void setTotalNearestNeighborSW(StopWatch totalNearestNeighborSW) {
		this.totalNearestNeighborSW = totalNearestNeighborSW;
	}

	public int getNumberOfTotalNeighborsAccess() {
		return numberOfTotalNeighborsAccess;
	}

	public void setNumberOfTotalNeighborsAccess(int numberOfTotalNeighborsAccess) {
		this.numberOfTotalNeighborsAccess = numberOfTotalNeighborsAccess;
	}

	public int getAverageTotalNeighborsAccessTime() {
		return averageTotalNeighborsAccessTime;
	}

	public void setAverageTotalNeighborsAccessTime(int averageTotalNeighborsAccessTime) {
		this.averageTotalNeighborsAccessTime = averageTotalNeighborsAccessTime;
	}

	public StopWatch getForwardNearestNeighborSW() {
		return forwardNearestNeighborSW;
	}

	public void setForwardNearestNeighborSW(StopWatch forwardNearestNeighborSW) {
		this.forwardNearestNeighborSW = forwardNearestNeighborSW;
	}

	public int getNumberOfForwardNeighborsAccess() {
		return numberOfForwardNeighborsAccess;
	}

	public void setNumberOfForwardNeighborsAccess(int numberOfForwardNeighborsAccess) {
		this.numberOfForwardNeighborsAccess = numberOfForwardNeighborsAccess;
	}

	public int getAverageForwardNeighborsAccessTime() {
		return averageForwardNeighborsAccessTime;
	}

	public void setAverageForwardNeighborsAccessTime(int averageForwardNeighborsAccessTime) {
		this.averageForwardNeighborsAccessTime = averageForwardNeighborsAccessTime;
	}

	public StopWatch getBackwardNearestNeighborSW() {
		return backwardNearestNeighborSW;
	}

	public void setBackwardNearestNeighborSW(StopWatch backwardNearestNeighborSW) {
		this.backwardNearestNeighborSW = backwardNearestNeighborSW;
	}

	public int getNumberOfBackwardNeighborsAccess() {
		return numberOfBackwardNeighborsAccess;
	}

	public void setNumberOfBackwardNeighborsAccess(int numberOfBackwardNeighborsAccess) {
		this.numberOfBackwardNeighborsAccess = numberOfBackwardNeighborsAccess;
	}

	public int getAverageBackwardNeighborsAccessTime() {
		return averageBackwardNeighborsAccessTime;
	}

	public void setAverageBackwardNeighborsAccessTime(int averageBackwardNeighborsAccessTime) {
		this.averageBackwardNeighborsAccessTime = averageBackwardNeighborsAccessTime;
	}

	public StopWatch getTotalExpantingVertexSW() {
		return totalExpantingVertexSW;
	}

	public void setTotalExpantingVertexSW(StopWatch totalExpantingVertexSW) {
		this.totalExpantingVertexSW = totalExpantingVertexSW;
	}

	public double getTotalExpandingVertexTime() {
		return totalExpandingVertexTime;
	}

	public void setTotalExpandingVertexTime(double totalExpandingVertexTime) {
		this.totalExpandingVertexTime = totalExpandingVertexTime;
	}

	public double getAverageTotalExpandingVertexTime() {
		return averageTotalExpandingVertexTime;
	}

	public void setAverageTotalExpandingVertexTime(double averageTotalExpandingVertexTime) {
		this.averageTotalExpandingVertexTime = averageTotalExpandingVertexTime;
	}

	public StopWatch getForwardExpandingVertexSW() {
		return forwardExpandingVertexSW;
	}

	public void setForwardExpandingVertexSW(StopWatch forwardExpandingVertexSW) {
		this.forwardExpandingVertexSW = forwardExpandingVertexSW;
	}

	public double getNumberOfForwardExpandingVertex() {
		return numberOfForwardExpandingVertex;
	}

	public void setNumberOfForwardExpandingVertex(double numberOfForwardExpandingVertex) {
		this.numberOfForwardExpandingVertex = numberOfForwardExpandingVertex;
	}

	public double getAverageForwardExpandingVertexTime() {
		return averageForwardExpandingVertexTime;
	}

	public void setAverageForwardExpandingVertexTime(double averageForwardExpandingVertexTime) {
		this.averageForwardExpandingVertexTime = averageForwardExpandingVertexTime;
	}

	public StopWatch getBackwardExpandingVertexSW() {
		return backwardExpandingVertexSW;
	}

	public void setBackwardExpandingVertexSW(StopWatch backwardExpandingVertexSW) {
		this.backwardExpandingVertexSW = backwardExpandingVertexSW;
	}

	public double getNumberOfBackwardExpandingVertex() {
		return numberOfBackwardExpandingVertex;
	}

	public void setNumberOfBackwardExpandingVertex(double numberOfBackwardExpandingVertex) {
		this.numberOfBackwardExpandingVertex = numberOfBackwardExpandingVertex;
	}

	public double getAverageBackwardExpandingVertexTime() {
		return averageBackwardExpandingVertexTime;
	}

	public void setAverageBackwardExpandingVertexTime(double averageBackwardExpandingVertexTime) {
		this.averageBackwardExpandingVertexTime = averageBackwardExpandingVertexTime;
	}

	public CHGraph getGraph() {
		return graph;
	}

	public void setGraph(CHGraph graph) {
		this.graph = graph;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public StopWatch getForwardSearchSW() {
		return forwardSearchSW;
	}

	public void setForwardSearchSW(StopWatch forwardSearchSW) {
		this.forwardSearchSW = forwardSearchSW;
	}

	public StopWatch getBackwardSearchSW() {
		return backwardSearchSW;
	}

	public void setBackwardSearchSW(StopWatch backwardSearchSW) {
		this.backwardSearchSW = backwardSearchSW;
	}

	public double getNumberOfForwardSearches() {
		return numberOfForwardSearches;
	}

	public void setNumberOfForwardSearches(double numberOfForwardSearches) {
		this.numberOfForwardSearches = numberOfForwardSearches;
	}

	public double getNumberOfBackwardSearches() {
		return numberOfBackwardSearches;
	}

	public void setNumberOfBackwardSearches(double numberOfBackwardSearches) {
		this.numberOfBackwardSearches = numberOfBackwardSearches;
	}
	
}
