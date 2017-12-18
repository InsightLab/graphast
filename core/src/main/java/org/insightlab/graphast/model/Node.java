package org.insightlab.graphast.model;

/**
 * The Node class. It represents the model of a graph node.
 * It extends the GraphObject abstract class.
 */
public class Node extends GraphObject {
	
	private long id;
	
	/**
	 * Instantiates a new node.
	 *
	 * @param id the id of the new node.
	 */
	public Node(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the id from the node.
	 *
	 * @return the id from the node (long).
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Equals method. It receives an object and checks if it is a Node
	 * and if it has the same id than the Node object calling this function.
	 *
	 * @param obj the object to check equality.
	 * @return true, if it is a Node object and has the same id than the Node
	 * instance calling this function. false, otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) return false;
		return ((Node)obj).getId() == id;
 	}
	
	/**
	 * Hash code function of the object (overridden).
	 *
	 * @return the 32-bit signed integer hash value of the Node's instance id.
	 */
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

}
