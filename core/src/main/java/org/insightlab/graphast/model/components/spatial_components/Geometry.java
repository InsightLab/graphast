package org.insightlab.graphast.model.components.spatial_components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geometry implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 955338995597901145L;
	
	private List<Point> points;
	
	public Geometry() {
		this.points = new ArrayList<>();
	}
	
	public Geometry(Point ...points) {
		this();
		this.points.addAll(Arrays.asList(points));
	}
	
	public void addPoint(Point p) {
		this.points.add(p);
	}
	
	public List<Point> getPoints() {
		return this.points;
	}

}
