package org.graphast.query.model;

import org.graphast.util.StringUtils;



public class Bound {

	//id = identifier of a category.
	private long id;
	private int cost;
	
	public Bound(long id, int cost){
		this.id = id;
		this.cost = cost;
	}
	
	public Bound(){
		this.id = -1;
		this.cost = Integer.MAX_VALUE;
	}
	
	public Bound(long id) {
		this.id = id;
		this.cost = Integer.MAX_VALUE;
	}
	
	public String toString(){
		return StringUtils.append(",", id,cost);
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
}
