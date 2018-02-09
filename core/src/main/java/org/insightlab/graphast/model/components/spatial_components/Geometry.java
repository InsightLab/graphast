package org.insightlab.graphast.model.components.spatial_components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geometry {
	
	private List<Point> points;
	
	public Geometry() {
		this.points = new ArrayList<>();
	}
	
	public Geometry(Point ...points) {
		this.points.addAll(Arrays.asList(points));
	}
	
	public void addPoint(Point p) {
		this.points.add(p);
	}
	
	public List<Point> getGeometry() {
		return this.points;
	}

}
