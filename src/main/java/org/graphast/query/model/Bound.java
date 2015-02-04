package org.graphast.query.model;

import org.graphast.util.StringUtils;



public class Bound {

	//id = identifier of a category.
	private long id;
	private short cost;
	
	public Bound(long id, short cost){
		this.id = id;
		this.cost = cost;
	}
	
	public Bound(){
		this.id = -1;
		this.cost = Short.MAX_VALUE;
	}
	
	public Bound(long id) {
		this.id = id;
		this.cost = Short.MAX_VALUE;
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

	public short getCost() {
		return cost;
	}

	public void setCost(short cost) {
		this.cost = cost;
	}
}
