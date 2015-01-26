package org.graphast.model;

public interface Node {
	
	/**
	 * Blocksize length of one node on IntBigArrayBigList.  
	 */
	public static final short NODE_BLOCKSIZE = 11;
	
	/**
	 * This method validates an node if latitude and longitude are different from 0 ,
	 * and if the node has at least one edge connected.
	 */
	public abstract void validate();

	/**
	 * This method returns the category of this node.
	 * @return the category
	 */
	public abstract int getCategory();

	/**
	 * This method returns the externalId of this node.
	 * @return the externalId
	 */
	public abstract long getExternalId();

	/**
	 * This method returns the latitude of this node.
	 * @return the latitude
	 */
	public abstract double getLatitude();

	/**
	 * This method returns the longitude of this node.
	 * @return the longitude
	 */
	public abstract double getLongitude();

	/**
	 * This method returns the id of this node.
	 * @return the id
	 */
	public abstract Long getId();
	
	/**
	 * This method returns the label of this node.
	 * @return the label
	 */
	public abstract String getLabel();

	/**
	 * toString method
	 * @return String object
	 */
	public abstract String toString();

}