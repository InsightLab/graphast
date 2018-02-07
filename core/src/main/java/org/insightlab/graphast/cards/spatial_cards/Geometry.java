package org.insightlab.graphast.cards.spatial_cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geometry {
	
	private List<Point> geometry;
	
	public Geometry() {
		geometry = new ArrayList<>();
	}
	
	public Geometry(Point ...points) {
		geometry.addAll(Arrays.asList(points));
	}
	
	public void addPoint(Point p) {
		geometry.add(p);
	}
	
	public List<Point> getGeometry() {
		return geometry;
	}

}
