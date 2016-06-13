package org.graphast.model;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.BigArrays;

import org.graphast.exception.GraphastException;

public class NodeImpl implements Node {

	private Long id;

	private long externalId;

	private int category;

	private int latitude;

	private int longitude;

	private long firstEdge;

	private long labelIndex;

	private long costsIndex;

	private int[] costs;

	private String label;

	public NodeImpl() {

	}

	/**
	 * This method will construct a FastGraphNode with the latitude and longitude passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * @param	latitude Node latitude.	
	 * @param 	longitude Node longitude.
	 */
	public NodeImpl(double latitude, double longitude) {
		setLatitude(latitude);
		setLongitude(longitude);
		this.firstEdge = -1;

		if(latitude == Integer.MAX_VALUE || longitude==Integer.MAX_VALUE) {
			throw new GraphastException("Invalid coordinate");
		}
	}

	/**
	 * This method will construct a FastGraphNode with the latitude, longitude
	 * externalId, category, firstEdge, labelIndex and costIndex passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param externalId
	 * @param category
	 * @param latitude
	 * @param longitude
	 * @param firstEdge
	 * @param labelIndex
	 * @param costIndex
	 */
	NodeImpl(long externalId, int category, double latitude, 
			double longitude, long firstEdge, long labelIndex, long costIndex, int[] costs) {

		this(latitude, longitude);
		this.externalId = externalId;
		this.category = category;
		this.firstEdge = firstEdge;
		this.labelIndex = labelIndex;
		this.costsIndex = costIndex;
		this.costs = costs;
	}

	NodeImpl(long externalId, int category, double latitude, 
			double longitude, long firstEdge, long labelIndex, long costIndex) {

		this(latitude, longitude);
		this.externalId = externalId;
		this.category = category;
		this.firstEdge = firstEdge;
		this.labelIndex = labelIndex;
		this.costsIndex = costIndex;
	}

	/**
	 * This method will construct a FastGraphNode with the externalId, latitude and longitude passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param	externalId Node id used in the dataset from where the data was imported.
	 * @param	latitude Node latitude.	
	 * @param 	longitude Node longitude.
	 */
	public NodeImpl(long externalId, double latitude, double longitude) {

		this(latitude, longitude);
		this.externalId = externalId;
		this.category = -1;

	}

	/**
	 * This method will construct a FastGraphNode with the externalId, latitude, longitude and
	 * label passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param	externalId Node id used in the dataset from where the data was imported.
	 * @param	latitude Node latitude.	
	 * @param 	longitude Node longitude.
	 * @param   label Node label.
	 */
	public NodeImpl(long externalId, double latitude, double longitude, String label) {

		this(externalId, latitude, longitude);
		this.label = label;

	}
	public NodeImpl(long externalId, double latitude, double longitude, int category) {

		this(externalId, latitude, longitude);
		this.category = category;
	}

	public NodeImpl(long externalId, double latitude, double longitude, int[] costs) {

		this(externalId, latitude, longitude);
		this.costs = costs;

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#validate()
	 */
	@Override
	public void validate(){

		if(latitude == 0 && longitude == 0 && firstEdge == 0){
			throw new GraphastException("Invalid vertex");
		}

	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getCategory()
	 */
	@Override
	public int getCategory() {
		return category;
	}

	@Override
	public void setCategory(int category) {
		this.category = category;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getExternalId()
	 */
	@Override
	public long getExternalId() {
		return externalId;
	}

	protected int getExternalIdSegment(){
		return BigArrays.segment(externalId);
	}

	protected int getExternalIdOffset(){
		return BigArrays.displacement(externalId);
	}

	@Override
	public void setExternalId(long externalId) {
		this.externalId = externalId;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getLatitude()
	 */
	@Override
	public double getLatitude() {
		return latLongToDouble(latitude);
	}

	protected int getLatitudeConvertedToInt() {
		return latitude;
	}


	@Override
	public void setLatitude(double latitude) {
		this.latitude = latLongToInt(latitude);
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getLongitude()
	 */
	@Override
	public double getLongitude() {
		return latLongToDouble(longitude);
	}

	protected int getLongitudeConvertedToInt() {
		return longitude;
	}

	@Override
	public void setLongitude(double longitude) {
		this.longitude = latLongToInt(longitude);
	}

	protected int getFirstEdgeSegment(){
		return BigArrays.segment(firstEdge);
	}

	protected int getFirstEdgeOffset(){
		return BigArrays.displacement(firstEdge);
	}

	protected void setFirstEdge(long firstEdge) {
		this.firstEdge = firstEdge;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	protected long getLabelIndex(){
		return labelIndex;
	}

	protected int getLabelIndexSegment(){
		return BigArrays.segment(labelIndex);
	}

	protected int getLabelIndexOffset(){
		return BigArrays.displacement(labelIndex);
	}

	protected void setLabelIndex(long labelIndex) {
		this.labelIndex = labelIndex;
	}

	protected int getCostsIndexSegment(){
		return BigArrays.segment(costsIndex);
	}

	protected int getCostsIndexOffset(){
		return BigArrays.displacement(costsIndex);
	}

	public long getCostsIndex() {
		return costsIndex;
	}

	protected void setCostsIndex(long costIndex) {
		this.costsIndex = costIndex;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int[] getCosts() {
		return costs;
	}

	@Override
	public void setCosts(int[] costs) {
		this.costs = costs;
	}

	/* (non-Javadoc)
	 * @see org.graphast.model.Node#toString()
	 */
	@Override
	public String toString() {
		return "FastGraphNode [id=" + id + ", externalId=" + externalId + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", firstEdge=" + firstEdge + ", label="+ label + "]";
	}
	
	public boolean equals(Node n) {
		if((n.getLatitude() == this.getLatitude() && (n.getLongitude() == this.getLongitude())
				&& n.getCategory() == this.getCategory())) {
			return true;
		}
		return false;
	}

}
