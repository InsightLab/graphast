package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

import javax.security.auth.callback.NameCallback;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
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

		
		//TODO && or || ?
		while (!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {

			if (!forwardsUnsettleNodes.isEmpty()) {

				forwardsRemovedNode = forwardsUnsettleNodes.poll();
				forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

				if (backwardsSettleNodes.containsKey(forwardsRemovedNode.getId())) {

					meetingNode = forwardsRemovedNode;
					HashMap<Long, RouteEntry> resultParentNodes;
					Path path = new Path();
					
					for (Long candidateNode : forwardsSettleNodes.keySet()) {

//						if (backwardsUnsettleNodes.contains(candidateNode)) {
//							
//							//TODO double check if we can compare just with the top of the backwardsUnsettleNodes Queue!
//							if (backwardsSettleNodes.get(forwardsRemovedNode.getId()) + forwardsSettleNodes.get(forwardsRemovedNode.getId()) > 	forwardsSettleNodes.get(candidateNode) + backwardsUnsettleNodes.peek().getDistance()) {
//								
//								//TODO double check this DistanceEntry!!!
//								meetingNode = new DistanceEntry(candidateNode, forwardsSettleNodes.get(candidateNode), forwardsParentNodes.get(candidateNode).getId());
//								
//								
//							} 
//						}
						
						
						for(DistanceEntry entry : backwardsUnsettleNodes) {
							if(entry.getId() == candidateNode) {
								if(backwardsSettleNodes.get(forwardsRemovedNode.getId()) + forwardsSettleNodes.get(forwardsRemovedNode.getId()) >
								entry.getDistance()	+ forwardsSettleNodes.get(candidateNode)) {
									
									forwardsParentNodes.remove(meetingNode.getId());
									meetingNode = new DistanceEntry(candidateNode, entry.getDistance(), backwardsParentNodes.get(candidateNode).getId());
									
								}
							}
						}
						
					}
					
					resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
					path.constructPath(target.getId(), resultParentNodes, graph);
					return path;
					
				}

				expandVertex(forwardsRemovedNode, forwardsUnsettleNodes, forwardsParentNodes, forwardsSettleNodes,
						forwardsDirection);

			}

			if (!backwardsUnsettleNodes.isEmpty()) {

				backwardsRemovedNode = backwardsUnsettleNodes.poll();
				backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());

				if (forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {

					meetingNode = backwardsRemovedNode;
					HashMap<Long, RouteEntry> resultParentNodes;
					Path path = new Path();

					for (Long candidateNode : backwardsSettleNodes.keySet()) {

						
						for(DistanceEntry entry : forwardsUnsettleNodes) {
						
							if (entry.getId() == candidateNode) {
	
								if (forwardsSettleNodes.get(backwardsRemovedNode.getId()) + backwardsSettleNodes.get(backwardsRemovedNode.getId()) > 
										entry.getDistance()	+ backwardsSettleNodes.get(candidateNode)) {
	
									backwardsParentNodes.remove(meetingNode.getId());
									meetingNode = new DistanceEntry(candidateNode, entry.getDistance(),
											forwardsParentNodes.get(candidateNode).getId());
	
								}
							}
						}
					}

					resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes, target);
					path.constructPath(target.getId(), resultParentNodes, graph);
					return path;

				}

				expandVertex(backwardsRemovedNode, backwardsUnsettleNodes, backwardsParentNodes, backwardsSettleNodes,
						backwardsDirection);

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void expandVertex(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes,
			HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes, boolean expandingDirection) {

		Long2IntMap neighbors;

		if (!expandingDirection) {

			neighbors = graph.accessNeighborhood(graph.getNode(removedNode.getId()));

		} else {

			neighbors = graph.accessIngoingNeighborhood(graph.getNode(removedNode.getId()));

		}

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid) + settleNodes.get(removedNode.getId()), removedNode.getId());

			Edge edge = null;
			int distance = -1;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);
				settleNodes.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance, expandingDirection); // FIX
																						// IT.
																						// Returning
																						// NULL
																						// in
																						// the
																						// backwards
																						// search

				parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = settleNodes.get(vid);
				distance = neighbors.get(vid);

				if (cost != wasSettled) {
					if (cost > distance) {
						unsettleNodes.remove(newEntry);
						unsettleNodes.offer(newEntry);
						settleNodes.remove(newEntry.getId());
						settleNodes.put(newEntry.getId(), distance);

						parentNodes.remove(vid);
						distance = neighbors.get(vid);
						edge = getEdge(removedNode.getId(), vid, distance, expandingDirection);
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

	//TODO Remover meeting node errado. Receber por parametro o destination node tambem.
	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes, Node target) {

	
		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		RouteEntry nextForwardParent = new RouteEntry(forwardsParentNodes.get(meetingNode.getId()).getId(), forwardsParentNodes.get(meetingNode.getId()).getCost(), forwardsParentNodes.get(meetingNode.getId()).getEdgeId(), forwardsParentNodes.get(meetingNode.getId()).getLabel());
		
		resultListOfParents.put(meetingNode.getId(), nextForwardParent);
		
		while(forwardsParentNodes.get(nextForwardParent.getId()) != null) {
		
			resultListOfParents.put(nextForwardParent.getId(), forwardsParentNodes.get(nextForwardParent.getId()));
			
			nextForwardParent = forwardsParentNodes.get(nextForwardParent.getId());
			
		}
		

		RouteEntry nextBackwardsParent = new RouteEntry(backwardsParentNodes.get(meetingNode.getId()).getId(), backwardsParentNodes.get(meetingNode.getId()).getCost(), backwardsParentNodes.get(meetingNode.getId()).getEdgeId(), backwardsParentNodes.get(meetingNode.getId()).getLabel());
		
		long auxiliarBackwardID = nextBackwardsParent.getId();
		nextBackwardsParent.setId(meetingNode.getId());
		
		resultListOfParents.put(auxiliarBackwardID, nextBackwardsParent);
		
		while(backwardsParentNodes.get(nextBackwardsParent.getId()) != null) {
			auxiliarBackwardID = backwardsParentNodes.get(nextBackwardsParent.getId()).getId();
			nextBackwardsParent = new RouteEntry(nextBackwardsParent.getId(), backwardsParentNodes.get(nextBackwardsParent.getId()).getCost(), backwardsParentNodes.get(nextBackwardsParent.getId()).getEdgeId(), backwardsParentNodes.get(nextBackwardsParent.getId()).getLabel());
			//TODO Double check this two lines of code
			resultListOfParents.put(auxiliarBackwardID, nextBackwardsParent);
			
			nextBackwardsParent = backwardsParentNodes.get(nextBackwardsParent.getId());
			
		}
		
		
		return resultListOfParents;
		
	}
	
//	private HashMap<Long, RouteEntry> joinParents(DistanceEntry removedNode,
//			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {
//
//		HashMap<Long, RouteEntry> resultParentNodes = forwardsParentNodes;
//		RouteEntry temporaryParent = backwardsParentNodes.get(removedNode.getId());
//
//		while (temporaryParent != null) {
//
//			resultParentNodes.put(temporaryParent.getId(), temporaryParent);
//			temporaryParent = backwardsParentNodes.get(temporaryParent);
//		}
//
//		return resultParentNodes;
//	}

	// TODO TEST
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance, boolean expandingDirection) {
		CHEdge edge = null;

		if (expandingDirection == forwardsDirection) {

			for (Long outEdge : graph.getOutEdges(fromNodeId)) {
				edge = graph.getEdge(outEdge);
				if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
					break;
				}
			}
			return edge;
		} else {
			for (Long inEdge : graph.getInEdges(fromNodeId)) {
				edge = graph.getEdge(inEdge);
				if ((int) edge.getFromNode() == fromNodeId && edge.getDistance() == distance) {
					break;
				}
			}
			return edge;
		}
	}

}
