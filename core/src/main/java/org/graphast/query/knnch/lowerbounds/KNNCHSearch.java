package org.graphast.query.knnch.lowerbounds;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.graphast.query.model.Entry;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Instruction;
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
	private Map<Long, Integer> smallerDistancePoIHash = new HashMap<>();
	private Queue<DistanceEntry> lowerBoundDistanceSourcePoIs = new PriorityQueue<>();

	private Map<Long, PriorityQueue<DistanceEntry>> backwardUnsettleNodesHash = new HashMap<>();
	private Map<Long, HashMap<Long, Integer>> backwardsUnsettleNodesAuxHash = new HashMap<>();
	private Map<Long, Map<Long, Integer>> backwardSettleNodesHash = new HashMap<>();
	private Map<Long, DistanceEntry> meetingNodeHash = new HashMap<>();
	private Map<Long, Path> pathHash = new HashMap<>();
	private Map<Long, HashMap<Long, RouteEntry>> parentsHash = new HashMap<>();

	private DistanceEntry nextCandidateNNLowerBound = new DistanceEntry(-1, -1, -1);
	// TODO Criar metodos para adicionar pois na estrutura poisFound

	private CHNode source;
	private CHNode target;
	
	private Long currentPoI = -1l;
	private int kIterator = 1;
	private int iteratorResult = 1;
	
	private Map<Integer, Long> nearestNeighborMap = new HashMap<>();

	int startCurr = 0;
	int endCurr = 0;
	Map<Long, Integer> startCurrList = new HashMap<>();
	Map<Long, Integer> endCurrList = new HashMap<>();

	private int numberOfPoIs;

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

		this.source = source;
		// this.smallerDistancePoI = smallerDistancePoI;

		initializeQueue(source, forwardsUnsettleNodes);

		forwardsUnsettleNodesAux.put(source.getId(), 0);

		forwardsParentNodes.put(source.getId(), new RouteEntry(-1, 0, -1, null));

		createLowerBounds(source.getId());

		// Inicializando atributos para cada ponto de interesse
		for (int i = 0; i < k; i++) {

			PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
			HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

			CHNode target = graph.getNode(lowerBoundDistanceSourcePoIs.poll().getId());
			initializeQueue(target, backwardsUnsettleNodes);
			backwardsUnsettleNodesAux.put(target.getId(), 0);

			this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));
			this.smallerDistancePoIHash.put(target.getId(), Integer.MAX_VALUE);

			backwardUnsettleNodesHash.put(target.getId(), backwardsUnsettleNodes);
			backwardsUnsettleNodesAuxHash.put(target.getId(), backwardsUnsettleNodesAux);

			Map<Long, Integer> backwardsSettleNodes = new HashMap<>();
			backwardSettleNodesHash.put(target.getId(), backwardsSettleNodes);

			meetingNodeHash.put(target.getId(), new DistanceEntry(-1, Integer.MAX_VALUE, -1));
			startCurrList.put(target.getId(), 0);
			endCurrList.put(target.getId(), 0);

			HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();
			backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));
			parentsHash.put(target.getId(), backwardsParentNodes);
		}

		// If the number of nearest nodes that we need is equal to the number of
		// PoIs in the graph,
		// there is no nextCandidate: all PoI's will be considered!
		if (numberOfPoIs < k) {
			nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
			nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
		}

		StopWatch knnSW = new StopWatch();

		knnSW.start();

		while (!backwardUnsettleNodesHash.isEmpty()) {

			if ((numberOfPoIs < k)
					&& smallerDistancePoI.peek().getDistance() >= nextCandidateNNLowerBound.getDistance()) {
				PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
				HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

				CHNode target = graph.getNode(nextCandidateNNLowerBound.getId());
				initializeQueue(target, backwardsUnsettleNodes);
				backwardsUnsettleNodesAux.put(target.getId(), 0);

				backwardUnsettleNodesHash.put(nextCandidateNNLowerBound.getId(), backwardsUnsettleNodes);
				backwardsUnsettleNodesAuxHash.put(nextCandidateNNLowerBound.getId(), backwardsUnsettleNodesAux);

				meetingNodeHash.put(lowerBoundDistanceSourcePoIs.poll().getId(),
						new DistanceEntry(-1, Integer.MAX_VALUE, -1));

				this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));
				this.smallerDistancePoIHash.put(target.getId(), Integer.MAX_VALUE);

				lowerBoundDistanceSourcePoIs.poll();
				nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
				nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
			}

			currentPoI = smallerDistancePoI.poll().getId();
			
			if(currentPoI == source.getId()) {
				backwardUnsettleNodesHash.remove(currentPoI);
				kIterator++;
				iteratorResult++;
				Path path = new Path();
				Instruction instruction = new Instruction(0,
						"SOURCE EQUAL TO DESTINATION", 0, 0);
				List<Instruction> instructions = new ArrayList<>();
				instructions.add(instruction);
				path.setInstructions(instructions);
				
				finalResult.add(path);
				
				continue;
			}
			
