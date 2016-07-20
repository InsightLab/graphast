package org.graphast.query.route.shortestpath.dijkstraCH;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.RouteEntry;

public class BidirectionalDijkstraCH {

	protected static int wasSettled = -1;	
	
	/**
	 * Bidirectional Dijkstra algorithm modified
	 * to deal with the Contraction Hierarchies
	 * speed up technique.
	 * 
	 * @param source source node
	 * @param target target node
	 */
	public void execute(Node source, Node target) {
		
		PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
		PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
		
		
		HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
		HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();
		
//		HashMap<Long, Integer> forwardsUnsettleNodes = new HashMap<>();
//		HashMap<Long, Integer> backwardsUnsettleNods = new HashMap<>();
		
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
					//procurar vizinhos
				}
				
				//expand node
				

			}
			
			if(!backwardsUnsettleNodes.isEmpty()) {
				
				backwardsRemovedNode = backwardsUnsettleNodes.poll();
				backwardsSettleNodes.put(backwardsRemovedNode.getId(), wasSettled);
				
				if(forwardsSettleNodes.containsKey(backwardsRemovedNode.getId())) {
					//procurar vizinhos
				}
				
				//expand node
				
			}
			
		}

		throw new PathNotFoundException("Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");
				
	}
	
	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {
		
		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));
	
	}
	
}
