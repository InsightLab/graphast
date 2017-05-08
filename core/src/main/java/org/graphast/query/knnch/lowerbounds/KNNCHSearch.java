package org.graphast.query.knnch.lowerbounds;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.geometry.PoI;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class KNNCHSearch {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CHGraph graph;

	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Queue<DistanceEntry> lowerBoundDistanceSourcePoIs = new PriorityQueue<>();

	private Map<Long, PriorityQueue<DistanceEntry>> backwardUnsettleNodesHash = new HashMap<>();
	private Map<Long, Map<Long, Integer>> backwardSettleNodesHash = new HashMap<>();
	private Map<Long, DistanceEntry> meetingNodeHash = new HashMap<>();
	private Map<Long, Path> pathHash = new HashMap<>();
	private Map<Long, HashMap<Long, RouteEntry>> parentsHash = new HashMap<>();

	private DistanceEntry nextCandidateNNLowerBound = new DistanceEntry(-1, -1, -1);
	// TODO Criar metodos para adicionar pois na estrutura poisFound

	private CHNode source;
	private CHNode target;

	private int expandedNodesForwardSearch = 0;
	private int expandedNodesBackwardSearch = 0;

	private int forwardsSmallerDistanceForThisIteration = 0;
	private int backwardsSmallerDistanceForThisIteration = 0;

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Path path;
	Queue<Path> finalResult = new PriorityQueue<>();

	PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
	PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
	HashMap<Long, Integer> forwardsUnsettleNodesAux = new HashMap<>();
	HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

	HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
	Map<Long, Integer> backwardsSettleNodes = new HashMap<>();

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);

	// --METRICS VARIABLES
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfRegularSearches = 0;

	public KNNCHSearch(CHGraph graph) {
		this.graph = graph;
	}

	/**
	 * This method will set the distanceSourcePoIs variable with the distance
	 * from each PoI to the source node.
	 * 
	 * @param source
	 *            source node of the query.
	 */
	private void createLowerBounds(Long source) {

		for (PoI poi : graph.getPOIs()) {
			long nodeId = graph.getNodeId(poi.getLatitude(), poi.getLongitude());
			Node node = graph.getNode(nodeId);

			// TODO Double check the units in for this distance (this cast may
			// not be totally correct).
			// Use this distance for real world maps.
			// int distance = (int)
			// DistanceUtils.distanceLatLong(graph.getNode(source).getLatitude(),
			// graph.getNode(source).getLongitude(), node.getLatitude(),
			// node.getLongitude());
			int distance = (int) Math.sqrt(Math.pow((node.getLatitude() - graph.getNode(source).getLatitude()), 2)
					+ Math.pow((node.getLongitude() - graph.getNode(source).getLongitude()), 2));

			DistanceEntry lowerBound = new DistanceEntry(nodeId, distance, -1);
			lowerBoundDistanceSourcePoIs.add(lowerBound);
		}

	}

	public Queue<Path> search(CHNode source, int k) {

		this.smallerDistancePoI = smallerDistancePoI;

		initializeQueue(source, forwardsUnsettleNodes);

		forwardsUnsettleNodesAux.put(source.getId(), 0);

		forwardsParentNodes.put(source.getId(), new RouteEntry(-1, 0, -1, null));

		createLowerBounds(source.getId());

		for (int i = 0; i < k; i++) {

			PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();

			CHNode target = graph.getNode(lowerBoundDistanceSourcePoIs.poll().getId());
			initializeQueue(target, backwardsUnsettleNodes);

			this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));

			backwardUnsettleNodesHash.put(target.getId(), backwardsUnsettleNodes);

			Map<Long, Integer> backwardsSettleNodes = new HashMap<>();
			backwardSettleNodesHash.put(target.getId(), backwardsSettleNodes);

			meetingNodeHash.put(target.getId(), new DistanceEntry(-1, Integer.MAX_VALUE, -1));

			HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();
			backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));
			parentsHash.put(target.getId(), backwardsParentNodes);
		}

		// If the number of nearest nodes that we need is equal to the number of
		// PoIs in the graph,
		// there is no nextCandidate: all PoI's will be considered!
		if (graph.getPOIs().size() < k) {
			nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
			nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
		}

		StopWatch knnSW = new StopWatch();

		knnSW.start();

		while (!backwardUnsettleNodesHash.isEmpty()) {

			if ((graph.getPOIs().size() < k)
					&& smallerDistancePoI.peek().getDistance() >= nextCandidateNNLowerBound.getDistance()) {
				PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();

				CHNode target = graph.getNode(nextCandidateNNLowerBound.getId());
				initializeQueue(target, backwardsUnsettleNodes);
				backwardUnsettleNodesHash.put(nextCandidateNNLowerBound.getId(), backwardsUnsettleNodes);

				meetingNodeHash.put(lowerBoundDistanceSourcePoIs.poll().getId(),
						new DistanceEntry(-1, Integer.MAX_VALUE, -1));

				this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));

				lowerBoundDistanceSourcePoIs.poll();
				nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
				nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
			}

			Long currentPoI = smallerDistancePoI.poll().getId();
			backwardsUnsettleNodes = backwardUnsettleNodesHash.get(currentPoI);
			backwardsSettleNodes = backwardSettleNodesHash.get(currentPoI);
			backwardsParentNodes = parentsHash.get(currentPoI);
			meetingNode = meetingNodeHash.get(currentPoI);
			target = graph.getNode(currentPoI);

			if(backwardsUnsettleNodes == null)
				continue;
			
