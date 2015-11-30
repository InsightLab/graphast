package org.graphast.query.rnn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DateUtils;

public class RNNDepthFirstSearch implements IRNNTimeDependent {

	private GraphBounds graph;

	public RNNDepthFirstSearch(GraphBounds graphBounds) {
		this.graph = graphBounds;
	}

	public NearestNeighbor search(Node root, Date timeout, Date timestamp) throws PathNotFoundException {

		long maxTravelTimeMillisenconds = DateUtils.dateToMilli(timeout);
		double bestTravelTime = maxTravelTimeMillisenconds;
		long currentPoi = -1;
		Path pathResult = null;

		Dijkstra dijkstraShortestPathLinearFunction = new DijkstraLinearFunction(graph);

		for (Long poi : graph.getPoiIds()) {
			try {
				Node target = graph.getNode(poi);
				Path path = dijkstraShortestPathLinearFunction.shortestPath(
						target.getId(), root.getId(), timestamp);

				if (path.getTotalCost() <= maxTravelTimeMillisenconds
						&& path.getTotalCost() <= bestTravelTime) {
					currentPoi = target.getId();
					bestTravelTime = path.getTotalCost();
					pathResult = path;
				}
			} catch(PathNotFoundException e) {
//				System.err.println(e.getMessage());
			}
		}

		NearestNeighbor nearestNeighbor = createNN(root, currentPoi, pathResult);

		if (nearestNeighbor == null) {
			throw new PathNotFoundException(
					"target not found for root and set timestamp");
		}

		return nearestNeighbor;
	}

	private NearestNeighbor createNN(Node root, long currentPoi, Path path) {

		NearestNeighbor nearestNeighbor = new NearestNeighbor();
		if (currentPoi > -1) {

			nearestNeighbor.setDistance((int) path.getTotalCost());
			nearestNeighbor.setId(currentPoi);

			ArrayList<Long> arrayPath = new ArrayList<Long>();
			List<Long> edges = path.getEdges();

			if (edges != null) {
				for (Long edge : edges) {
					arrayPath.add(graph.getEdge(edge).getFromNode());
				}
			}

			arrayPath.add(root.getId());
			nearestNeighbor.setPath(arrayPath);
		}

		return nearestNeighbor;
	}
}