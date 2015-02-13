package org.graphast.query.model;

import org.graphast.util.StringUtils;

/**
 * The structure Bound represents the distance from a certain node to a PoI.
 * The distance is represented by the variable 'distance' and the PoI by the 'id' variable.
 * 
 * @author NEX2ME
 *
 */

public class Bound {

	private long id;
	private int distance;
	
	/**
	 * This constructor creates a distance bound for a given PoI.
	 * 
	 * @param	id	identifier of a node (that is also a PoI).
	 * @param	distance	the distance from a node to the PoI in this constructor.
	 */
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
