package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

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
	 * Bidirectional Dijkstra algorithm modified
	 * to deal with the Contraction Hierarchies
	 * speed up technique.
	 * 
	 * @param source source node
	 * @param target target node
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
		
		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);
		
		while(!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {
		
			if(!forwardsUnsettleNodes.isEmpty()) {
				
				forwardsRemovedNode = forwardsUnsettleNodes.poll();
				forwardsSettleNodes.put(forwardsRemovedNode.getId(), wasSettled);
				
				if(backwardsSettleNodes.containsKey(forwardsRemovedNode.getId())) {
					
					DistanceEntry candidateNode =  forwardsUnsettleNodes.poll();
					
					if(backwardsSettleNodes.containsKey(candidateNode.getId())) {
						HashMap<Long, RouteEntry> resultParentNodes;
						Path path = new Path();
						
						if(forwardsRemovedNode.getDistance() + backwardsSettleNodes.get(forwardsRemovedNode.getDistance()) > candidateNode.getDistance() + backwardsSettleNodes.get(candidateNode.getDistance())) {
							resultParentNodes = joinParents(candidateNode, forwardsParentNodes, backwardsParentNodes);
							path.constructPath(target.getId(), resultParentNodes, graph);
							return path;
						} else {
							resultParentNodes = joinParents(forwardsRemovedNode, forwardsParentNodes, backwardsParentNodes);
							path.constructPath(target.getId(), resultParentNodes, graph);
							return path;
						}
					}
					
				}
				
				expandVertex(forwardsRemovedNode, forwardsUnsettleNodes, forwardsParentNodes, forwardsSettleNodes, forwardsDirection);
				
			}
			
			if(!backwardsUnsettleNodes.isEmpty()) {
				
				backwardsRemovedNode = backwardsUnsettleNodes.poll();
				backwardsSettleNodes.put(backwardsRemovedNode.getId(), wasSettled);
				
				if(forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {
					
					DistanceEntry candidateNode =  backwardsUnsettleNodes.poll();
					
					if(forwardsSettleNodes.containsKey(candidateNode.getId())) {
						HashMap<Long, RouteEntry> resultParentNodes;
						Path path = new Path();
						
						if(backwardsRemovedNode.getDistance() + forwardsSettleNodes.get(backwardsRemovedNode.getDistance()) > candidateNode.getDistance() + forwardsSettleNodes.get(candidateNode.getDistance())) {
							resultParentNodes = joinParents(candidateNode, forwardsParentNodes, backwardsParentNodes);
							path.constructPath(target.getId(), resultParentNodes, graph);
							return path;
						} else {
							resultParentNodes = joinParents(backwardsRemovedNode, forwardsParentNodes, backwardsParentNodes);
							path.constructPath(target.getId(), resultParentNodes, graph);
							return path;
						}
					}
					
				}
				
				expandVertex(backwardsRemovedNode, backwardsUnsettleNodes, backwardsParentNodes, backwardsSettleNodes, backwardsDirection);
				
			}
			
		}

		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");
				
	}
	
	private void expandVertex(DistanceEntry removedNode, PriorityQueue<DistanceEntry> unsettleNodes, HashMap<Long, RouteEntry> parentNodes, HashMap<Long, Integer> settleNodes, boolean expandingDirection) {

		Long2IntMap neighbors;
		
		if(!expandingDirection){
		
			neighbors = graph.accessNeighborhood(graph.getNode(removedNode.getId()));

		} else {
			
			neighbors = graph.accessIngoingNeighborhood(graph.getNode(removedNode.getId()));
		
		}
		
		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid, neighbors.get(vid), removedNode.getId());

			Edge edge = null;
			int distance = -1;

			if (!settleNodes.containsKey(vid)) {

				unsettleNodes.offer(newEntry);
				settleNodes.put(newEntry.getId(), neighbors.get(vid));

				distance = neighbors.get(vid);
				edge = getEdge(removedNode.getId(), vid, distance);	//FIX IT. Returning NULL in the backwards search

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
						edge = getEdge(removedNode.getId(), vid, distance);
						parentNodes.put(vid, new RouteEntry(removedNode.getId(), distance, edge.getId(), edge.getLabel()));

					}
				}
			}
		}
	}
	
	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {
		
		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));
	
	}
	
	private HashMap<Long, RouteEntry> joinParents(DistanceEntry removedNode, HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {
		
		HashMap<Long, RouteEntry> resultParentNodes = forwardsParentNodes;
		RouteEntry temporaryParent = backwardsParentNodes.get(removedNode);
		
		while(temporaryParent != null) {
			
			resultParentNodes.put(temporaryParent.getId(), temporaryParent);
			temporaryParent = backwardsParentNodes.get(temporaryParent);
		}
		
		return resultParentNodes;
	}
	
	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance) {
		CHEdge edge = null;
		for (Long outEdge : graph.getOutEdges(fromNodeId)) {
			edge = graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}
	
}
