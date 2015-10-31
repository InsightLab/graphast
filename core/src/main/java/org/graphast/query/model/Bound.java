package org.graphast.query.model;

import java.io.Serializable;

import org.graphast.util.StringUtils;

/**
 * The structure Bound represents the cost from a certain node to a PoI.
 * The cost is represented by the variable 'cost' and the PoI by the 'id' variable.
 * 
 * @author NEX2ME
 *
 */

public class Bound implements Serializable {

	private static final long serialVersionUID = 3331624172624494538L;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cost;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bound other = (Bound) obj;
		if (cost != other.cost)
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
