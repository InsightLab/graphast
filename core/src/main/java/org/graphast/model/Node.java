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
	 * this method set a category from a node.
	 * @param	category a category
	 */
	public void setCategory(int category);
	
	/**
	 * This method returns the externalId of this node.
	 * @return the externalId
	 */
	public abstract long getExternalId();

	/**
	 * this method set the externalId from a node.
	 * @param externalId externalId
	 */
	public void setExternalId(long externalId);
	
	/**
	 * This method returns the latitude of this node.
	 * @return the latitude
	 */
	public abstract double getLatitude();

	/**
	 * this method set the latitude from a node.
	 * @param latitude latitude
	 */
	public void setLatitude(double latitude);
	
	/**
	 * This method returns the longitude of this node.
	 * @return the longitude
	 */
	public abstract double getLongitude();

	/**
	 * this method set the longitude from a node.
	 * @param longitude longitude
	 */
	public void setLongitude(double longitude);
	
	/**
	 * This method returns the id of this node.
	 * @return the id
	 */
	public abstract Long getId();
	
	/**
	 * this method set the id from a node.
	 * @param id id
	 */
	public void setId(Long id);
	
	/**
	 * This method returns the label of this node.
	 * @return the label
	 */
	public abstract String getLabel();
	
	/**
	 * this method set the label from a node.
	 * @param label label
	 */
	public void setLabel(String label);
	
	/**
	 * This method returns the costIndex of this node.
	 * @return costIndex
	 */
	public long getCostsIndex();

	/**
	 * toString method
	 * @return String object
	 */
	public abstract String toString();
	
	public abstract int[] getCosts();
	
	public abstract void setCosts(int[] costs);
	
	public boolean equals(Node n);


}