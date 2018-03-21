package org.insightlab.graphast.query.map_matching;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.spatial_components.Point;
import org.insightlab.graphast.model.components.spatial_components.SpatialEdgeComponent;
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

	private double distanceBetweenLineAndPoint(Point pFrom, Point pTo, Point p) {
		double n1 = (pTo.getLng() - pFrom.getLng()) * p.getLat();
		double n2 = (pTo.getLat() - pFrom.getLat()) * p.getLng();
		double n3 = pTo.getLat() * pFrom.getLng() - pTo.getLng() - pFrom.getLat();
		return (n1 - n2 + n3) / pFrom.distanceTo(pTo);
	}

	@Override
	public Edge getNearestEdge(double lat, double lng) {
		Point p = new Point(lat, lng);
		Edge nearest = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Edge e : g.getEdges()) {
			Point pFrom = g.getNode(e.getFromNodeId()).getComponent(SpatialNodeComponent.class).getPoint();
			Point pTo = g.getNode(e.getToNodeId()).getComponent(SpatialNodeComponent.class).getPoint();
			double distance = distanceBetweenLineAndPoint(pFrom, pTo, p);
			if (nearest == null || distance < minDistance) {
				nearest = e;
				minDistance = distance;
			}
		}
		return nearest;
	}

}
