package org.graphast.query.knn;

import static org.graphast.util.NumberUtils.convertToInt;

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
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Instruction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalKNNSearch {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CHGraph graph;

	private Long largestSettleNodeDistance = 0l;
	private Long largestForwardNodesDistance = 0l;
	private Long largestBackwardsNodesDistance = 0l;
	private Queue<DistanceEntry> smallerDistancePoI = new PriorityQueue<>();
	private Map<Long, Integer> smallerDistancePoIHash = new HashMap<>();
	private Queue<DistanceEntry> lowerBoundDistanceSourcePoIs = new PriorityQueue<>();
	private Queue<DistanceEntry> smallerSettleNodes = new PriorityQueue<>(Collections.reverseOrder());
	private Queue<DistanceEntry> lowerBoundForwardRemovedNode = new PriorityQueue<>(Collections.reverseOrder());

	private Map<Long, PriorityQueue<DistanceEntry>> backwardUnsettleNodesHash = new HashMap<>();
	private Map<Long, HashMap<Long, Integer>> backwardsUnsettleNodesAuxHash = new HashMap<>();
	private Map<Long, Map<Long, Integer>> backwardSettleNodesHash = new HashMap<>();
	private Map<Long, DistanceEntry> meetingNodeHash = new HashMap<>();
	private Map<Long, HashMap<Long, RouteEntry>> parentsHash = new HashMap<>();

	private Map<Long, Long> backwardAllSettleNodes = new HashMap<>();

	private DistanceEntry nextCandidateNNLowerBound = new DistanceEntry(-1, -1, -1);
	// TODO Criar metodos para adicionar pois na estrutura poisFound

	private CHNode source;
	private CHNode target;

	private Long currentPoI = -1l;
	private int kIterator = 1;

	private Map<Integer, Long> nearestNeighborMap = new HashMap<>();

	int startCurr = 0;
	int endCurr = 0;
	Map<Long, Integer> startCurrList = new HashMap<>();
	Map<Long, Integer> endCurrList = new HashMap<>();

	private int numberOfPoIs;

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

		// Updating the next candidate for nearest neighbor
		if (numberOfPoIs > k) {
			nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
			nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
		}

		StopWatch knnSW = new StopWatch();

		knnSW.start();

		while (!backwardUnsettleNodesHash.isEmpty() && kIterator < k + 1) {

			if ((numberOfPoIs > backwardsUnsettleNodesAuxHash.size()) && calculateCurrentLowerBound() > nextCandidateNNLowerBound.getDistance()) {
				PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
				HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

				CHNode target = graph.getNode(nextCandidateNNLowerBound.getId());
				initializeSearchStructures(target, backwardsUnsettleNodes);
				backwardsUnsettleNodesAux.put(target.getId(), 0);

				this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));
				this.smallerDistancePoIHash.put(target.getId(), Integer.MAX_VALUE);

				backwardUnsettleNodesHash.put(nextCandidateNNLowerBound.getId(), backwardsUnsettleNodes);
				backwardsUnsettleNodesAuxHash.put(nextCandidateNNLowerBound.getId(), backwardsUnsettleNodesAux);

				Map<Long, Integer> backwardsSettleNodes = new HashMap<>();
				backwardsSettleNodes.put(target.getId(), 0);
				backwardSettleNodesHash.put(target.getId(), backwardsSettleNodes);
				backwardAllSettleNodes.put(target.getId(), target.getId());

				meetingNodeHash.put(target.getId(), new DistanceEntry(-1, Integer.MAX_VALUE, -1));
				startCurrList.put(target.getId(), 0);
				endCurrList.put(target.getId(), 0);

				List<Long> possibleMeetingPoints = new ArrayList<>();
				possibleMeetingPoints.add(target.getId());
				backwardUnsettlePossibleMeetingPoint.put(target.getId(), possibleMeetingPoints);

				HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();
				backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));
				parentsHash.put(target.getId(), backwardsParentNodes);

				lowerBoundDistanceSourcePoIs.poll();
				if(lowerBoundDistanceSourcePoIs.peek() != null) {
					nextCandidateNNLowerBound.setId(lowerBoundDistanceSourcePoIs.peek().getId());
					nextCandidateNNLowerBound.setDistance(lowerBoundDistanceSourcePoIs.peek().getDistance());
				} else {
					nextCandidateNNLowerBound.setId(-1l);
					nextCandidateNNLowerBound.setDistance(-1);
				}
				
			}

			if (smallerDistancePoI.isEmpty()) {
				break;
			} else {
				currentPoI = smallerDistancePoI.peek().getId();
			}

			// smallerDistancePoIHash.remove(currentPoI);
			backwardsUnsettleNodes = backwardUnsettleNodesHash.get(currentPoI);
			backwardsUnsettleNodesAux = backwardsUnsettleNodesAuxHash.get(currentPoI);
			backwardsSettleNodes = backwardSettleNodesHash.get(currentPoI);
			backwardsParentNodes = parentsHash.get(currentPoI);
			meetingNode = meetingNodeHash.get(currentPoI);
			startCurr = startCurrList.get(currentPoI);
			endCurr = endCurrList.get(currentPoI);
			target = graph.getNode(currentPoI);

			if (currentPoI == source.getId()) {
				backwardUnsettleNodesHash.remove(currentPoI);

				meetingNode.setId(currentPoI);
				meetingNode.setDistance(0);
				meetingNode.setParent(currentPoI);

				nearestNeighborMap.put(kIterator, currentPoI);
				kIterator++;
				smallerDistancePoI.poll();
				smallerSettleNodes.remove(new DistanceEntry(source.getId(), 0, -1));
				continue;
			}

			forwardsRemovedNode = null;

			if (!finished()) {

				logger.debug("PoI that will be analyzed: {}", graph.getNode(currentPoI).getExternalId());

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

			}

		}

		knnSW.stop();

		for (int i = 1; i < kIterator; i++) {

			path = new Path();

			if (meetingNodeHash.get(nearestNeighborMap.get(i)).getId() == -1) {
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

			HashMap<Long, RouteEntry> resultParentNodes = joinParents(meetingNodeHash.get(nearestNeighborMap.get(i)),
					forwardsParentNodes, parentsHash.get(nearestNeighborMap.get(i)));
			path.constructPath(nearestNeighborMap.get(i), resultParentNodes, graph);

			finalResult.add(path);

		}

		return finalResult;

	}

	private boolean finished() {

		if (forwardsRemovedNode != null && backwardAllSettleNodes.containsKey(forwardsRemovedNode.getId())) {
			if (nearestNeighborMap.containsValue(forwardsRemovedNode.getId())) {
				return true;
			}
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		}

		// TODO fazer parecido para backwards?
		if (forwardsSettleNodes.containsKey(currentPoI)) {
			if (nearestNeighborMap.containsValue(currentPoI)) {
				return true;
			}
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		}

		if (forwardsUnsettleNodes.isEmpty() && backwardsUnsettleNodes.isEmpty()) {
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		}

		if (startCurr >= meetingNode.getDistance() && endCurr >= meetingNode.getDistance()) {
			backwardUnsettleNodesHash.remove(currentPoI);
			smallerDistancePoI.remove(new DistanceEntry(currentPoI, -1, -1));
			nearestNeighborMap.put(kIterator, currentPoI);
			kIterator++;
			return true;
		} else {
			return false;
		}

	}

	private void initializeSearchStructures(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	/**
	 * Initialize the necessary structures for the Forward Search.
	 */
	private void initializeForwardSearchStructure() {

		initializeSearchStructures(source, forwardsUnsettleNodes);

		forwardsUnsettleNodesAux.put(source.getId(), 0);
		forwardsParentNodes.put(source.getId(), new RouteEntry(-1, 0, -1, null));
		lowerBoundForwardRemovedNode.offer(new DistanceEntry(source.getId(), 0, -1));

	}

	/**
	 * Initialize the necessary structures for the Backward Search.
	 */
	private void initializeBackwardSearchStructure() {

		PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
		HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

		CHNode target = graph.getNode(lowerBoundDistanceSourcePoIs.poll().getId());
		initializeSearchStructures(target, backwardsUnsettleNodes);
		backwardsUnsettleNodesAux.put(target.getId(), 0);

		this.smallerDistancePoI.add(new DistanceEntry(target.getId(), 0, -1));
		this.smallerDistancePoIHash.put(target.getId(), Integer.MAX_VALUE);

		backwardUnsettleNodesHash.put(target.getId(), backwardsUnsettleNodes);
		backwardsUnsettleNodesAuxHash.put(target.getId(), backwardsUnsettleNodesAux);

		Map<Long, Integer> backwardsSettleNodes = new HashMap<>();
		backwardsSettleNodes.put(target.getId(), 0);
		backwardSettleNodesHash.put(target.getId(), backwardsSettleNodes);
		backwardAllSettleNodes.put(target.getId(), target.getId());

		meetingNodeHash.put(target.getId(), new DistanceEntry(-1, Integer.MAX_VALUE, -1));
		startCurrList.put(target.getId(), 0);
		endCurrList.put(target.getId(), 0);

		List<Long> possibleMeetingPoints = new ArrayList<>();
		possibleMeetingPoints.add(target.getId());
		backwardUnsettlePossibleMeetingPoint.put(target.getId(), possibleMeetingPoints);

		HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();
		backwardsParentNodes.put(target.getId(), new RouteEntry(-1, 0, -1, null));
		parentsHash.put(target.getId(), backwardsParentNodes);

		smallerSettleNodes.add(new DistanceEntry(target.getId(), 0, target.getId()));

	}

	private void forwardSearch() {

		lowerBoundForwardRemovedNode.offer(forwardsUnsettleNodes.peek());
		forwardsRemovedNode = forwardsUnsettleNodes.poll();

		verifyPoI(forwardsRemovedNode);

		if (forwardsRemovedNode.getDistance() > largestForwardNodesDistance) {
			largestForwardNodesDistance = (long) forwardsRemovedNode.getDistance();
		}

		if (largestSettleNodeDistance < largestForwardNodesDistance + largestBackwardsNodesDistance) {
			largestSettleNodeDistance = (long) largestForwardNodesDistance + largestBackwardsNodesDistance;
		}

		startCurrList.replace(currentPoI, forwardsRemovedNode.getDistance());

		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

		logger.debug("\t[FORWARD] Node being analyzed: {}. Distance: {}",
				graph.getNode(forwardsRemovedNode.getId()).getExternalId(), forwardsRemovedNode.getDistance());

		expandVertexForward();

	}

	/**
	 * Verifies if the forwardsRemovedNode.getID() is a Point of Interest. If
	 * true, add a meeting node and remove it from the queue.
	 * 
	 * @param forwardsRemovedNode
	 */
	private void verifyPoI(DistanceEntry forwardsRemovedNode) {
		if (graph.getPOIsNodes().contains(graph.getNode(forwardsRemovedNode.getId()))) {

			if (nearestNeighborMap.containsValue(forwardsRemovedNode.getId())) {
				return;
			}

			backwardUnsettleNodesHash.remove(forwardsRemovedNode.getId());
			smallerDistancePoI.remove(new DistanceEntry(forwardsRemovedNode.getId(), -1, -1));
			nearestNeighborMap.put(kIterator, forwardsRemovedNode.getId());

			if (meetingNodeHash.get(forwardsRemovedNode.getId()) == null) {
				meetingNodeHash.put(forwardsRemovedNode.getId(), new DistanceEntry(forwardsRemovedNode.getId(),
						forwardsRemovedNode.getDistance(), forwardsRemovedNode.getParent()));
				kIterator++;

				if (parentsHash.get(forwardsRemovedNode.getId()) == null) {
					HashMap<Long, RouteEntry> newParent = new HashMap<>();
					newParent.put(forwardsRemovedNode.getId(), new RouteEntry(-1, 0, -1, null));
					parentsHash.put(forwardsRemovedNode.getId(), newParent);
				}

				return;
			}

			meetingNodeHash.get(forwardsRemovedNode.getId()).setId(forwardsRemovedNode.getId());
			meetingNodeHash.get(forwardsRemovedNode.getId()).setDistance(forwardsRemovedNode.getDistance());
			meetingNodeHash.get(forwardsRemovedNode.getId()).setParent(forwardsRemovedNode.getParent());

			kIterator++;
			return;
		}
	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		int backupDistance = forwardsRemovedNode.getDistance();

		forwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		// neighbors.put((long) forwardsRemovedNode.getId(), 0);

		verifyMeetingNodeForwardSearch(forwardsRemovedNode.getId(), forwardsRemovedNode.getId(), neighbors);

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				logger.debug("\t\t[IGNORED] Node: {}", graph.getNode(vid).getExternalId());
				continue;
			}

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + forwardsRemovedNode.getDistance(),
					forwardsRemovedNode.getId());
			logger.debug("\t\tNode considered: {}. Distance: {}", graph.getNode(vid).getExternalId(),
					newEntry.getDistance());

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

		// smallerDistancePoI.poll();
		// smallerDistancePoI.add(new DistanceEntry(target.getId(),
		// forwardsSmallerDistanceForThisIteration +
		// backwardsSmallerDistanceForThisIteration, -1));

	}

	private void verifyMeetingNodeForwardSearch(long removed, long vid, Long2IntMap neighbors) {
		// backwardUnsettlePossibleMeetingPoint<Long, Long>

		if (backwardUnsettlePossibleMeetingPoint.containsKey(vid)) {
			for (Long poi : backwardUnsettlePossibleMeetingPoint.get(vid)) {
				meetingNode = meetingNodeHash.get(poi);
				if ((forwardsSettleNodes.get(removed) + neighbors.get(vid)
						+ backwardsUnsettleNodesAuxHash.get(poi).get(vid) < meetingNode.getDistance())) {
					meetingNode.setId(vid);
					meetingNode.setDistance(forwardsSettleNodes.get(removed) + neighbors.get(vid)
							+ backwardsUnsettleNodesAuxHash.get(poi).get(vid));
					meetingNode.setParent(forwardsRemovedNode.getId());
					logger.debug("\t\t\tMeeting node: {}. Distance: {}",
							graph.getNode(meetingNode.getId()).getExternalId(), meetingNode.getDistance());
				}
			}
		}

		// if (backwardsUnsettleNodesAux.containsKey(vid) &&
		// (forwardsSettleNodes.get(removed) + neighbors.get(vid)
		// + backwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
		// meetingNode.setId(vid);
		// meetingNode.setDistance(
		// forwardsSettleNodes.get(removed) + neighbors.get(vid) +
		// backwardsUnsettleNodesAux.get(vid));
		// meetingNode.setParent(forwardsRemovedNode.getId());
		//
		// logger.debug("\t\t\tMeeting node: {}. Distance: {}",
		// graph.getNode(meetingNode.getId()).getExternalId(),
		// meetingNode.getDistance());
		// }

	}

	private void backwardSearch() {

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardAllSettleNodes.put(backwardsRemovedNode.getId(), currentPoI);

		smallerSettleNodes.remove(new DistanceEntry(currentPoI, -1, -1));
		smallerSettleNodes
				.offer(new DistanceEntry(currentPoI, backwardsRemovedNode.getDistance(), backwardsRemovedNode.getId()));

		largestBackwardsNodesDistance = (long) backwardsRemovedNode.getDistance();

		if (largestSettleNodeDistance < largestForwardNodesDistance + largestBackwardsNodesDistance) {
			largestSettleNodeDistance = (long) largestForwardNodesDistance + largestBackwardsNodesDistance;
		}
		endCurrList.replace(currentPoI, backwardsRemovedNode.getDistance());

		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());

		logger.debug("\t[BACKWARD] Node being analyzed: {}. Distance: {}",
				graph.getNode(backwardsRemovedNode.getId()).getExternalId(), backwardsRemovedNode.getDistance());

		expandVertexBackward();

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		int backupDistance = backwardsRemovedNode.getDistance();
		backwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;

		// neighbors.put((long) backwardsRemovedNode.getId(), 0);

		verifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId(), backwardsRemovedNode.getId(), neighbors);

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				// verifyMeetingNodeBackwardSearch(vid, neighbors);
				logger.debug("\t\t[IGNORED] Node: {}", graph.getNode(vid).getExternalId());
				continue;
			}

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + backwardsRemovedNode.getDistance(),
					backwardsRemovedNode.getId());

			logger.debug("\t\tNode considered: {}. Distance: {}", graph.getNode(vid).getExternalId(),
					newEntry.getDistance());

			if (backwardUnsettlePossibleMeetingPoint.get(vid) == null) {
				List<Long> listOfPoIsForThisNode = new ArrayList<>();
				listOfPoIsForThisNode.add(target.getId());
				backwardUnsettlePossibleMeetingPoint.put(vid, listOfPoIsForThisNode);
			} else {
				List<Long> listOfPoIsForThisNode = backwardUnsettlePossibleMeetingPoint.get(vid);
				listOfPoIsForThisNode.add(target.getId());
				backwardUnsettlePossibleMeetingPoint.replace(vid, listOfPoIsForThisNode);
			}

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

		// if(smallerDistancePoIHash.get(target.getId()) >
		// forwardsSmallerDistanceForThisIteration +
		// backwardsSmallerDistanceForThisIteration) {
		// smallerDistancePoIHash.replace(target.getId(),
		// forwardsSmallerDistanceForThisIteration +
		// backwardsSmallerDistanceForThisIteration);
		// }
		if (!nearestNeighborMap.containsValue(target.getId())) {
			smallerDistancePoI.poll();
			smallerDistancePoI.add(new DistanceEntry(target.getId(), backwardsSmallerDistanceForThisIteration, -1));
		}

	}

	private void verifyMeetingNodeBackwardSearch(long removed, long vid, Long2IntMap neighbors) {

		if (forwardsUnsettleNodesAux.containsKey(vid) && (backwardsSettleNodes.get(removed) + neighbors.get(vid)
				+ forwardsUnsettleNodesAux.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(
					backwardsSettleNodes.get(removed) + neighbors.get(vid) + forwardsUnsettleNodesAux.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
			logger.debug("\t\t\tMeeting node: {}. Distance: {}", graph.getNode(meetingNode.getId()).getExternalId(),
					meetingNode.getDistance());
		} else if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(removed) + neighbors.get(vid)
				+ forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode
					.setDistance(backwardsSettleNodes.get(removed) + neighbors.get(vid) + forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
			logger.debug("\t\t\tMeeting node: {}. Distance: {}", graph.getNode(meetingNode.getId()).getExternalId(),
					meetingNode.getDistance());

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

	private int calculateCurrentLowerBound() {

		Map<Long, PriorityQueue<DistanceEntry>> possibleBackwardUnsettleNodes = new HashMap<>();
		for (Map.Entry<Long, PriorityQueue<DistanceEntry>> entryToBeCopied : backwardUnsettleNodesHash.entrySet()) {
			Queue<DistanceEntry> newEntry = new PriorityQueue<>(entryToBeCopied.getValue());
			possibleBackwardUnsettleNodes.put(entryToBeCopied.getKey(), (PriorityQueue<DistanceEntry>) newEntry);
		}

		Map<Long, PriorityQueue<DistanceEntry>> backwardNodesToBeAnalyzed = new HashMap<>();

		for (Map.Entry<Long, PriorityQueue<DistanceEntry>> possibleLowerBoundPoI : possibleBackwardUnsettleNodes
				.entrySet()) {

			DistanceEntry backwardLowerBoundNode = possibleLowerBoundPoI.getValue().poll();
			Queue<DistanceEntry> localBackwardNodesToBeAnalyzed = new PriorityQueue<>();
			localBackwardNodesToBeAnalyzed.add(backwardLowerBoundNode);

			while (possibleLowerBoundPoI.getValue().peek() != null
					&& possibleLowerBoundPoI.getValue().peek().getDistance() == backwardLowerBoundNode.getDistance()) {
				localBackwardNodesToBeAnalyzed.add(possibleLowerBoundPoI.getValue().peek());
				possibleLowerBoundPoI.getValue().poll();
			}

			backwardNodesToBeAnalyzed.put(possibleLowerBoundPoI.getKey(),
					(PriorityQueue<DistanceEntry>) localBackwardNodesToBeAnalyzed);
		}

		Map<Long, PriorityQueue<Integer>> finalBackwardsLowerBounds = new HashMap<>();

		for (Map.Entry<Long, PriorityQueue<DistanceEntry>> backwardCandidates : backwardNodesToBeAnalyzed.entrySet()) {

			int localPoILowerBound = Integer.MAX_VALUE;

			for (DistanceEntry backwardCandidate : backwardCandidates.getValue()) {
				
				CHNode lowerBoundSource = source;
				CHNode lowerBoundTarget = graph.getNode(backwardCandidate.getId());

				int lowerBound = (int) Math
						.sqrt(Math.pow((lowerBoundTarget.getLatitude() - lowerBoundSource.getLatitude()), 2)
								+ Math.pow((lowerBoundTarget.getLongitude() - lowerBoundSource.getLongitude()), 2));

				if (lowerBound + backwardCandidate.getDistance() < localPoILowerBound) {
					localPoILowerBound = lowerBound + backwardCandidate.getDistance();
				}

			}

			if (finalBackwardsLowerBounds.get(backwardCandidates.getKey()) == null) {
				Queue<Integer> teste = new PriorityQueue<>();
				teste.add(localPoILowerBound);
				finalBackwardsLowerBounds.put(backwardCandidates.getKey(), (PriorityQueue<Integer>) teste);
			} else {
				finalBackwardsLowerBounds.get(backwardCandidates.getKey()).add(localPoILowerBound);
			}

		}

		int lowerBound = 0;

		for (Map.Entry<Long, PriorityQueue<Integer>> entry : finalBackwardsLowerBounds.entrySet()) {
			if (entry.getValue().peek() > lowerBound)
				lowerBound = entry.getValue().peek();
		}

		return lowerBound;
	}

}
