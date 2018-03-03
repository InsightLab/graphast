package org.insightlab.graphast.query.map_matching;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.spatial_components.Point;
import org.insightlab.graphast.model.components.spatial_components.SpatialNodeComponent;

public class NaiveMapMatchingStrategy implements MapMatchingStrategy {
	
	private Graph g;
	
	public NaiveMapMatchingStrategy(Graph g) {
		this.g = g;
	}

	@Override
	public Node getNearestNode(double lat, double lng) {
		Point p = new Point(lat, lng);
		Node nearest = null;
		double minDistance = Double.POSITIVE_INFINITY;
		
		for (Node n : g.getNodes()) {
			Point nodePoint = n.getComponent(SpatialNodeComponent.class).getPoint();
			double distance = p.distanceTo(nodePoint);
			if (nearest == null || distance < minDistance) {
				nearest = n;
				minDistance = distance;
			}
		}
		
		return nearest;
		
	}

	@Override
	public Node getNearestEdge(double lat, double lng) {
		// TODO Auto-generated method stub
		return null;
	}

}
