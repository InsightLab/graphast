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
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.util.DistanceUtils;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalAStarCH {

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Node source;
	Node target;

	Path path;

	PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
	PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
	HashMap<Long, Integer> forwardsUnsettleNodesAux = new HashMap<>();
	HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

	HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
	HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);
	private CHGraph graph;
	
	private int newLength;

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

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);
		
		forwardsUnsettleNodesAux.put(source.getId(), 0);
		backwardsUnsettleNodesAux.put(target.getId(), 0);

		forwardsRemovedNode = forwardsUnsettleNodes.peek();
		backwardsRemovedNode = backwardsUnsettleNodes.peek();

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
		if (forwardsUnsettleNodes.peek().getDistance() + backwardsUnsettleNodes.peek().getDistance() >= meetingNode.getDistance()) {
			
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}
		
		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsUnsettleNodesAux.remove(forwardsRemovedNode.getId());
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		numberOfForwardSettleNodes++;

		expandVertexForward();

	}

	private void backwardSearch() {

		// Stopping criteria of Bidirectional search
		if (forwardsUnsettleNodes.peek().getDistance() + backwardsUnsettleNodes.peek().getDistance() >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}
		
		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsUnsettleNodesAux.remove(backwardsRemovedNode.getId());
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		numberOfBackwardSettleNodes++;

		expandVertexBackward();

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			//Change the neighbors.get(vid) by the new one
			
			newLength = neighbors.get(vid) + 
					(forwardHeuristic(graph.getNode(vid)) - forwardHeuristic(graph.getNode(forwardsRemovedNode.getId())))/4 + 
					(backwardHeuristic(graph.getNode(forwardsRemovedNode.getId())) - backwardHeuristic(graph.getNode(vid)))/4;
			
			DistanceEntry newEntry = new DistanceEntry(vid,	newLength + forwardsRemovedNode.getDistance(), forwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!forwardsSettleNodes.containsKey(vid)) {
				
				forwardsUnsettleNodes.offer(newEntry);
				forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
				forwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());
				
				distance = neighbors.get(vid);
				edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);

				forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = forwardsSettleNodes.get(vid);
				distance = neighbors.get(vid) + forwardsRemovedNode.getDistance();

				if (cost > distance) {
					forwardsUnsettleNodes.remove(newEntry);
					forwardsUnsettleNodesAux.remove(newEntry.getId());
					forwardsUnsettleNodes.offer(newEntry);
					forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
					
					forwardsSettleNodes.remove(newEntry.getId());
					forwardsSettleNodes.put(newEntry.getId(), distance);
							
						
					forwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);
					forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
			
			verifyMeetingNodeForwardSearch(vid, neighbors);
			
		}
	}
	
	private void verifyMeetingNodeForwardSearch(long vid, Long2IntMap neighbors) {

		if (backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId()) + newLength + backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(forwardsSettleNodes.get(forwardsRemovedNode.getId()) + newLength + backwardsSettleNodes.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());
		}

		if (backwardsUnsettleNodesAux.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())	+ newLength + backwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(forwardsSettleNodes.get(forwardsRemovedNode.getId()) + newLength + backwardsUnsettleNodesAux.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());
		}

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {
			
			//Change the neighbors.get(vid) by the new one
			
			newLength = neighbors.get(vid) + 
					(forwardHeuristicBackwards(graph.getNode(vid)) - forwardHeuristicBackwards(graph.getNode(backwardsRemovedNode.getId())))/4 + 
					(backwardHeuristicBackwards(graph.getNode(backwardsRemovedNode.getId())) - backwardHeuristicBackwards(graph.getNode(vid)))/4;

			DistanceEntry newEntry = new DistanceEntry(vid,	newLength + backwardsRemovedNode.getDistance(), backwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!backwardsSettleNodes.containsKey(vid)) {

				backwardsUnsettleNodes.offer(newEntry);
				backwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
				backwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);

				backwardsParentNodes.put(vid, new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = backwardsSettleNodes.get(vid);
				distance = backwardsRemovedNode.getDistance() + neighbors.get(vid);

				if (cost > distance) {
					backwardsUnsettleNodes.remove(newEntry);
					backwardsUnsettleNodes.offer(newEntry);
					
					backwardsUnsettleNodesAux.remove(newEntry.getId());
					backwardsUnsettleNodesAux.put(newEntry.getId(), distance);
					
					backwardsSettleNodes.remove(newEntry.getId());
					backwardsSettleNodes.put(newEntry.getId(), distance);

					backwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
			
			verifyMeetingNodeBackwardSearch(vid, neighbors);
			
		}
	}

	private void verifyMeetingNodeBackwardSearch(long vid, Long2IntMap neighbors) {

		if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())	+ newLength + forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + newLength	+ forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}
		if (forwardsUnsettleNodesAux.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId()) + newLength + forwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + newLength	+ forwardsUnsettleNodesAux.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}

	}
	
	private int forwardHeuristic(Node vid) {

		
		return (int) DistanceUtils.distanceLatLong(vid, target);
		
//		return (int) Math.ceil(Math.sqrt(Math.pow((target.getLatitude() - vid.getLatitude()), 2)
//				+ Math.pow((target.getLongitude() - vid.getLongitude()), 2)));

	}
	
	private int backwardHeuristic(Node vid) {

		return (int) DistanceUtils.distanceLatLong(vid, source);
		
//		return (int) Math.ceil(Math.sqrt(Math.pow((source.getLatitude() - vid.getLatitude()), 2)
//				+ Math.pow((source.getLongitude() - vid.getLongitude()), 2)));

	}
	
	private int forwardHeuristicBackwards(Node vid) {

		return (int) DistanceUtils.distanceLatLong(vid, source);
		
//		return (int) Math.ceil(Math.sqrt(Math.pow((source.getLatitude() - vid.getLatitude()), 2)
//				+ Math.pow((source.getLongitude() - vid.getLongitude()), 2)));

	}
	
	private int backwardHeuristicBackwards(Node vid) {

		return (int) DistanceUtils.distanceLatLong(vid, target);
//		return (int) Math.ceil(Math.sqrt(Math.pow((target.getLatitude() - vid.getLatitude()), 2)
//				+ Math.pow((target.getLongitude() - vid.getLongitude()), 2)));

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

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);

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
