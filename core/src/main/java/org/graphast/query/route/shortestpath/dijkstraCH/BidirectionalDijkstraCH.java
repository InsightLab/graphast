package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
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
	
	StopWatch totalNearestNeighborSW = new StopWatch();
	int totalNumberOfNeighborsAccess = 0;
	int totalAverageNeighborsAccessTime = 0;
	
	StopWatch forwardNearestNeighborSW = new StopWatch();
	int forwardNumberOfNeighborsAccess = 0;
	int forwardAverageNeighborsAccessTime = 0;
	
	StopWatch backwardNearestNeighborSW = new StopWatch();
	int backwardNumberOfNeighborsAccess = 0;
	int backwardAverageNeighborsAccessTime = 0;
	
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfTotalSettleNodes = 0;

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

		

		while (!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {

			// Condition to alternate between forward and backward search
			if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {

				if (!forwardsUnsettleNodes.isEmpty()) {

					forwardsRemovedNode = forwardsUnsettleNodes.poll();
					forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
					numberOfForwardSettleNodes++;

					// Stopping criteria of Bidirectional search
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
							//
							// // TODO DOUBLE CHECK Verificando a lista de
							// Settle
							// // do
							// // sentido contrario (O QUE ESTAVA FALTANDO)
							// // for(Long entry :
							// backwardsSettleNodes.keySet()) {
							// // if(entry == candidateNode) {
							// //
							// if(backwardsSettleNodes.get(forwardsRemovedNode.getId())
							// // +
							// //
							// forwardsSettleNodes.get(forwardsRemovedNode.getId())
							// // >
							// // backwardsSettleNodes.get(entry) +
							// // forwardsSettleNodes.get(candidateNode)) {
							// //
							// //
							// forwardsParentNodes.remove(meetingNode.getId());
							// // meetingNode = new DistanceEntry(candidateNode,
							// // backwardsSettleNodes.get(entry),
							// //
							// backwardsParentNodes.get(candidateNode).getId());
							// //
							// // }
							// //
							// // }
							// //
							// // }
							//
						}

						resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
						path.constructPath(target.getId(), resultParentNodes, graph);
						
						
						numberOfTotalSettleNodes = numberOfForwardSettleNodes + numberOfBackwardSettleNodes;
						System.out.println("[BIDIRECTIONAL] Number of forward settle nodes: " + numberOfForwardSettleNodes);
						System.out.println("[BIDIRECTIONAL] Number of backward settle nodes: " + numberOfBackwardSettleNodes);
						System.out.println("[BIDIRECTIONAL] Number of total settle nodes: " + numberOfTotalSettleNodes);
						
						totalAverageNeighborsAccessTime = totalAverageNeighborsAccessTime/totalNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Total Average Neighbors Access Time: " + totalAverageNeighborsAccessTime);
						
						forwardAverageNeighborsAccessTime = forwardAverageNeighborsAccessTime/forwardNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Forward Average Neighbors Access Time: " + forwardAverageNeighborsAccessTime);
						
						backwardAverageNeighborsAccessTime = backwardAverageNeighborsAccessTime/backwardNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Backward Average Neighbors Access Time: " + backwardAverageNeighborsAccessTime);

						return path;

					}
					
					expandVertex(forwardsRemovedNode, forwardsUnsettleNodes, forwardsParentNodes, forwardsSettleNodes,
							forwardDirection);
					
				}

			} else {

				if (!backwardsUnsettleNodes.isEmpty()) {

					backwardsRemovedNode = backwardsUnsettleNodes.poll();
					backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
					numberOfBackwardSettleNodes++;
					
					// Stopping criteria of Bidirectional search
					if (forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {
						meetingNode = backwardsRemovedNode;
						HashMap<Long, RouteEntry> resultParentNodes;
						Path path = new Path();

						for (Long candidateNode : backwardsSettleNodes.keySet()) {

							// TODO DOUBLE CHECK Verificando a lista de Unsuttle
							// do
							// sentido contrario
							for (DistanceEntry entry : forwardsUnsettleNodes) {

								if (entry.getId() == candidateNode) {

									if (forwardsSettleNodes.get(backwardsRemovedNode.getId()) + backwardsSettleNodes
											.get(backwardsRemovedNode.getId()) > entry.getDistance()
													+ backwardsSettleNodes.get(candidateNode)) {

										backwardsParentNodes.remove(meetingNode.getId());
										meetingNode = new DistanceEntry(candidateNode, entry.getDistance(),
												forwardsParentNodes.get(candidateNode).getId());

									}
								}
							}
							// // // Verificando a lista de Settle do sentido
							// // contrario
							// // (O QUE ESTAVA FALTANDO)
							// // for(Long entry : forwardsSettleNodes.keySet())
							// {
							// //
							// // if (entry == candidateNode) {
							// //
							// // if
							// //
							// (forwardsSettleNodes.get(backwardsRemovedNode.getId())
							// // +
							// //
							// backwardsSettleNodes.get(backwardsRemovedNode.getId())
							// // > forwardsSettleNodes.get(entry) +
							// // backwardsSettleNodes.get(candidateNode)) {
							// //
							// //
							// backwardsParentNodes.remove(meetingNode.getId());
							// // meetingNode = new DistanceEntry(candidateNode,
							// // forwardsSettleNodes.get(entry),
							// //
							// forwardsParentNodes.get(candidateNode).getId());
							// //
							// //
							// // }
							// //
							// // }
							// //
							// // }
							//
						}

						resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
						path.constructPath(target.getId(), resultParentNodes, graph);
						
						numberOfTotalSettleNodes = numberOfForwardSettleNodes + numberOfBackwardSettleNodes;
						System.out.println("[BIDIRECTIONAL] Number of forward settle nodes: " + numberOfForwardSettleNodes);
						System.out.println("[BIDIRECTIONAL] Number of backward settle nodes: " + numberOfBackwardSettleNodes);
						System.out.println("[BIDIRECTIONAL] Number of total settle nodes: " + numberOfTotalSettleNodes);
						
						totalAverageNeighborsAccessTime = totalAverageNeighborsAccessTime/totalNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Total Average Neighbors Access Time: " + totalAverageNeighborsAccessTime);
						
						forwardAverageNeighborsAccessTime = forwardAverageNeighborsAccessTime/forwardNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Forward Average Neighbors Access Time: " + forwardAverageNeighborsAccessTime);
						
						backwardAverageNeighborsAccessTime = backwardAverageNeighborsAccessTime/backwardNumberOfNeighborsAccess;
						System.out.println("[BIDIRECTIONAL] Backward Average Neighbors Access Time: " + backwardAverageNeighborsAccessTime);
						
						return path;

					}
					
					expandVertex(backwardsRemovedNode, backwardsUnsettleNodes, backwardsParentNodes,
							backwardsSettleNodes, backwardDirection);

				}

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void expandVertex(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes, boolean expandingDirection) {

		if (expandingDirection == true) {

			expandVertexForward(removedNode, unsettleNodes, parentNodes, settleNodes);

		} else {

			expandVertexBackward(removedNode, unsettleNodes, parentNodes, settleNodes);

		}

	}

	private void expandVertexForward(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes) {

		totalNearestNeighborSW.start();
		forwardNearestNeighborSW.start();
		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(removedNode.getId()));
		forwardNearestNeighborSW.stop();
		totalNearestNeighborSW.stop();
		
		forwardNumberOfNeighborsAccess++;
		forwardAverageNeighborsAccessTime+=forwardNearestNeighborSW.getNanos();
		totalNumberOfNeighborsAccess++;
		totalAverageNeighborsAccessTime+=totalNearestNeighborSW.getNanos();
		
		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()),
					removedNode.getId());

			Edge edge = null;
			int distance = -1;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);
				// settleNodes.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, forwardDirection);

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
						edge = getEdge(removedNode.getId(), vid, distance, forwardDirection);
						parentNodes.put(vid,
								new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

					}
				}
			}
		}
		
	}

	private void expandVertexBackward(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes) {

		
		totalNearestNeighborSW.start();
		backwardNearestNeighborSW.start();
		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(removedNode.getId()));
		backwardNearestNeighborSW.stop();
		totalNearestNeighborSW.stop();
		
		backwardNumberOfNeighborsAccess++;
		backwardAverageNeighborsAccessTime+=backwardNearestNeighborSW.getNanos();
		totalNumberOfNeighborsAccess++;
		totalAverageNeighborsAccessTime+=totalNearestNeighborSW.getNanos();
		
		
		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()),
					removedNode.getId());

			Edge edge = null;
			int distance = -1;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);
				// settleNodes.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, backwardDirection);

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
						edge = getEdge(removedNode.getId(), vid, distance, backwardDirection);
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
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance, boolean expandingDirection) {

		if (expandingDirection == true) {

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

		returningEdge.setFromNode(edge.getToNode());
		returningEdge.setToNode(edge.getFromNode());

		return returningEdge;

	}

}
