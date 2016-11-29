package org.graphast.query.route.shortestpath.model;

/**
 * Class use for reconstruct path in Dijkstra and AStar.
 * Also Use to constructPath with class Path
 * It's a little bit different from another entries in this package but the idea is the same.
 */
public class RouteEntry {

	private long id;
	private int cost;
	private long edgeId;
	private String label;
	
	public RouteEntry(long id, int cost, long edgeId, String label){
		this.id = id;
		this.cost = cost;
		this.edgeId = edgeId;
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

	public long getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(long edgeId) {
		this.edgeId = edgeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "RouteEntry [id=" + id + ", cost=" + cost + ", edgeId=" + edgeId
				+ ", label=" + label + "]";
	}
	
}
