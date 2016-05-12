package org.graphast.model.contraction;

import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;

import it.unimi.dsi.fastutil.BigArrays;

public class CHEdgeImpl extends EdgeImpl implements CHEdge {

	private int originalEdgeCounter;
	private long contractedNodeId;
	private long ingoingSkippedEdge;
	private long outgoingSkippedEdge;
	private boolean isShortcut;

	public CHEdgeImpl(long fromNode, long toNode, int distance, int originalEdgeCounter) {
		super(fromNode, toNode, distance);
		this.originalEdgeCounter = originalEdgeCounter;
	}
	
	public CHEdgeImpl(long fromNode, long toNode, int distance, int originalEdgeCounter, long contractedNodeId,
			long ingoingSkippedEdge, long outgoingSkippedEdge, boolean isShortcut) {
		super(fromNode, toNode, distance);
		this.originalEdgeCounter = originalEdgeCounter;
		this.contractedNodeId = contractedNodeId;
		this.ingoingSkippedEdge = ingoingSkippedEdge;
		this.outgoingSkippedEdge = outgoingSkippedEdge;
		this.isShortcut = isShortcut;
	}

	public CHEdgeImpl(Edge edge) {

		this.setId(edge.getId());
		this.setExternalId(edge.getExternalId());
		this.setFromNode(edge.getFromNode());
		this.setToNode(edge.getToNode());
		this.setFromNodeNextEdge(edge.getFromNodeNextEdge());
		this.setToNodeNextEdge(edge.getToNodeNextEdge());
		this.setDistance(edge.getDistance());
		this.setCostsIndex(edge.getCostsIndex());
		this.setCosts(edge.getCosts());
		this.setGeometryIndex(edge.getGeometryIndex());
		this.setGeometry(edge.getGeometry());
		this.setLabelIndex(edge.getLabelIndex());
		this.setLabel(edge.getLabel());

	}
	
	public boolean isShortcut() {
		return isShortcut;
	}

	public void setShortcut(boolean isShortcut) {
		this.isShortcut = isShortcut;
	}
	
	public int getOriginalEdgeCounter() {
		return originalEdgeCounter;
	}

	public void setOriginalEdgeCounter(int originalEdgeCounter) {
		this.originalEdgeCounter = originalEdgeCounter;
	}

	public long getContractedNodeId() {
		return contractedNodeId;
	}

	public void setContractedNodeId(int contractedNodeId) {
		this.contractedNodeId = contractedNodeId;
	}

	public int getContractedNodeIdSegment() {
		return BigArrays.segment(contractedNodeId);
	}

	public int getContractedNodeIdOffset() {
		return BigArrays.displacement(contractedNodeId);
	}
	
	public int getIngoingSkippedEdgeSegment() {
		return BigArrays.segment(ingoingSkippedEdge);
	}

	public int getIngoingSkippedEdgeOffset() {
		return BigArrays.displacement(ingoingSkippedEdge);
	}
	
	public int getOutgoingSkippedEdgeSegment() {
		return BigArrays.segment(outgoingSkippedEdge);
	}

	public int getOutgoingSkippedEdgeOffset() {
		return BigArrays.displacement(outgoingSkippedEdge);
	}

	public long getIngoingSkippedEdge() {
		return ingoingSkippedEdge;
	}

	public void setIngoingSkippedEdge(long ingoingSkippedEdge) {
		this.ingoingSkippedEdge = ingoingSkippedEdge;
	}

	public long getOutgoingSkippedEdge() {
		return outgoingSkippedEdge;
	}

	public void setOutgoingSkippedEdge(long outgoingSkippedEdge) {
		this.outgoingSkippedEdge = outgoingSkippedEdge;
	}

	public void setContractedNodeId(long contractedNodeId) {
		this.contractedNodeId = contractedNodeId;
	}
	
	
	
}
