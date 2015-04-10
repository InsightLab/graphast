package org.graphast.query.model;

import org.graphast.util.StringUtils;

/**
 * The structure Bound represents the cost from a certain node to a PoI.
 * The cost is represented by the variable 'cost' and the PoI by the 'id' variable.
 * 
 * @author NEX2ME
 *
 */

public class Bound {

	private long id;

	private int cost;

	/**
	 * This constructor creates a cost bound for a given PoI.
	 * 
	 * @param	id	identifier of a node (that is also a PoI).
	 * @param	cost	the cost from a node to the PoI in this constructor.
	 */
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
