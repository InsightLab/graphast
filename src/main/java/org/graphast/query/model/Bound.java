package org.graphast.query.model;

import org.graphast.util.StringUtils;



public class Bound {

	//id = identifier of a category.
	private long id;
	//distance = the distance from a vertex to the current category (represented by the id above).
	private int distance;
	
	public Bound(long id, int distance){
		this.id = id;
		this.distance = distance;
	}
	
	public Bound(){
		this.id = -1;
		this.distance = Integer.MAX_VALUE;
	}
	
	public Bound(long id) {
		this.id = id;
		this.distance = Integer.MAX_VALUE;
	}
	
	public String toString(){
		return StringUtils.append(",", id,distance);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
