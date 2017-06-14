package org.graphast.query.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.knn.bidirectionalknn.SearchEntry;
import org.graphast.query.route.shortestpath.bidirectionalastar.LowerBoundDistanceEntry;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Instruction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.util.DistanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalKNNSearch {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CHGraph graph;

	private Queue<SearchEntry> searchEntryQueue = new PriorityQueue<>();
	SearchEntry forwardSearch;

	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Queue<DistanceEntry> lowerBoundDistanceSourcePoIs = new PriorityQueue<>();
	private Queue<DistanceEntry> smallerSettleNodes = new PriorityQueue<>(Collections.reverseOrder());

	private Map<Long, PriorityQueue<DistanceEntry>> backwardUnsettleNodesHash = new HashMap<>();
	private Map<Long, HashMap<Long, Integer>> backwardsUnsettleNodesAuxHash = new HashMap<>();
	private Map<Long, DistanceEntry> meetingNodeHash = new HashMap<>();
	private Map<Long, HashMap<Long, RouteEntry>> parentsHash = new HashMap<>();

	private DistanceEntry nextCandidateNNLowerBound = new DistanceEntry(-1, Integer.MAX_VALUE, -1);
	// TODO Criar metodos para adicionar pois na estrutura poisFound

	private CHNode source;
	private CHNode target;

	private Long currentPoI = -1l;
	private int kIterator = 1;

	private Map<Integer, SearchEntry> nearestNeighborMap = new HashMap<>();

	int startCurr = 0;
	int endCurr = 0;
	Map<Long, Integer> startCurrList = new HashMap<>();
	Map<Long, Integer> endCurrList = new HashMap<>();

	private int numberOfPoIs;

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Path path;
	Queue<Path> finalResult = new PriorityQueue<>();

	Map<Long, List<Long>> backwardUnsettlePossibleMeetingPoint = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);

	// --METRICS VARIABLES
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfRegularSearches = 0;

	public BidirectionalKNNSearch(CHGraph graph) {
		this.graph = graph;
		this.numberOfPoIs = graph.getPOIs().size();
	}

	/**
	 * This method will set the distanceSourcePoIs variable with the distance
	 * from each PoI to the source node.
	 * 
	 * @param source
	 *            source node of the query.
	 */
	private void createLowerBounds(Long source) {

		for (Node node : graph.getPOIsNodes()) {

			// TODO Double check the units in for this distance (this cast may
			// not be totally correct).
			// Use this distance for real world maps.
			// int distance = (int)
			// DistanceUtils.distanceLatLong(graph.getNode(source).getLatitude(),
			// graph.getNode(source).getLongitude(), node.getLatitude(),
			// node.getLongitude());
			int distance = (int) Math.sqrt(Math.pow((node.getLatitude() - graph.getNode(source).getLatitude()), 2)
					+ Math.pow((node.getLongitude() - graph.getNode(source).getLongitude()), 2));

			DistanceEntry lowerBound = new DistanceEntry(node.getId(), distance, -1);
			lowerBoundDistanceSourcePoIs.add(lowerBound);
		}

	}

	public Queue<Path> search(CHNode source, int k) {

		this.source = source;

		createLowerBounds(source.getId());

		initializeForwardSearchStructure();

		for (int i = 0; i < k; i++) {
			initializeBackwardSearchStructure();
		}

		// TODO Double check this
		// Updating the next candidate for nearest neighbor
		if (numberOfPoIs > k) {
			nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
			nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
		}

		StopWatch knnSW = new StopWatch();

		knnSW.start();

		while (!searchEntryQueue.isEmpty() && kIterator < k + 1) {

			if (searchEntryQueue.peek().getUnsettleNodes().peek().getLowerBound() > nextCandidateNNLowerBound.getDistance()) {

				CHNode target = graph.getNode(nextCandidateNNLowerBound.getId());

				SearchEntry searchEntry = new SearchEntry(target, source);

				searchEntryQueue.offer(searchEntry);

				lowerBoundDistanceSourcePoIs.poll();

				if (lowerBoundDistanceSourcePoIs.peek() != null) {
					nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
					nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
				} else {
					nextCandidateNNLowerBound.setId(-1l);
					nextCandidateNNLowerBound.setDistance(Integer.MAX_VALUE);
				}

			}

			currentPoI = searchEntryQueue.peek().getSource().getId();

			// TODO Review this case
			if (currentPoI == source.getId()) {

				searchEntryQueue.peek().setMeetingNode(new DistanceEntry(currentPoI, 0, currentPoI));

				nearestNeighborMap.put(kIterator, searchEntryQueue.poll());
				kIterator++;
				continue;
			}

			forwardsRemovedNode = null;

			if (!finished()) {

				logger.debug("PoI that will be analyzed: {}", graph.getNode(currentPoI).getExternalId());

				if (!forwardSearch.getUnsettleNodes().isEmpty())
					forwardSearch();
				
				if(forwardsRemovedNode != null && forwardsRemovedNode.getId() == searchEntryQueue.peek().getSource().getId()) {
					nearestNeighborMap.put(kIterator, searchEntryQueue.poll());
					kIterator++;
					continue;
				}

				if (!searchEntryQueue.peek().getUnsettleNodes().isEmpty())
					backwardSearch();
				
				if(backwardsRemovedNode.getId() == forwardSearch.getSource().getId()) {
					nearestNeighborMap.put(kIterator, searchEntryQueue.poll());
					kIterator++;
					continue;
				}
				
				SearchEntry transitionElement = searchEntryQueue.poll();
				searchEntryQueue.add(transitionElement);
				

			}

		}

		knnSW.stop();

		for (int i = 1; i < kIterator; i++) {

			path = new Path();

			if (nearestNeighborMap.get(i).getMeetingNode().getId() == -1) {
				Instruction instruction = new Instruction(target.getId().intValue(),
						"PATH NOT FOUND BETWEEN " + source.getId() + " AND " + target.getId(), 0, 0);
				List<Instruction> instructions = new ArrayList<>();
				instructions.add(instruction);
				path.setInstructions(instructions);
				List<Long> edges = new ArrayList<>();
				path.setEdges(edges);

				finalResult.add(path);

				continue;
			}

			HashMap<Long, RouteEntry> resultParentNodes = joinParents(nearestNeighborMap.get(i).getMeetingNode(),
					forwardSearch.getParentNodes(), (HashMap<Long, RouteEntry>) nearestNeighborMap.get(i).getParentNodes());
			path.constructPath(nearestNeighborMap.get(i).getSource().getId(), resultParentNodes, graph);

			finalResult.add(path);

		}

		return finalResult;

	}

	private boolean finished() {

		for (SearchEntry iteratedSearchEntry : searchEntryQueue) {

			if (forwardSearch.getUnsettleNodes().isEmpty() && iteratedSearchEntry.getUnsettleNodes().isEmpty()) {

				nearestNeighborMap.put(kIterator, iteratedSearchEntry);
				searchEntryQueue.remove(iteratedSearchEntry);
				kIterator++;
				return true;

			}

			DistanceEntry meetingNode = iteratedSearchEntry.getMeetingNode();

			if(forwardSearch.getUnsettleNodes().peek() == null) {
				if(iteratedSearchEntry.getUnsettleNodes().peek().getDistance() >= meetingNode.getDistance()) {
					nearestNeighborMap.put(kIterator, iteratedSearchEntry);
					searchEntryQueue.remove(iteratedSearchEntry);
					kIterator++;
					return true;
				}
			} else {
				if (forwardSearch.getUnsettleNodes().peek().getDistance() >= meetingNode.getDistance()
						&& iteratedSearchEntry.getUnsettleNodes().peek().getDistance() >= meetingNode.getDistance()) {

					nearestNeighborMap.put(kIterator, iteratedSearchEntry);
					searchEntryQueue.remove(iteratedSearchEntry);
					kIterator++;
					return true;

				}
			}
			
			

		}

		return false;

	}

	/**
	 * Initialize the necessary structures for the Forward Search.
	 */
	private void initializeForwardSearchStructure() {

		CHNode target = graph.getNode(lowerBoundDistanceSourcePoIs.peek().getId());

		forwardSearch = new SearchEntry(source, target);

	}

	/**
	 * Initialize the necessary structures for the Backward Search.
	 */
	private void initializeBackwardSearchStructure() {

		CHNode target = graph.getNode(lowerBoundDistanceSourcePoIs.poll().getId());
		// TODO change the names from source and target latter
		SearchEntry searchEntry = new SearchEntry(target, source);

		searchEntryQueue.offer(searchEntry);

	}

	private void forwardSearch() {

		forwardsRemovedNode = forwardSearch.getUnsettleNodes().poll();

		forwardSearch.getSettleNodes().put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

		logger.debug("\t[FORWARD] Node being analyzed: {}. Distance: {}",
				graph.getNode(forwardsRemovedNode.getId()).getExternalId(), forwardsRemovedNode.getDistance());

		expandVertexForward();
		
	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				logger.debug("\t\t[IGNORED] Node: {}", graph.getNode(vid).getExternalId());
				continue;
			}

			Node lowerBoundSource = graph.getNode(vid);
			Node lowerBoundTarget = searchEntryQueue.peek().getSource();

			double lowerBound = neighbors.get(vid) + forwardsRemovedNode.getDistance()
					+ Math.sqrt(Math.pow((lowerBoundTarget.getLatitude() - lowerBoundSource.getLatitude()), 2)
							+ Math.pow((lowerBoundTarget.getLongitude() - lowerBoundSource.getLongitude()), 2));

			LowerBoundDistanceEntry newEntry = new LowerBoundDistanceEntry(vid,
					neighbors.get(vid) + forwardsRemovedNode.getDistance(), lowerBound, forwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (forwardSearch.getSettleNodes().containsKey(newEntry.getId())) {
				continue;
			}
			
			logger.debug("\t\tNode expanded: {}. Distance: {}", graph.getNode(vid).getExternalId(),
					newEntry.getDistance());
			
			if (!forwardSearch.getForwardUnsettleNodesAux().containsKey(vid)) {

				forwardSearch.getUnsettleNodes().offer(newEntry);
				forwardSearch.getForwardUnsettleNodesAux().put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);

				if (forwardSearch.getParentNodes().containsKey(vid)) {
					if (forwardSearch.getParentNodes().get(vid).getCost() > distance) {
						forwardSearch.getParentNodes().put(vid,
								new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));
					}
				} else {

					forwardSearch.getParentNodes().put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}

			} else {

				int cost = forwardSearch.getForwardUnsettleNodesAux().get(vid);
				distance = neighbors.get(vid) + forwardsRemovedNode.getDistance();

				if (cost > distance) {
					forwardSearch.getUnsettleNodes().remove(newEntry);
					forwardSearch.getForwardUnsettleNodesAux().remove(newEntry.getId());
					forwardSearch.getUnsettleNodes().offer(newEntry);
					forwardSearch.getForwardUnsettleNodesAux().put(newEntry.getId(), newEntry.getDistance());

					forwardSearch.getParentNodes().remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);
					forwardSearch.getParentNodes().put(vid,
							new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}

			verifyMeetingNodeForwardSearch(forwardsRemovedNode.getId(), vid, neighbors);

		}

	}

	private void verifyMeetingNodeForwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (searchEntryQueue.peek().getBackwardUnsettleNodesAux().containsKey(vid)
				&& forwardSearch.getSettleNodes().get(removed) + neighbors.get(vid)
						+ searchEntryQueue.peek().getBackwardUnsettleNodesAux().get(vid) < searchEntryQueue.peek()
								.getMeetingNode().getDistance()) {
			searchEntryQueue.peek().getMeetingNode().setId(vid);
			searchEntryQueue.peek().getMeetingNode().setDistance(forwardSearch.getSettleNodes().get(removed)
					+ neighbors.get(vid) + searchEntryQueue.peek().getBackwardUnsettleNodesAux().get(vid));
			searchEntryQueue.peek().getMeetingNode().setParent(forwardsRemovedNode.getId());

			logger.debug("\t\t\tMeeting node: {}", searchEntryQueue.peek().getMeetingNode());
			
			// System.out.println("\t\t\tMeeting node: " +
			// graph.getNode(meetingNode.getId()).getExternalId());
		}
		
		if(searchEntryQueue.peek().getSettleNodes().containsKey(vid) && forwardSearch.getSettleNodes().get(removed) + neighbors.get(vid) + searchEntryQueue.peek().getSettleNodes().get(vid) < searchEntryQueue.peek()
				.getMeetingNode().getDistance()) {
			searchEntryQueue.peek().getMeetingNode().setId(vid);
			searchEntryQueue.peek().getMeetingNode().setDistance(forwardSearch.getSettleNodes().get(removed) + neighbors.get(vid) + searchEntryQueue.peek().getSettleNodes().get(vid));
			searchEntryQueue.peek().getMeetingNode().setParent(forwardsRemovedNode.getId());

			logger.debug("\t\t\tMeeting node: {}", searchEntryQueue.peek().getMeetingNode());
		}

	}

	private void backwardSearch() {

		backwardsRemovedNode = searchEntryQueue.peek().getUnsettleNodes().poll();

		searchEntryQueue.peek().getSettleNodes().put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());

		logger.debug("\t[BACKWARD] Node being analyzed: {}. Distance: {}",
				graph.getNode(backwardsRemovedNode.getId()).getExternalId(), backwardsRemovedNode.getDistance());

		expandVertexBackward();
		
	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				// verifyMeetingNodeBackwardSearch(vid, neighbors);
				logger.debug("\t\t[IGNORED] Node: {}", graph.getNode(vid).getExternalId());
				continue;
			}

			Node lowerBoundSource = graph.getNode(vid);
			Node lowerBoundTarget = forwardSearch.getSource();

			double lowerBound = neighbors.get(vid) + backwardsRemovedNode.getDistance()
					+ Math.sqrt(Math.pow((lowerBoundTarget.getLatitude() - lowerBoundSource.getLatitude()), 2)
							+ Math.pow((lowerBoundTarget.getLongitude() - lowerBoundSource.getLongitude()), 2));

			LowerBoundDistanceEntry newEntry = new LowerBoundDistanceEntry(vid,
					neighbors.get(vid) + backwardsRemovedNode.getDistance(), lowerBound, backwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (searchEntryQueue.peek().getBackwardUnsettleNodesAux().containsKey(newEntry.getId())) {
				continue;
			}
			
			logger.debug("\t\tNode expanded: {}. Distance: {}", graph.getNode(vid).getExternalId(),
					newEntry.getDistance());
			
			if (!searchEntryQueue.peek().getBackwardUnsettleNodesAux().containsKey(vid)) {

				searchEntryQueue.peek().getUnsettleNodes().offer(newEntry);
				searchEntryQueue.peek().getBackwardUnsettleNodesAux().put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);

				if (searchEntryQueue.peek().getParentNodes().containsKey(vid)) {
					if (searchEntryQueue.peek().getParentNodes().get(vid).getCost() > distance) {
						searchEntryQueue.peek().getParentNodes().put(vid,
								new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));
					}
				} else {

					searchEntryQueue.peek().getParentNodes().put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}

			} else {

				int cost = searchEntryQueue.peek().getBackwardUnsettleNodesAux().get(vid);
				distance = backwardsRemovedNode.getDistance() + neighbors.get(vid);

				if (cost > distance) {
					searchEntryQueue.peek().getUnsettleNodes().remove(newEntry);
					searchEntryQueue.peek().getUnsettleNodes().offer(newEntry);

					searchEntryQueue.peek().getBackwardUnsettleNodesAux().remove(newEntry.getId());
					searchEntryQueue.peek().getBackwardUnsettleNodesAux().put(newEntry.getId(), newEntry.getDistance());

					searchEntryQueue.peek().getParentNodes().remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					searchEntryQueue.peek().getParentNodes().put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}

			}

			verifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId(), vid, neighbors);

		}

	}

	private void verifyMeetingNodeBackwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (forwardSearch.getForwardUnsettleNodesAux().containsKey(vid)
				&& searchEntryQueue.peek().getSettleNodes().get(removed) + neighbors.get(vid)
						+ forwardSearch.getForwardUnsettleNodesAux().get(vid) < searchEntryQueue.peek()
								.getMeetingNode().getDistance()) {
			searchEntryQueue.peek().getMeetingNode().setId(vid);
			searchEntryQueue.peek().getMeetingNode().setDistance(searchEntryQueue.peek().getSettleNodes().get(removed)
					+ neighbors.get(vid) + forwardSearch.getForwardUnsettleNodesAux().get(vid));
			searchEntryQueue.peek().getMeetingNode().setParent(backwardsRemovedNode.getId());

			logger.debug("\t\t\tMeeting node: {}", searchEntryQueue.peek().getMeetingNode());
			
			// System.out.println("\t\t\tMeeting node: " +
			// graph.getNode(meetingNode.getId()).getExternalId());
		}
		
		if (forwardSearch.getSettleNodes().containsKey(vid) && searchEntryQueue.peek().getSettleNodes().get(removed) + neighbors.get(vid) + forwardSearch.getSettleNodes().get(vid) < searchEntryQueue.peek()
				.getMeetingNode().getDistance()) {
			searchEntryQueue.peek().getMeetingNode().setId(vid);
			searchEntryQueue.peek().getMeetingNode().setDistance(searchEntryQueue.peek().getSettleNodes().get(removed) + neighbors.get(vid) + forwardSearch.getSettleNodes().get(vid));
			searchEntryQueue.peek().getMeetingNode().setParent(backwardsRemovedNode.getId());

			logger.debug("\t\t\tMeeting node: {}", searchEntryQueue.peek().getMeetingNode());
		}

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode, Map<Long, RouteEntry> forwardsParentNodes,
			HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		long currrentNodeId = meetingNode.getId();
		RouteEntry nextParent = null;
		
		if(forwardsParentNodes.get(currrentNodeId) != null) {
			nextParent = forwardsParentNodes.get(currrentNodeId);
			resultListOfParents.put(currrentNodeId, nextParent);
			currrentNodeId = nextParent.getId();
		}
		
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

	private double calculateCurrentLowerBound() {

		
		CHNode lowerBoundSource = source;
		CHNode lowerBoundTarget = graph.getNode(searchEntryQueue.peek().getUnsettleNodes().peek().getId());

		double lowerBound = Math.sqrt(Math.pow((lowerBoundTarget.getLatitude() - lowerBoundSource.getLatitude()), 2)
						+ Math.pow((lowerBoundTarget.getLongitude() - lowerBoundSource.getLongitude()), 2))
				+ searchEntryQueue.peek().getUnsettleNodes().peek().getDistance();

		logger.debug("LOWER BOUND from {} to {} ({}): {}", lowerBoundSource.getId(), lowerBoundTarget.getId(), searchEntryQueue.peek().getSource().getId(), lowerBound);
		
		logger.debug("{} == {}?", lowerBound, searchEntryQueue.peek().getUnsettleNodes().peek().getLowerBound());
		
		return lowerBound;
		
		
//		CHNode lowerBoundSource = graph.getNode(forwardSearch.getUnsettleNodes().peek().getId());
//		CHNode lowerBoundTarget = graph.getNode(searchEntryQueue.peek().getUnsettleNodes().peek().getId());
//
//		int lowerBound = forwardSearch.getUnsettleNodes().peek().getDistance()
//				+ (int) Math.sqrt(Math.pow((lowerBoundTarget.getLatitude() - lowerBoundSource.getLatitude()), 2)
//						+ Math.pow((lowerBoundTarget.getLongitude() - lowerBoundSource.getLongitude()), 2))
//				+ searchEntryQueue.peek().getUnsettleNodes().peek().getDistance();
//
//		return lowerBound;
	}

}
