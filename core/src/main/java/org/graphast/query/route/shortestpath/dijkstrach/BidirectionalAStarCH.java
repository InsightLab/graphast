package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.util.DistanceUtils;

import com.graphhopper.util.DistancePlaneProjection;
import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalAStarCH {

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	DistancePlaneProjection dist = new DistancePlaneProjection();

	Node source;
	Node target;

	Path path;

	PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
	PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
	// HashMap<Long, Integer> forwardsUnsettleNodesAux = new HashMap<>();
	// HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

	HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
	HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();

	HashMap<Long, Integer> forwardsSettleNodesAux = new HashMap<>();
	HashMap<Long, Integer> backwardsSettleNodesAux = new HashMap<>();

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);
	private CHGraph graph;

	private int alreadyVisitedWeight;

	// --METRICS VARIABLES
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfRegularSearches = 0;

	public BidirectionalAStarCH(CHGraph graph) {

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

		initializeQueueForward(source, forwardsUnsettleNodes);
		initializeQueueBackward(target, backwardsUnsettleNodes);

		forwardsSettleNodes.put(source.getId(), 0);
		backwardsSettleNodes.put(target.getId(), 0);

		while (!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {

			// Condition to alternate between forward and backward search
			if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {
				forwardSearch();
			} else {
				backwardSearch();
			}

			if (path != null) {

				return path;

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void forwardSearch() {

		// Stopping criteria of Bidirectional search
		if (forwardsUnsettleNodes.peek().getDistance() + backwardsUnsettleNodes.peek().getDistance() >= meetingNode
				.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsSettleNodesAux.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		numberOfForwardSettleNodes++;

		expandVertexForward();

	}

	private void backwardSearch() {

		// Stopping criteria of Bidirectional search
		if (forwardsUnsettleNodes.peek().getDistance() + backwardsUnsettleNodes.peek().getDistance() >= meetingNode
				.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsSettleNodesAux.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		numberOfBackwardSettleNodes++;

		expandVertexBackward();

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {
			graph.getNode(vid);
			if (forwardsSettleNodesAux.containsKey(vid)) {
				continue;
			}

			CHNode w = graph.getNode(vid);

			alreadyVisitedWeight = forwardsSettleNodes.get(forwardsRemovedNode.getId()) + neighbors.get(vid);
			double currentWeightToGoal = dist.calcDist(target.getLatitude(), target.getLongitude(),
					graph.getNode(vid).getLatitude(), graph.getNode(vid).getLongitude()) * 1000;
			double estimationFullDist = alreadyVisitedWeight + currentWeightToGoal;
			DistanceEntry newEntry = new DistanceEntry(vid, (int) Math.round(estimationFullDist),
					forwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!forwardsSettleNodes.containsKey(vid) || forwardsSettleNodes.get(vid) > alreadyVisitedWeight) {

				if (!forwardsSettleNodes.containsKey(vid)) {

					forwardsUnsettleNodes.offer(newEntry);
					forwardsSettleNodes.put(newEntry.getId(), alreadyVisitedWeight);

					edge = getEdge(forwardsRemovedNode.getId(), vid, neighbors.get(vid), forwardDirection);
					forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), neighbors.get(vid),
							edge.getId(), edge.getLabel()));

				} else {
					forwardsUnsettleNodes.remove(newEntry);

					forwardsSettleNodes.remove(newEntry.getId());
					forwardsSettleNodes.put(newEntry.getId(), alreadyVisitedWeight);

					forwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);
					forwardsParentNodes.put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}

				forwardsUnsettleNodes.offer(newEntry);
				verifyMeetingNodeForwardSearch(vid, neighbors);

			}

		}
	}

	private void verifyMeetingNodeForwardSearch(long vid, Long2IntMap neighbors) {

		if (backwardsSettleNodesAux.containsKey(vid)
				&& (alreadyVisitedWeight + backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(alreadyVisitedWeight + backwardsSettleNodes.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());
		}

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			if (backwardsSettleNodesAux.containsKey(vid)) {
				continue;
			}

			CHNode w = graph.getNode(vid);

			alreadyVisitedWeight = backwardsSettleNodes.get(backwardsRemovedNode.getId()) + neighbors.get(vid);
			// TODO Double check this backwardHeuristic
			double currentWeightToGoal = dist.calcDist(source.getLatitude(), source.getLongitude(),
					graph.getNode(vid).getLatitude(), graph.getNode(vid).getLongitude()) * 1000;
			double estimationFullDist = alreadyVisitedWeight + currentWeightToGoal;
			DistanceEntry newEntry = new DistanceEntry(vid, (int) Math.round(estimationFullDist),
					backwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!backwardsSettleNodes.containsKey(vid) || backwardsSettleNodes.get(vid) > alreadyVisitedWeight) {

				if (!backwardsSettleNodes.containsKey(vid)) {
					// adicionar aos settle nodes, adicionar aos parents, criar
					// new entry, separar o Unsettle nodes
					backwardsUnsettleNodes.offer(newEntry);
					backwardsSettleNodes.put(newEntry.getId(), alreadyVisitedWeight);

					edge = getEdge(backwardsRemovedNode.getId(), vid, neighbors.get(vid), backwardDirection);
					backwardsParentNodes.put(vid, new RouteEntry(backwardsRemovedNode.getId(), neighbors.get(vid),
							edge.getId(), edge.getLabel()));

				} else {
					backwardsUnsettleNodes.remove(newEntry);
					// TODO falta colocar o DE aqui tambem

					backwardsSettleNodes.remove(newEntry.getId());
					backwardsSettleNodes.put(newEntry.getId(), alreadyVisitedWeight);

					backwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));
				}

				backwardsUnsettleNodes.offer(newEntry);
				verifyMeetingNodeBackwardSearch(vid, neighbors);

			}
		}
	}

	private void verifyMeetingNodeBackwardSearch(long vid, Long2IntMap neighbors) {

		if (forwardsSettleNodesAux.containsKey(vid)
				&& (alreadyVisitedWeight + forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(alreadyVisitedWeight + forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}

	}

	private double forwardHeuristic(Node vid) {

		DistancePlaneProjection distance = new DistancePlaneProjection();

		return (distance.calcDist(vid.getLatitude(), vid.getLongitude(), target.getLatitude(), target.getLongitude())
				/ 6371000) * 6378137 * 100;

		// return DistanceUtils.distanceLatLong(vid, target);

		// return Math.sqrt( Math.pow(target.getLatitude() - vid.getLatitude(),
		// 2) + Math.pow(target.getLongitude() - vid.getLongitude(), 2) );

	}

	private double backwardHeuristic(Node vid) {

		DistancePlaneProjection distance = new DistancePlaneProjection();

		return (distance.calcDist(vid.getLatitude(), vid.getLongitude(), source.getLatitude(), source.getLongitude())
				/ 6371000) * 6378137 * 100;

		// return DistanceUtils.distanceLatLong(vid, source);

		// return Math.sqrt( Math.pow(source.getLatitude() - vid.getLatitude(),
		// 2) + Math.pow(source.getLongitude() - vid.getLongitude(), 2) );

	}

	private void initializeQueueForward(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private void initializeQueueBackward(Node node, PriorityQueue<DistanceEntry> queue) {

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

		if (meetingNode.getId() != target.getId()) {

			RouteEntry nextBackwardsParent = new RouteEntry(meetingNode.getId(),
					backwardsParentNodes.get(meetingNode.getId()).getCost(),
					backwardsParentNodes.get(meetingNode.getId()).getEdgeId(),
					backwardsParentNodes.get(meetingNode.getId()).getLabel());

			resultListOfParents.put(backwardsParentNodes.get(meetingNode.getId()).getId(), nextBackwardsParent);

			long nextNodeId = backwardsParentNodes.get(meetingNode.getId()).getId();

			while (backwardsParentNodes.get(nextNodeId) != null) {
				nextBackwardsParent = new RouteEntry(nextNodeId, backwardsParentNodes.get(nextNodeId).getCost(),
						backwardsParentNodes.get(nextNodeId).getEdgeId(),
						backwardsParentNodes.get(nextNodeId).getLabel());
				nextNodeId = backwardsParentNodes.get(nextNodeId).getId();

				resultListOfParents.put(nextNodeId, nextBackwardsParent);

			}
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

	public Path executeRegular(Node source, Node target) {

		this.setSource(source);
		this.setTarget(target);

		initializeQueueForward(source, forwardsUnsettleNodes);
		initializeQueueBackward(target, backwardsUnsettleNodes);

		while (!forwardsUnsettleNodes.isEmpty()) {

			forwardsRemovedNode = forwardsUnsettleNodes.poll();
			forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

			// Stopping criteria of Bidirectional search
			if (forwardsRemovedNode.getId() == target.getId()) {

				path = new Path();
				path.constructPath(target.getId(), forwardsParentNodes, graph);

				return path;

			}

			StopWatch auxiliarSW = new StopWatch();
			auxiliarSW.start();
			expandVertexForward();
			auxiliarSW.stop();

			numberOfRegularSearches++;

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	public boolean heuristicValidator(Edge edge) {

		if (edge.getDistance() + forwardHeuristic(graph.getNode(edge.getToNode())) >= forwardHeuristic(
				graph.getNode(edge.getFromNode()))) {
			return true;
		} else {
			return false;
		}

	}

	public void setSource(Node source) {
		this.source = source;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public int getNumberOfForwardSettleNodes() {
		return numberOfForwardSettleNodes;
	}

	public int getNumberOfBackwardSettleNodes() {
		return numberOfBackwardSettleNodes;
	}

	public int getNumberOfRegularSearches() {
		return numberOfRegularSearches;
	}

}
