package org.graphast.query.knn.model;

import org.graphast.util.StringUtils;


public class Bound {
	private long id;
	private int distance;
	
	public Bound(long id, int distance){
		this.id = id;
		this.distance = distance;
	}
	
	public Bound(){
		this.id = -1;
		this.distance = Integer.MAX_VALUE;
	}
	
	public Bound(String s){
		read(s);
	}
	
	public String toString(){
		return StringUtils.append(",", id,distance);
	}
	
	public void read(String s){
		int[] bound = StringUtils.splitInt(",", s);
		this.id = bound[0];
		this.distance = bound[1];
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
