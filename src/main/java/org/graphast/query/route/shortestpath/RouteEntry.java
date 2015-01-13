package org.graphast.query.route.shortestpath;

public class RouteEntry {

	private int id;
	private int cost;
	
	public RouteEntry(int id, int cost){
		this.id = id;
		this.cost = cost;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "RouteEntry [id=" + id + ", cost=" + cost + "]";
	}
	
}
