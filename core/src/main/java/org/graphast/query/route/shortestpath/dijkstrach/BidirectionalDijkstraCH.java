package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.model.Entry;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Instruction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalDijkstraCH {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Node source;
	Node target;
	
	int startCurr = 0;
	int endCurr = 0;
	

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

	// --METRICS VARIABLES
	int expandedNodesForwardSearch = 0;
	int expandedNodesBackwardSearch = 0;
	int numberOfRegularSearches = 0;

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

		forwardsUnsettleNodesAux.put(source.getId(), 0);
		backwardsUnsettleNodesAux.put(target.getId(), 0);

		forwardsRemovedNode = forwardsUnsettleNodes.peek();
		backwardsRemovedNode = backwardsUnsettleNodes.peek();

		forwardsParentNodes.put(source.getId(), new RouteEntry(-1, 0, -1, null));
		backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));

		while (!forwardsUnsettleNodes.isEmpty() || !backwardsUnsettleNodes.isEmpty()) {

			if (!forwardsUnsettleNodes.isEmpty())
				forwardSearch();

			if (!backwardsUnsettleNodes.isEmpty())
				backwardSearch();

			// // Condition to alternate between forward and backward search
			// if (forwardsUnsettleNodes.isEmpty()) {
			// backwardSearch();
			// } else if (backwardsUnsettleNodes.isEmpty()) {
			// forwardSearch();
			// } else {
			// if (forwardsUnsettleNodes.peek().getDistance() <=
			// backwardsUnsettleNodes.peek().getDistance()) {
			// forwardSearch();
			// } else {
			// backwardSearch();
			// }
			// }

			if (path != null) {

				logger.info("PoI being searched: {}", target.getId());
				logger.info("    Number of expanded nodes in the forward search: {}", expandedNodesForwardSearch);
				logger.info("    Number of expanded nodes in the backward search: {}", expandedNodesBackwardSearch);

				return path;

			}

		}

		Path path = new Path();
		Instruction instruction = new Instruction(Integer.MAX_VALUE,
				"PATH NOT FOUND BETWEEN " + source.getId() + " AND " + target.getId(), 0, 0);
		List<Instruction> instructions = new ArrayList<>();
		instructions.add(instruction);
		path.setInstructions(instructions);
		return path;

		// throw new PathNotFoundException("Path not found between (" +
		// source.getLatitude() + "," + source.getLongitude()
		// + ") and (" + target.getLatitude() + "," + target.getLongitude() +
		// ")");

	}

	private void forwardSearch() {

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		System.out.println("[BIDIRECTIONAL FORWARD] Node being analyzed: " + graph.getNode(forwardsRemovedNode.getId()).getExternalId() + ". Distance: " + forwardsRemovedNode.getDistance());
		forwardsUnsettleNodesAux.remove(forwardsRemovedNode.getId());
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		expandedNodesForwardSearch++;

		logger.debug("[BIDIRECTIONAL FORWARD] Node being analyzed in the forwardSearch(): {}",
				forwardsRemovedNode.getId());

		expandVertexForward();
		
		if(forwardsUnsettleNodes.peek() == null && backwardsUnsettleNodes.peek() == null && meetingNode.getDistance() != Integer.MAX_VALUE) {
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;
		}
		
		if (forwardsUnsettleNodes.peek() != null) {
			startCurr = forwardsUnsettleNodes.peek().getDistance();
		} else {
			startCurr = 0;
		}
		if (backwardsUnsettleNodes.peek() != null) {
			endCurr = backwardsUnsettleNodes.peek().getDistance();
		} else {
			endCurr = 0;
		}

		if (startCurr + endCurr >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}
		
		if ((forwardsUnsettleNodes.peek() == null && backwardsUnsettleNodes.peek() == null)
				&& meetingNode.getId() == -1) {
			path = new Path();
			Instruction instruction = new Instruction(Integer.MAX_VALUE,
					"PATH NOT FOUND BETWEEN " + source.getId() + " AND " + target.getId(), 0, 0);
			List<Instruction> instructions = new ArrayList<>();
			instructions.add(instruction);
			path.setInstructions(instructions);
			return;
		}

	}

	private void backwardSearch() {

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		System.out.println("[BIDIRECTIONAL BACKWARD] Node being analyzed: "
				+ graph.getNode(backwardsRemovedNode.getId()).getExternalId() + ". Distance: "
				+ backwardsRemovedNode.getDistance());
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		expandedNodesBackwardSearch++;

		logger.debug("[BIDIRECTIONAL BACKWARD] Node being analyzed in the backwardSearch(): {}",
				backwardsRemovedNode.getId());
		
		expandVertexBackward();
		
		if(forwardsUnsettleNodes.peek() == null && backwardsUnsettleNodes.peek() == null && meetingNode.getDistance() != Integer.MAX_VALUE) {
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;
		}
		
		if (forwardsUnsettleNodes.peek() != null) {
			startCurr = forwardsUnsettleNodes.peek().getDistance();
		} else {
			startCurr = 0;
		}
		if (backwardsUnsettleNodes.peek() != null) {
			endCurr = backwardsUnsettleNodes.peek().getDistance();
		} else {
			endCurr = 0;
		}

		if (startCurr + endCurr >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		} 
		
		if ((forwardsUnsettleNodes.peek() == null && backwardsUnsettleNodes.peek() == null)
				&& meetingNode.getId() == -1) {
			path = new Path();
			Instruction instruction = new Instruction(Integer.MAX_VALUE,
					"PATH NOT FOUND BETWEEN " + source.getId() + " AND " + target.getId(), 0, 0);
			List<Instruction> instructions = new ArrayList<>();
			instructions.add(instruction);
			path.setInstructions(instructions);
			return;
		}

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));
		
		neighbors.put(forwardsRemovedNode.getId().longValue(), 0);
		
		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				logger.debug("Node ignored: {}", vid);
				continue;
			}

			verifyMeetingNodeForwardSearch(forwardsRemovedNode.getId(), vid, neighbors);
			
			logger.debug("Node considered: {}", vid);
			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + forwardsRemovedNode.getDistance(),
					forwardsRemovedNode.getId());

			// System.out.println("\tNode being expanded: " + vid + ". Distance:
			// " + newEntry.getDistance());

			Edge edge;
			int distance;

			if (forwardsSettleNodes.containsKey(newEntry.getId())) {
				continue;
			}

			if (!forwardsUnsettleNodesAux.containsKey(vid)) {

				forwardsUnsettleNodes.offer(newEntry);
				forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);

				if (forwardsParentNodes.containsKey(vid)) {
					if (forwardsParentNodes.get(vid).getCost() > distance) {
						forwardsParentNodes.put(vid,
								new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));
					}
				} else {

					forwardsParentNodes.put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			} else {

				int cost = forwardsUnsettleNodesAux.get(vid);
				distance = neighbors.get(vid) + forwardsRemovedNode.getDistance();

				if (cost > distance) {
					forwardsUnsettleNodes.remove(newEntry);
					forwardsUnsettleNodesAux.remove(newEntry.getId());
					forwardsUnsettleNodes.offer(newEntry);
					forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());

					forwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);
					forwardsParentNodes.put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
		}
	}

	private void verifyMeetingNodeForwardSearch(long removed, long vid, Long2IntMap neighbors) {
		
		if (backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(removed) + neighbors.get(vid) + backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(forwardsSettleNodes.get(removed) + neighbors.get(vid) + backwardsSettleNodes.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());
		}
		
	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		neighbors.put(backwardsRemovedNode.getId().longValue(), 0);
		
		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				// verifyMeetingNodeBackwardSearch(vid, neighbors);
				logger.debug("Node ignored: {}", vid);
				continue;
			}
			
			verifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId(), vid, neighbors);

			logger.debug("Node considered: {}", vid);

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + backwardsRemovedNode.getDistance(),
					backwardsRemovedNode.getId());

			// System.out.println("\tNode being expanded: " + vid + ". Distance:
			// " + newEntry.getDistance());

			Edge edge;
			int distance;

			if (backwardsSettleNodes.containsKey(newEntry.getId())) {
				continue;
			}

			if (!backwardsUnsettleNodesAux.containsKey(vid)) {

				backwardsUnsettleNodes.offer(newEntry);
				backwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);

				if (backwardsParentNodes.containsKey(vid)) {
					if (backwardsParentNodes.get(vid).getCost() > distance) {
						backwardsParentNodes.put(vid,
								new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));
					}
				} else {

					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}

			} else {

				int cost = backwardsUnsettleNodesAux.get(vid);
				distance = backwardsRemovedNode.getDistance() + neighbors.get(vid);

				if (cost > distance) {
					backwardsUnsettleNodes.remove(newEntry);
					backwardsUnsettleNodes.offer(newEntry);

					backwardsUnsettleNodesAux.remove(newEntry.getId());
					backwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());

					backwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
		}
	}

	private void verifyMeetingNodeBackwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(removed) + neighbors.get(vid) + forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(backwardsSettleNodes.get(removed) + neighbors.get(vid) + forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}

	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		long currrentNodeId = meetingNode.getId();
		RouteEntry nextParent = forwardsParentNodes.get(currrentNodeId);
		resultListOfParents.put(currrentNodeId, nextParent);
		currrentNodeId = nextParent.getId();

		while (forwardsParentNodes.get(currrentNodeId) != null) {
			nextParent = forwardsParentNodes.get(currrentNodeId);
			resultListOfParents.put(currrentNodeId, nextParent);
			currrentNodeId = nextParent.getId();
		}

		currrentNodeId = meetingNode.getId();

		while (backwardsParentNodes.get(currrentNodeId) != null) {
			nextParent = backwardsParentNodes.get(currrentNodeId);
			resultListOfParents.put(nextParent.getId(), new RouteEntry(currrentNodeId, nextParent.getCost(),
					nextParent.getEdgeId(), nextParent.getLabel()));
			currrentNodeId = nextParent.getId();
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
		return expandedNodesForwardSearch;
	}

	public int getNumberOfBackwardSettleNodes() {
		return expandedNodesBackwardSearch;
	}

	public int getNumberOfRegularSearches() {
		return numberOfRegularSearches;
	}

	public void executeToAll(Node source) {

		for (int i = 0; i < graph.getNumberOfNodes(); i++) {

			path = null;
			meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);

			this.setSource(source);
			this.setTarget(graph.getNode(i));

			if (source.getId() == target.getId()) {
				continue;
			}

			initializeQueue(source, forwardsUnsettleNodes);
			initializeQueue(target, backwardsUnsettleNodes);

			forwardsUnsettleNodesAux.put(source.getId(), 0);
			backwardsUnsettleNodesAux.put(target.getId(), 0);

			forwardsRemovedNode = forwardsUnsettleNodes.peek();
			backwardsRemovedNode = backwardsUnsettleNodes.peek();

			forwardsParentNodes.put(source.getId(), new RouteEntry(-1, 0, -1, null));
			backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));

			while (!forwardsUnsettleNodes.isEmpty() || !backwardsUnsettleNodes.isEmpty()) {

				// Condition to alternate between forward and backward search
				if (forwardsUnsettleNodes.isEmpty()) {
					backwardSearch();
				} else if (backwardsUnsettleNodes.isEmpty()) {
					forwardSearch();
				} else {
					if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {
						forwardSearch();
					} else {
						backwardSearch();
					}
				}

				if (path != null) {

					System.out.println("Path  found between (" + source.getId() + ") and (" + target.getId() + ")");

					break;

				}

			}

			if (path == null) {
				System.out.println("Path not found between (" + source.getId() + ") and (" + target.getId() + ")");
				break;
			}

		}

	}

}
