package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalDijkstraCH {

	protected static int wasSettled = -1;
	protected static boolean forwardsDirection = false;
	protected static boolean backwardsDirection = true;

	private CHGraph graph, reverseGraph;

	public BidirectionalDijkstraCH(CHGraph graph) {
		this.graph = graph;
		this.reverseGraph = graph.generateReverseCHGraph(graph);
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

		PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
		PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();

		HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
		HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();

		HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<Long, RouteEntry>();
		HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<Long, RouteEntry>();

		DistanceEntry forwardsRemovedNode = null;
		DistanceEntry backwardsRemovedNode = null;
		DistanceEntry meetingNode = null;

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);

		while (!forwardsUnsettleNodes.isEmpty() || !backwardsUnsettleNodes.isEmpty()) {

			if (!forwardsUnsettleNodes.isEmpty()) {

				forwardsRemovedNode = forwardsUnsettleNodes.poll();
				forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

				if (backwardsSettleNodes.containsKey(forwardsRemovedNode.getId())) {

					meetingNode = forwardsRemovedNode;
					HashMap<Long, RouteEntry> resultParentNodes;
					Path path = new Path();

					for (Long candidateNode : forwardsSettleNodes.keySet()) {
						for (DistanceEntry entry : backwardsUnsettleNodes) {
							if (entry.getId() == candidateNode) {
								if (backwardsSettleNodes.get(forwardsRemovedNode.getId())
										+ forwardsSettleNodes.get(forwardsRemovedNode.getId()) > entry.getDistance()
												+ forwardsSettleNodes.get(candidateNode)) {

									forwardsParentNodes.remove(meetingNode.getId());
									meetingNode = new DistanceEntry(candidateNode, entry.getDistance(),
											backwardsParentNodes.get(candidateNode).getId());

								}
							}
						}

						// TODO DOUBLE CHECK Verificando a lista de Settle do
						// sentido contrario (O QUE ESTAVA FALTANDO)
						// for(Long entry : backwardsSettleNodes.keySet()) {
						// if(entry == candidateNode) {
						// if(backwardsSettleNodes.get(forwardsRemovedNode.getId())
						// +
						// forwardsSettleNodes.get(forwardsRemovedNode.getId())
						// >
						// backwardsSettleNodes.get(entry) +
						// forwardsSettleNodes.get(candidateNode)) {
						//
						// forwardsParentNodes.remove(meetingNode.getId());
						// meetingNode = new DistanceEntry(candidateNode,
						// backwardsSettleNodes.get(entry),
						// backwardsParentNodes.get(candidateNode).getId());
						//
						// }
						//
						// }
						//
						// }

					}

					resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
					path.constructPath(target.getId(), resultParentNodes, graph);
					return path;

				}

				expandVertex(forwardsRemovedNode, forwardsUnsettleNodes, forwardsParentNodes, forwardsSettleNodes,
						graph);

			}

			if (!backwardsUnsettleNodes.isEmpty()) {

				backwardsRemovedNode = backwardsUnsettleNodes.poll();
				backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());

				if (forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {

					meetingNode = backwardsRemovedNode;
					HashMap<Long, RouteEntry> resultParentNodes;
					Path path = new Path();

					for (Long candidateNode : backwardsSettleNodes.keySet()) {

						// TODO DOUBLE CHECK Verificando a lista de Unsuttle do
						// sentido contrario
						for (DistanceEntry entry : forwardsUnsettleNodes) {

							if (entry.getId() == candidateNode) {

								if (forwardsSettleNodes.get(backwardsRemovedNode.getId())
										+ backwardsSettleNodes.get(backwardsRemovedNode.getId()) > entry.getDistance()
												+ backwardsSettleNodes.get(candidateNode)) {

									backwardsParentNodes.remove(meetingNode.getId());
									meetingNode = new DistanceEntry(candidateNode, entry.getDistance(),
											forwardsParentNodes.get(candidateNode).getId());

								}
							}
						}
						// // Verificando a lista de Settle do sentido contrario
						// (O QUE ESTAVA FALTANDO)
						// for(Long entry : forwardsSettleNodes.keySet()) {
						//
						// if (entry == candidateNode) {
						//
						// if
						// (forwardsSettleNodes.get(backwardsRemovedNode.getId())
						// +
						// backwardsSettleNodes.get(backwardsRemovedNode.getId())
						// > forwardsSettleNodes.get(entry) +
						// backwardsSettleNodes.get(candidateNode)) {
						//
						// backwardsParentNodes.remove(meetingNode.getId());
						// meetingNode = new DistanceEntry(candidateNode,
						// forwardsSettleNodes.get(entry),
						// forwardsParentNodes.get(candidateNode).getId());
						//
						//
						// }
						//
						// }
						//
						// }

					}

					resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
					path.constructPath(target.getId(), resultParentNodes, graph);
					return path;

				}

				expandVertex(backwardsRemovedNode, backwardsUnsettleNodes, backwardsParentNodes, backwardsSettleNodes,
						reverseGraph);

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void expandVertex(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes, CHGraph usedGraph) {

		Long2IntMap neighbors = usedGraph.accessNeighborhood(usedGraph.getNode(removedNode.getId()));

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()),
					removedNode.getId());

			Edge edge = null;
			int distance = -1;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);
				// settleNodes.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, usedGraph);

				if (parentNodes.containsKey(vid)) {
					if (parentNodes.get(vid).getCost() + settleNodes.get(parentNodes.get(vid).getId()) > distance
							+ settleNodes.get(removedNode.getId())) {
						parentNodes.put(vid,
								new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));
					}
				} else {
					parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));
				}

			} else {

				int cost = settleNodes.get(vid);
				distance = settleNodes.get(removedNode.getId()) + neighbors.get(vid);

				if (cost != wasSettled) {
					if (cost > distance) {
						unsettleNodes.remove(newEntry);
						unsettleNodes.offer(newEntry);
						settleNodes.remove(newEntry.getId());
						settleNodes.put(newEntry.getId(), distance);

						parentNodes.remove(vid);
						distance = neighbors.get(vid);
						edge = getEdge(removedNode.getId(), vid, distance, usedGraph);
						parentNodes.put(vid,
								new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

					}
				}
			}
		}
	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	// TODO Remover meeting node errado. Receber por parametro o destination
	// node tambem.
	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes,
			Node target) {

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
			// backwardsParentNodes.get(meetingNodeID).getId()
			nextBackwardsParent = new RouteEntry(nextNodeId, backwardsParentNodes.get(nextNodeId).getCost(),
					backwardsParentNodes.get(nextNodeId).getEdgeId(), backwardsParentNodes.get(nextNodeId).getLabel());
			nextNodeId = backwardsParentNodes.get(nextNodeId).getId();

			resultListOfParents.put(nextNodeId, nextBackwardsParent);

		}

		return resultListOfParents;

	}

	// TODO TEST
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance, CHGraph usedGraph) {
		CHEdge edge = null;

		for (Long outEdge : usedGraph.getOutEdges(fromNodeId)) {
			edge = usedGraph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}

}