//			 logger.info("PoI that will be analyzed: {}", currentPoI);
			// logger.info("Number of PoIs being considered: {}",
			// dijkstraHash.size());

			if (forwardsUnsettleNodes.isEmpty() && backwardsUnsettleNodes.isEmpty()) {
				// Add this node to a specific queue
				// logger.info("Path NOT found");
				backwardUnsettleNodesHash.remove(currentPoI);
			} else {

				// Condition to alternate between forward and backward search
				if (forwardsUnsettleNodes.isEmpty()) {
					Path nearestNeighborPath = backwardSearch();
					if (nearestNeighborPath != null) {
						finalResult.add(nearestNeighborPath);
					}

				} else if (backwardsUnsettleNodes.isEmpty()) {
					Path nearestNeighborPath = forwardSearch();
					if (nearestNeighborPath != null) {
						finalResult.add(nearestNeighborPath);
					}
				} else {
					if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {
						Path nearestNeighborPath = forwardSearch();
						if (nearestNeighborPath != null) {
							finalResult.add(nearestNeighborPath);
						}
					} else {
						Path nearestNeighborPath = backwardSearch();
						if (nearestNeighborPath != null) {
							finalResult.add(nearestNeighborPath);
						}
					}
				}
			}
		}

		knnSW.stop();

		// logger.info("Execution Time of lower bound kNN: {}ms",
		// knnSW.getNanos());
		// logger.info("Number of expanded nodes in the forward search: {}",
		// expandedNodesForwardSearch);
		// logger.info("Number of expanded nodes in the backward search: {}",
		// expandedNodesBackwardSearch);

		return finalResult;

	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private Path forwardSearch() {
		Path path = null;
		// Stopping criteria of Bidirectional search
		if (backwardsUnsettleNodes.isEmpty()) {

			if (meetingNode.getDistance() != Integer.MAX_VALUE && forwardsUnsettleNodes.peek().getDistance()
					+ backwardsSettleNodes.get(meetingNode.getId()) >= meetingNode.getDistance()) {

				HashMap<Long, RouteEntry> resultParentNodes;
				path = new Path();
				resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
				path.constructPath(target.getId(), resultParentNodes, graph);

				pathHash.put(target.getId(), path);
				backwardUnsettleNodesHash.remove(target.getId());
				return path;

			}

		} else if (forwardsUnsettleNodes.peek().getDistance()
				+ backwardsUnsettleNodes.peek().getDistance() >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			pathHash.put(target.getId(), path);
			backwardUnsettleNodesHash.remove(target.getId());

			return path;

		}

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsUnsettleNodesAux.remove(forwardsRemovedNode.getId());
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		numberOfForwardSettleNodes++;

		// logger.debug("Node being analyzed: {}", forwardsRemovedNode.getId());

		expandVertexForward();

		expandedNodesForwardSearch++;

		return path;

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		verifyMeetingNodeForwardSearch(forwardsRemovedNode.getId(), neighbors, true);

		int backupDistance = forwardsSmallerDistanceForThisIteration;

		forwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				// logger.debug("Neighbor not being analyzed: {}", vid);
				continue;
			}

			// logger.debug("Neighbor being analyzed: {}", vid);
			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + forwardsRemovedNode.getDistance(),
					forwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!forwardsSettleNodes.containsKey(vid)) {

				forwardsUnsettleNodes.offer(newEntry);
				forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
				forwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);

				forwardsParentNodes.put(vid,
						new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				if (forwardsSmallerDistanceForThisIteration > neighbors.get(vid) + forwardsRemovedNode.getDistance()) {
					forwardsSmallerDistanceForThisIteration = neighbors.get(vid) + forwardsRemovedNode.getDistance();
				}

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
					forwardsParentNodes.put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

					if (forwardsSmallerDistanceForThisIteration > distance) {
						forwardsSmallerDistanceForThisIteration = distance;
					}

				} else {
					forwardsSmallerDistanceForThisIteration = backupDistance;
				}
			}

			verifyMeetingNodeForwardSearch(vid, neighbors, false);

		}
		smallerDistancePoI.add(new DistanceEntry(target.getId(),
				forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));
	}

	private void verifyMeetingNodeForwardSearch(long vid, Long2IntMap neighbors, boolean test) {

		if (test) {
			if (backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())
					+ backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
				meetingNode.setId(vid);
				meetingNode.setDistance(
						forwardsSettleNodes.get(forwardsRemovedNode.getId()) + backwardsSettleNodes.get(vid));
				meetingNode.setParent(forwardsRemovedNode.getId());
			}
		} else {
			if (backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())
					+ neighbors.get(vid) + backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
				meetingNode.setId(vid);
				meetingNode.setDistance(forwardsSettleNodes.get(forwardsRemovedNode.getId()) + neighbors.get(vid)
						+ backwardsSettleNodes.get(vid));
				meetingNode.setParent(forwardsRemovedNode.getId());
			}
		}

	}

	private Path backwardSearch() {
		Path path = null;
		// Stopping criteria of Bidirectional search
		if (forwardsUnsettleNodes.isEmpty()) {
			if (meetingNode.getDistance() != Integer.MAX_VALUE && backwardsUnsettleNodes.peek().getDistance()
					+ forwardsSettleNodes.get(meetingNode.getId()) >= meetingNode.getDistance()) {

				HashMap<Long, RouteEntry> resultParentNodes;
				path = new Path();
				resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
				path.constructPath(target.getId(), resultParentNodes, graph);

				pathHash.put(target.getId(), path);
				backwardUnsettleNodesHash.remove(target.getId());
				return path;

			}
		} else {

			if (forwardsUnsettleNodes.peek().getDistance() + backwardsUnsettleNodes.peek().getDistance() >= meetingNode
					.getDistance()) {

				HashMap<Long, RouteEntry> resultParentNodes;
				path = new Path();
				resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
				path.constructPath(target.getId(), resultParentNodes, graph);

				pathHash.put(target.getId(), path);
				backwardUnsettleNodesHash.remove(target.getId());

				return path;

			}
		}

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsUnsettleNodesAux.remove(backwardsRemovedNode.getId());
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		numberOfBackwardSettleNodes++;

		expandedNodesBackwardSearch++;

		expandVertexBackward();

		return path;

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		verifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId(), neighbors, true);

		int backupDistance = backwardsSmallerDistanceForThisIteration;
		backwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				// verifyMeetingNodeBackwardSearch(vid, neighbors);
				// logger.debug("Neighbor not being analyzed: {}", vid);
				continue;
			}

			// logger.debug("Neighbor being analyzed: {}", vid);

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + backwardsRemovedNode.getDistance(),
					backwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!backwardsSettleNodes.containsKey(vid)) {

				backwardsUnsettleNodes.offer(newEntry);
				backwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
				backwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);

				backwardsParentNodes.put(vid,
						new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid)
						+ backwardsRemovedNode.getDistance()) {
					backwardsSmallerDistanceForThisIteration = neighbors.get(vid) + backwardsRemovedNode.getDistance();
				}

			} else {

				int cost = backwardsSettleNodes.get(vid);
				distance = backwardsRemovedNode.getDistance() + neighbors.get(vid);

				if (cost > distance) {
					backwardsUnsettleNodes.remove(newEntry);
					backwardsUnsettleNodes.offer(newEntry);

					backwardsUnsettleNodesAux.remove(newEntry.getId());
					backwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());

					backwardsSettleNodes.remove(newEntry.getId());
					backwardsSettleNodes.put(newEntry.getId(), distance);

					backwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

					if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid)
							+ backwardsRemovedNode.getDistance()) {
						backwardsSmallerDistanceForThisIteration = neighbors.get(vid)
								+ backwardsRemovedNode.getDistance();
					}

				} else {
					backwardsSmallerDistanceForThisIteration = backupDistance;
				}
			}

			verifyMeetingNodeBackwardSearch(vid, neighbors, false);

		}
		smallerDistancePoI.add(new DistanceEntry(target.getId(),
				forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));

	}

	private void verifyMeetingNodeBackwardSearch(long vid, Long2IntMap neighbors, boolean test) {

		if (test) {
			if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())
					+ forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
				meetingNode.setId(vid);
				meetingNode.setDistance(
						backwardsSettleNodes.get(backwardsRemovedNode.getId()) + forwardsSettleNodes.get(vid));
				meetingNode.setParent(backwardsRemovedNode.getId());
			}
		} else {
			if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())
					+ neighbors.get(vid) + forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
				meetingNode.setId(vid);
				meetingNode.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + neighbors.get(vid)
						+ forwardsSettleNodes.get(vid));
				meetingNode.setParent(backwardsRemovedNode.getId());
			}
		}

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		RouteEntry nextForwardParent = forwardsParentNodes.get(meetingNode.getId());

		if (nextForwardParent != null) {

			if (forwardsParentNodes.get(meetingNode.getId()) == null) {
				forwardsParentNodes.put(meetingNode.getId(), backwardsParentNodes.get(meetingNode.getId()));
			}

			resultListOfParents.put(meetingNode.getId(), nextForwardParent);

			while (forwardsParentNodes.get(nextForwardParent.getId()) != null) {

				resultListOfParents.put(nextForwardParent.getId(), forwardsParentNodes.get(nextForwardParent.getId()));

				nextForwardParent = forwardsParentNodes.get(nextForwardParent.getId());

			}

		}

		if (!backwardsParentNodes.isEmpty()) {

			if (backwardsParentNodes.get(meetingNode.getId()) == null) {
				backwardsParentNodes.put(meetingNode.getId(), forwardsParentNodes.get(meetingNode.getId()));
			}

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

}
