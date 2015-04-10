package org.graphast.query.route.shortestpath.model;

public class RouteEntry {

	private long id;
	private int cost;
	private String label;
	
	public RouteEntry(long id, int cost, String label){
		this.id = id;
		this.cost = cost;
		this.label = label;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "RouteEntry [id=" + id + ", cost=" + cost + ", label=" + label + "]";
	}
	
}
