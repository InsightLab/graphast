package org.graphast.query.rnn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DateUtils;

public class RNNBreadthFirstSearch implements IRNNTimeDependent{

	private GraphBounds graph;
	
	public RNNBreadthFirstSearch(GraphBounds graph) {
		this.graph = graph;	
		this.graph.reverseGraph();
	}
	
	public NearestNeighbor search(Node customer, Date maxTravelTime, Date startServiceTime) {
		
		int numberVisitedNodes = 0;
		
 		if (graph.getPoi(customer.getId()) != null) {
 			ArrayList<Long> path = new ArrayList<Long>();
 			path.add(customer.getId());
 			numberVisitedNodes = numberVisitedNodes + 1;
 			
 			NearestNeighbor nearestNeighbor = new NearestNeighbor(customer.getId(), 0, path);
 			nearestNeighbor.setNumberVisitedNodes(numberVisitedNodes);
			return nearestNeighbor;
		}
		
		PriorityQueue<RouteQueueRNNEntry> queue = new PriorityQueue<RouteQueueRNNEntry>();
		Map<Long, List<Long>> parents = new HashMap<Long, List<Long>>();
		
		long maxTravelTimeMilliseconds = DateUtils.dateToMilli(maxTravelTime);
		long hourServiceTimeMilliseconds = DateUtils.dateToMilli(startServiceTime);
		long startServiceTimeMilliseconds = hourServiceTimeMilliseconds + maxTravelTimeMilliseconds;
		
		init(customer, queue, parents, hourServiceTimeMilliseconds, startServiceTimeMilliseconds);
		RouteQueueRNNEntry current = null;
		Set<Long> visited = new HashSet<>();
		
		while(!queue.isEmpty()) {
			
			current = queue.poll();
			numberVisitedNodes = numberVisitedNodes + 1; 
			if (visited.contains(current.getId())) {
				continue;
			} else {
				visited.add(current.getId());				
			}
			
			if(current.getTravelTime() > maxTravelTimeMilliseconds) {
				throw new PathNotFoundException(String.format("not found path in reverse graph for parameter time %s milliseconds.", 
						maxTravelTimeMilliseconds));
			}
			
			if (graph.getPoi(current.getId()) != null) {
				
				double totalCostInMilissegundo =  current.getTravelTime();
				double totalCostInNanosegundos = totalCostInMilissegundo * Math.pow(10, 6);
				ArrayList<Long> pathToTaxi = pathToTaxi(current.getId(), customer.getId(), parents);
				
				NearestNeighbor nearestNeighbor = new NearestNeighbor(current.getId(),
						Double.valueOf(totalCostInNanosegundos), pathToTaxi, numberVisitedNodes);
				return nearestNeighbor;
			}
			
			// Acessa os vizinhos do primeiro vértice da pilha, no caso os vizinho do vértice que representa o cliente.
			HashMap<Node, Integer> neighbors = graph.accessNeighborhood(graph.getNode(current.getId()), current.getArrivalTime());
			
			for (Node neighbor : neighbors.keySet()) {
				numberVisitedNodes = numberVisitedNodes + 1;
				if (visited.contains(neighbor.getId())) {
					continue;
				}
				int travelTime = current.getTravelTime() + neighbors.get(neighbor);
				if (travelTime > maxTravelTimeMilliseconds) {
					continue;
				}
				
				List<Long> parents_list = new ArrayList<Long>();
				parents_list.add(current.getId());
				parents.put(neighbor.getId(), parents_list);
				
				int arrivalTime = current.getArrivalTime() - neighbors.get(neighbor);
				
				RouteQueueRNNEntry newRouteQueueTaxiEntry = new RouteQueueRNNEntry(neighbor.getId(), travelTime, 
						arrivalTime, current.getId(), current.getRoutes());
				queue.offer(newRouteQueueTaxiEntry);
			}
			
		}
		
		throw new PathNotFoundException("not found path in reverse graph");
	}

	private ArrayList<Long> pathToTaxi(Long idTaxista, Long idCustomer, Map<Long, List<Long>> parents) {
		
		ArrayList<Long> idsPath = new ArrayList<Long>();
		idsPath.add(idTaxista);
		
		Long idAnterior = idTaxista;
		Set<Long> keySet = parents.keySet();
		 for (Iterator<Long> iter = keySet.iterator(); iter.hasNext(); ) {
			 Long next = iter.next();
			if(next.equals(idAnterior) && !next.equals(idCustomer)) {
				List<Long> list = parents.get(next);
				for (Long long1 : list) {
					idsPath.add(long1);
					idAnterior = long1;
					iter = keySet.iterator();
				}
			}
		}
		if(!idsPath.contains(idCustomer)) {
			idsPath.add(idCustomer);
		}
		
		return idsPath;
	}

	private void init(Node customer, PriorityQueue<RouteQueueRNNEntry> queue, Map<Long, List<Long>> parents, long startServiceTime, long arrivedTime) {
		
		int travelTime = Long.valueOf(startServiceTime).intValue();
		int arrivalTime = Long.valueOf(arrivedTime).intValue();
		
		List<Long> parents_list = new ArrayList<Long>();
		parents_list.add(Long.valueOf(-1));
		parents.put(customer.getId(), parents_list);
		
		queue.offer(new RouteQueueRNNEntry(customer.getId(), travelTime, arrivalTime, -1, new ArrayList<NearestNeighbor>()));
	}
}