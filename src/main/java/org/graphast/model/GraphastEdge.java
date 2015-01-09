package org.graphast.model;

import it.unimi.dsi.fastutil.BigArrays;

import java.util.Arrays;
import java.util.List;

import org.graphast.exception.GraphastException;
import org.graphast.geometry.Point;

public class GraphastEdge {
	//private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final short EDGE_BLOCKSIZE = 17;

	private Long id;

	private long externalId;

	private long fromNode;

	private long toNode;

	private long fromNodeNextEdge;

	private long toNodeNextEdge;

	private int distance;

	private long costsIndex;

	private short[] costs;

	private long geometryIndex;

	private List<Point> geometry;

	private long labelIndex;

	private String label;

	public GraphastEdge(Long id, long fromNode, long toNode, int distance,
			short[] costs, List<Point> geometry, String label) {

		this(fromNode, toNode, distance);
		this.id = id;
		this.costs = costs;
		this.geometry = geometry;
		this.label = label;

	}

	public GraphastEdge(long fromNode, long toNode, int distance) {

		this(null, 0, fromNode, toNode, -1, -1, distance, -1, -1, -1, null);

	}

	public GraphastEdge(long externalId, long fromNode, long toNode, int distance) {
		this(null, externalId, fromNode, toNode, -1, -1, distance, -1, -1, -1, null);
	}

	public GraphastEdge(Long id, long fromNode, long toNode,
			long fromNodeNextEdge, long toNodeNextEdge, int distance,
			long costsIndex, long geometryIndex, long labelIndex,
			String label) {
		this(id, 0, fromNode, toNode, fromNodeNextEdge, toNodeNextEdge, distance, costsIndex, geometryIndex, labelIndex, label);

	}

	public GraphastEdge(Long id, long externalId, long fromNode, long toNode,
			long fromNodeNextEdge, long toNodeNextEdge, int distance,
			long costsIndex, long geometryIndex,long labelIndex, String label) {

		this.fromNode = fromNode;
		this.toNode = toNode;
		this.distance = distance;
		this.id = id;
		this.fromNodeNextEdge = fromNodeNextEdge;
		this.toNodeNextEdge = toNodeNextEdge;
		this.costsIndex = costsIndex;
		this.labelIndex = labelIndex;
		this.geometryIndex = geometryIndex;
		this.label = label;
		this.externalId = externalId;
		validate();
	
	}

	public void validate(){
		
		if(distance < 0){
			
			throw new GraphastException("Invalid edge");
		
		}
		
		if(fromNode == 0 && toNode == 0 && fromNodeNextEdge == 0 && toNodeNextEdge == 0) {
			
			throw new GraphastException("Invalid edge");
		
		}

		if(fromNode == toNode) {
			
			//logger.error("Invalid edge: {}", toString());
			throw new GraphastException("Invalid edge: fromNode == toNode");
		
		}

		if(getId() != null && (getFromNodeNextEdge() == getId() || getToNodeNextEdge() == getId())) {
			
			//logger.error("Invalid edge: {}", toString());
			throw new GraphastException("Invalid edge: edgeId == nodeNextEdge");
		
		}
	
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getExternalIdSegment() {
		return BigArrays.segment(externalId);
	}

	public int getExternalIdOffset() {
		return BigArrays.displacement(externalId);
	}

	public long getFromNode() {
		return fromNode;
	}

	public void setFromNode(long fromNode) {
		this.fromNode = fromNode;
	}

	public long getToNode() {
		return toNode;
	}

	public void setToNode(long toNode) {
		this.toNode = toNode;
	}

	public long getFromNodeNextEdge() {
		return fromNodeNextEdge;
	}

	public void setFromNodeNextEdge(long fromNodeNextEdge) {
		this.fromNodeNextEdge = fromNodeNextEdge;
	}

	public long getToNodeNextEdge() {
		return toNodeNextEdge;
	}

	public void setToNodeNextEdge(long toNodeNextEdge) {
		this.toNodeNextEdge = toNodeNextEdge;
	}

	public int getFromNodeSegment() {
		return BigArrays.segment(fromNode);
	}

	public int getFromNodeOffset() {
		return BigArrays.displacement(fromNode);
	}

	public int getToNodeSegment() {
		return BigArrays.segment(toNode);
	}

	public int getToNodeOffset() {
		return BigArrays.displacement(toNode);
	}

	public int getCostsSegment() {

		return BigArrays.segment(costsIndex);
	}

	public int getCostsOffset() {
		return BigArrays.displacement(costsIndex);
	}

	public int getGeometrySegment() {
		return BigArrays.segment(geometryIndex);
	}

	public int getGeometryOffset() {
		return BigArrays.displacement(geometryIndex);
	}

	public long getCostsIndex() {
		return costsIndex;
	}

	public void setCostsIndex(long costsIndex) {
		this.costsIndex = costsIndex;
	}

	public long getGeometryIndex() {
		return geometryIndex;
	}

	public void setGeometryIndex(long geometryIndex) {
		this.geometryIndex = geometryIndex;
	}

	public short[] getCosts() {
		return costs;
	}

	public void setCosts(short[] costs) {
		this.costs = costs;
	}

	public List<Point> getGeometry() {
		return geometry;
	}

	public void setGeometry(List<Point> geometry) {
		this.geometry = geometry;
	}

	public int getLabelIndexSegment(){
		return BigArrays.segment(labelIndex);
	}

	public int getLabelIndexOffset(){
		return BigArrays.displacement(labelIndex);
	}

	public void setLabelIndex(long labelIndex) {
		this.labelIndex = labelIndex;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getFromNodeNextEdgeSegment(){
		return BigArrays.segment(fromNodeNextEdge);
	}

	public int getFromNodeNextEdgeOffset(){
		return BigArrays.displacement(fromNodeNextEdge);
	}

	public int getToNodeNextEdgeSegment(){
		return BigArrays.segment(toNodeNextEdge);
	}

	public int getToNodeNextEdgeOffset(){
		return BigArrays.displacement(toNodeNextEdge);
	}

	public void setExternalId(long externalID) {
		this.externalId = externalID;
	}

	@Override
	public String toString() {
		return "FastGraphEdge [id=" + id + ", externalId=" + externalId + ", fromNode=" + fromNode
				+ ", toNode=" + toNode + ", fromNodeNextEdge="
				+ fromNodeNextEdge + ", toNodeNextEdge=" + toNodeNextEdge
				+ ", distance=" + distance + ", costsIndex=" + costsIndex
				+ ", costs=" + Arrays.toString(costs) + ", geometryIndex="
				+ geometryIndex + ", geometry=" + geometry + ", label=" + label
				+ "]";
	}

}
