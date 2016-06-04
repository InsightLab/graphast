package org.graphast.query.rnn;

import java.util.List;

import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.route.shortestpath.model.TimeEntry;

/**
 * Representa um taxista na malha com a rota do cliente a este.
 *
 */
public class RouteQueueRNNEntry extends TimeEntry {

	private List<NearestNeighbor> routes;

	public RouteQueueRNNEntry(long id, int travelTime, int arrivalTime, long parentId, List<NearestNeighbor> routes) {
		super(id, travelTime, arrivalTime, parentId);
		this.routes = routes;
	}
	
	public List<NearestNeighbor> getRoutes() {
		return routes;
	}

}
