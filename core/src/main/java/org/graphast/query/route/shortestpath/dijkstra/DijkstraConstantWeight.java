package org.graphast.query.route.shortestpath.dijkstra;

import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.query.route.shortestpath.model.TimeEntry;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class DijkstraConstantWeight extends Dijkstra {

	public DijkstraConstantWeight(Graph graph) {
		super(graph);
	}

	public DijkstraConstantWeight(GraphBounds graphBounds) {
		super(graphBounds);
	}

	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed,
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents) {

		this.expandVertex(target, removed, wasTraversed, queue, parents, null);
	}

	// This method with consider a node to be skipped!
	public void expandVertex(Node target, TimeEntry removed, HashMap<Long, Integer> wasTraversed,
			PriorityQueue<TimeEntry> queue, HashMap<Long, RouteEntry> parents, Node skippedNode) {

		Long2IntMap neig = graph.accessNeighborhood(graph.getNode(removed.getId()));
		
		for (long vid : neig.keySet()) {

			if(skippedNode == null) {
//			if (vid != skippedNode.getId()) {

				int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
				int travelTime = removed.getTravelTime() + neig.get(vid);
				TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime, removed.getId());

//				System.out.println("\tNode being expanded: " + vid + ". Distance: " + travelTime);
				
				Edge edge = null;
				int distance = -1;

				if (!wasTraversed.containsKey(vid)) {

					queue.offer(newEntry);
					wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

					distance = neig.get(vid);
					edge = getEdge(removed.getId(), vid, distance);
					parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
				} else {

					int cost = wasTraversed.get(vid);

					if (cost != wasRemoved) {
						if (cost > newEntry.getTravelTime()) {
							
//							System.out.println("Entrou");
							queue.remove(newEntry);
							queue.offer(newEntry);
							wasTraversed.remove(newEntry.getId());
							wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

							parents.remove(vid);
							distance = neig.get(vid);
							edge = getEdge(removed.getId(), vid, distance);
							parents.put(vid,
									new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
						}
					}
				}
			} else {
				if (vid != skippedNode.getId()) {

					int arrivalTime = graph.getArrival(removed.getArrivalTime(), neig.get(vid));
					int travelTime = removed.getTravelTime() + neig.get(vid);
					TimeEntry newEntry = new TimeEntry(vid, travelTime, arrivalTime, removed.getId());

					Edge edge = null;
					int distance = -1;

					if (!wasTraversed.containsKey(vid)) {

						queue.offer(newEntry);
						wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

						distance = neig.get(vid);
						edge = getEdge(removed.getId(), vid, distance);
						parents.put(vid, new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
					} else {

						int cost = wasTraversed.get(vid);

						if (cost != wasRemoved) {
							if (cost > newEntry.getTravelTime()) {
								queue.remove(newEntry);
								queue.offer(newEntry);
								wasTraversed.remove(newEntry.getId());
								wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());

								parents.remove(vid);
								distance = neig.get(vid);
								edge = getEdge(removed.getId(), vid, distance);
								parents.put(vid,
										new RouteEntry(removed.getId(), distance, edge.getId(), edge.getLabel()));
							}
						}
					}
				} else {
					continue;
				}
			}
		}
	}

	private Edge getEdge(long fromNodeId, long toNodeId, int distance) {
		Edge edge = null;
		for (Long outEdge : graph.getOutEdges(fromNodeId)) {
			edge = graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}
		return edge;
	}

}
