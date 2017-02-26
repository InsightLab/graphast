package org.graphast.query.knnch.baseline;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

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

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalDijkstraCHIterator {

	// External references
	private CHGraph graph;
	private CHNode source;
	private CHNode target;
	private Queue<DistanceEntry> smallerDistancePoI;
	private Map<Long, BidirectionalDijkstraCHIterator> dijkstraHash;
	
	private int forwardsSmallerDistanceForThisIteration = 0;
	private int backwardsSmallerDistanceForThisIteration = 0;

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

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

	// --METRICS VARIABLES
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfRegularSearches = 0;

	public BidirectionalDijkstraCHIterator(CHGraph graph, CHNode source, CHNode target,
			Queue<DistanceEntry> smallerDistancePoI, Map<Long, BidirectionalDijkstraCHIterator> dijkstraHash) {

		this.graph = graph;
		this.source = source;
		this.target = target;
		this.smallerDistancePoI = smallerDistancePoI;
		this.dijkstraHash = dijkstraHash;

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);

		forwardsUnsettleNodesAux.put(source.getId(), 0);
		backwardsUnsettleNodesAux.put(target.getId(), 0);

		forwardsRemovedNode = forwardsUnsettleNodes.peek();
		backwardsRemovedNode = backwardsUnsettleNodes.peek();
		
	}

	public boolean iterate() {

		// Condition to alternate between forward and backward search

		if (forwardsUnsettleNodes.isEmpty()) {
			return backwardSearch();
		}

		if (backwardsUnsettleNodes.isEmpty()) {
			return forwardSearch();
		}

		if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {
			return forwardSearch();
		} else {
			return backwardSearch();
		}

	}

	private boolean forwardSearch() {

		int forwardDistance;
		int backwardDistance;

		if (forwardsUnsettleNodes.isEmpty()) {
			forwardDistance = forwardsSmallerDistanceForThisIteration;
		} else {
			forwardDistance = forwardsUnsettleNodes.peek().getDistance();
		}

		if (backwardsUnsettleNodes.isEmpty()) {
			backwardDistance = backwardsSmallerDistanceForThisIteration;
		} else {
			backwardDistance = backwardsUnsettleNodes.peek().getDistance();
		}

		if (forwardDistance + backwardDistance >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);
			System.out.println("Caminho para o PoI " + target.getId());
			System.out.println("\t" + path);
			return true;

		}

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsUnsettleNodesAux.remove(forwardsRemovedNode.getId());
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		numberOfForwardSettleNodes++;

		expandVertexForward(forwardsRemovedNode);

		return false;

	}

	private boolean backwardSearch() {

		int forwardDistance;
		int backwardDistance;

		if (forwardsUnsettleNodes.isEmpty()) {
			forwardDistance = forwardsSmallerDistanceForThisIteration;
		} else {
			forwardDistance = forwardsUnsettleNodes.peek().getDistance();
		}

		if (backwardsUnsettleNodes.isEmpty()) {
			backwardDistance = backwardsSmallerDistanceForThisIteration;
		} else {
			backwardDistance = backwardsUnsettleNodes.peek().getDistance();
		}

		if (forwardDistance + backwardDistance >= meetingNode.getDistance()) {

			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);
			System.out.println("Caminho para o PoI " + target.getId());
			System.out.println("\t" + path);
			System.out.println("Vizinho encontrado!");

			return true;

		}

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		numberOfBackwardSettleNodes++;

		expandVertexBackward(backwardsRemovedNode);

		return false;

	}

	private void expandVertexForward(DistanceEntry forwardsRemovedNode) {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		boolean flag = false;
		
		int backupDistance = forwardsSmallerDistanceForThisIteration;
		
		forwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;
		
		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode.getId()).getLevel()) {
				// if(graph.getNode(vid).getExternalId() !=
				// graph.getNode(removed.getId()).getExternalId()) {
				continue;
				// }

			}

			flag = true;

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

				forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

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

					// TODO Chegar se eu não tenho que retirar da pilha
					if (forwardsSmallerDistanceForThisIteration > distance) {
						forwardsSmallerDistanceForThisIteration = distance;
					} 

				} else {
					forwardsSmallerDistanceForThisIteration = backupDistance;
				}
			}

			verifyMeetingNodeForwardSearch(vid, neighbors);

		}

		if (!flag) {
			specialVerifyMeetingNodeForwardSearch(forwardsRemovedNode.getId());

			if (forwardsSmallerDistanceForThisIteration > meetingNode.getDistance()) {
				forwardsSmallerDistanceForThisIteration = meetingNode.getDistance();
			} 
			else {
				forwardsSmallerDistanceForThisIteration = backupDistance;
			}
		}
		smallerDistancePoI.add(new DistanceEntry(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));
	}

	private void verifyMeetingNodeForwardSearch(long vid, Long2IntMap neighbors) {

		if (backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())
				+ neighbors.get(vid) + backwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(forwardsSettleNodes.get(forwardsRemovedNode.getId()) + neighbors.get(vid)
					+ backwardsSettleNodes.get(vid));
			meetingNode.setParent(forwardsRemovedNode.getId());
		}
	}

	private void specialVerifyMeetingNodeForwardSearch(long vid) {

		if (forwardsSettleNodes.containsKey(vid) && backwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(vid)
				+ forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode
					.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}
	}

	private void expandVertexBackward(DistanceEntry backwardsRemovedNode) {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		boolean flag = false;
		
		int backupDistance = backwardsSmallerDistanceForThisIteration;
		backwardsSmallerDistanceForThisIteration = Integer.MAX_VALUE;
		
		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode.getId()).getLevel()) {
				continue;
			}

			flag = true;

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

				if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid) + backwardsRemovedNode.getDistance()) {
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

					if (backwardsSmallerDistanceForThisIteration > neighbors.get(vid) + backwardsRemovedNode.getDistance()) {
						backwardsSmallerDistanceForThisIteration = neighbors.get(vid) + backwardsRemovedNode.getDistance();
					} 
					

				} else {
					backwardsSmallerDistanceForThisIteration = backupDistance;
				}

			}

			verifyMeetingNodeBackwardSearch(vid, neighbors);

		}

		if (!flag) {
			specialVerifyMeetingNodeBackwardSearch(backwardsRemovedNode.getId());

			if (backwardsSmallerDistanceForThisIteration > meetingNode.getDistance()) {
				backwardsSmallerDistanceForThisIteration = meetingNode.getDistance();
			} else {
				backwardsSmallerDistanceForThisIteration = backupDistance;
			}
		}
		
		smallerDistancePoI.add(new DistanceEntry(target.getId(), forwardsSmallerDistanceForThisIteration + backwardsSmallerDistanceForThisIteration, -1));
	}

	private void verifyMeetingNodeBackwardSearch(long vid, Long2IntMap neighbors) {

		if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())
				+ neighbors.get(vid) + forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + neighbors.get(vid)
					+ forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}
	}

	private void specialVerifyMeetingNodeBackwardSearch(long vid) {

		if (forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())
				+ forwardsSettleNodes.get(vid) < meetingNode.getDistance())) {
			meetingNode.setId(vid);
			meetingNode
					.setDistance(backwardsSettleNodes.get(backwardsRemovedNode.getId()) + forwardsSettleNodes.get(vid));
			meetingNode.setParent(backwardsRemovedNode.getId());
		}

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		RouteEntry nextForwardParent = forwardsParentNodes.get(meetingNode.getId());

		if (nextForwardParent != null) {
			
			if(forwardsParentNodes.get(meetingNode.getId()) == null) {
				forwardsParentNodes.put(meetingNode.getId(), backwardsParentNodes.get(meetingNode.getId()));
			}

			resultListOfParents.put(meetingNode.getId(), nextForwardParent);

			while (forwardsParentNodes.get(nextForwardParent.getId()) != null) {

				resultListOfParents.put(nextForwardParent.getId(), forwardsParentNodes.get(nextForwardParent.getId()));

				nextForwardParent = forwardsParentNodes.get(nextForwardParent.getId());

			}

		}

		if (!backwardsParentNodes.isEmpty()) {

			if(backwardsParentNodes.get(meetingNode.getId()) == null) {
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

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

}
