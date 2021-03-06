package br.ufc.insightlab.graphast.model.components.spatial_components;

import br.ufc.insightlab.graphast.model.components.NodeComponent;

public class SpatialNodeComponent extends NodeComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4231579086439483117L;
	
	private Point point;
	
	public SpatialNodeComponent() {
		this(new Point());
	}
	
	public SpatialNodeComponent(double lat, double lng) {
		this(new Point(lat, lng));
	}
	
	public SpatialNodeComponent(Point point) {
		this.point = point;
	}
	
	public double getLat() {
		return point.getLat();
	}
	
	public double getLng() {
		return point.getLng();
	}
	
	public void setLat(double lat) {
		point.setLat(lat);
	}
	
	public void setLng(double lng) {
		point.setLng(lng);
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}

}