//			smallerDistancePoIHash.remove(currentPoI);
			backwardsUnsettleNodes = backwardUnsettleNodesHash.get(currentPoI);
			backwardsUnsettleNodesAux = backwardsUnsettleNodesAuxHash.get(currentPoI);
			backwardsSettleNodes = backwardSettleNodesHash.get(currentPoI);
			backwardsParentNodes = parentsHash.get(currentPoI);
			meetingNode = meetingNodeHash.get(currentPoI);
			startCurr = startCurrList.get(currentPoI);
			endCurr = endCurrList.get(currentPoI);
			target = graph.getNode(currentPoI);

			if (!finished()) {

				logger.debug("PoI that will be analyzed: {}", currentPoI);

				if (!forwardsUnsettleNodes.isEmpty())
					forwardSearch();

				if (!backwardsUnsettleNodes.isEmpty())
					backwardSearch();
			}

		}

		knnSW.stop();
		
		for(int i = iteratorResult; i<kIterator; i++) {
			
			path = new Path();
			HashMap<Long, RouteEntry> resultParentNodes = joinParents(meetingNodeHash.get(nearestNeighborMap.get(i)), forwardsParentNodes, parentsHash.get(nearestNeighborMap.get(i)));
			path.constructPath(nearestNeighborMap.get(i), resultParentNodes, graph);

			finalResult.add(path);
			
		}
		
		return finalResult;

	}

	private boolean finished() {
		if (forwardsUnsettleNodes.isEmpty() && backwardsUnsettleNodes.isEmpty()) {
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		}
		
		if(startCurr >= meetingNode.getDistance() && endCurr >= meetingNode.getDistance()) {
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		} else {
			return false;
		}

	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private void forwardSearch() {

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		
		startCurrList.replace(currentPoI, forwardsRemovedNode.getDistance());

		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

		expandedNodesForwardSearch++;

		logger.debug("Node being analyzed in the forwardSearch(): {}", forwardsRemovedNode.getId());

		expandVertexForward();

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		int backupDistance = forwardsRemovedNode.getDistance();

		forwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				logger.debug("Node ignored: {}", vid);
				continue;
			}

			logger.debug("Node considered: {}", vid);
			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + forwardsRemovedNode.getDistance(),
					forwardsRemovedNode.getId());

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

				if (forwardsSmallerDistanceForThisIteration > neighbors.get(vid) + forwardsRemovedNode.getDistance()) {
					forwardsSmallerDistanceForThisIteration = neighbors.get(vid) + forwardsRemovedNode.getDistance();
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

					if (forwardsSmallerDistanceForThisIteration > distance) {
						forwardsSmallerDistanceForThisIteration = distance;
					}

				} else {
					forwardsSmallerDistanceForThisIteration = backupDistance;
				}
			}

			verifyMeetingNodeForwardSearch(forwardsRemovedNode.getId(), vid, neighbors);

		}
		if (forwardsSmallerDistanceForThisIteration == Integer.MAX_VALUE) {
			forwardsSmallerDistanceForThisIteration = backupDistance;
		}
		
//		if(smallerDistancePoIHash.get(target.getId()) > forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration) {
//			smallerDistancePoIHash.replace(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration);
//			smallerDistancePoI.add(new DistanceEntry(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));
//		}
		
		
	}

	private void verifyMeetingNodeForwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (backwardsUnsettleNodesAux.containsKey(vid) && (forwardsSettleNodes.get(removed) + neighbors.get(vid)
				+ backwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(
					forwardsSettleNodes.get(removed) + neighbors.get(vid) + backwardsUnsettleNodesAux.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());

			System.out.println("\t\t\tMeeting node: " + graph.getNode(meetingNode.getId()).getExternalId());
		}

	}

	private void backwardSearch() {

		backwardsRemovedNode = backwardsUnsettleNodes.poll();

		endCurrList.replace(currentPoI, forwardsRemovedNode.getDistance());

		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());

		expandedNodesBackwardSearch++;

		logger.debug("Node being analyzed in the backwardSearch(): {}", backwardsRemovedNode.getId());

		expandVertexBackward();

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		int backupDistance = backwardsRemovedNode.getDistance();
		backwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				// verifyMeetingNodeBackwardSearch(vid, neighbors);
				logger.debug("Node ignored: {}", vid);
				continue;
			}

			logger.debug("Node considered: {}", vid);

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + backwardsRemovedNode.getDistance(),
					backwardsRemovedNode.getId());

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

				if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid)
						+ backwardsRemovedNode.getDistance()) {
					backwardsSmallerDistanceForThisIteration = neighbors.get(vid) + backwardsRemovedNode.getDistance();
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

					if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid)
							+ backwardsRemovedNode.getDistance()) {
						backwardsSmallerDistanceForThisIteration = neighbors.get(vid)
								+ backwardsRemovedNode.getDistance();
					}

				} else {
					backwardsSmallerDistanceForThisIteration = backupDistance;
				}

			}

			verifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId(), vid, neighbors);

		}
		if (backwardsSmallerDistanceForThisIteration == Integer.MAX_VALUE) {
			backwardsSmallerDistanceForThisIteration = backupDistance;
		}

//		if(smallerDistancePoIHash.get(target.getId()) > forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration) {
//			smallerDistancePoIHash.replace(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration);
//		}
		smallerDistancePoI.add(new DistanceEntry(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));

	}

	private void verifyMeetingNodeBackwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (forwardsUnsettleNodesAux.containsKey(vid) && (backwardsSettleNodes.get(removed) + neighbors.get(vid)
				+ forwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(
					backwardsSettleNodes.get(removed) + neighbors.get(vid) + forwardsUnsettleNodesAux.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
			System.out.println("\t\t\tMeeting node: " + graph.getNode(meetingNode.getId()).getExternalId());
		}

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

}
