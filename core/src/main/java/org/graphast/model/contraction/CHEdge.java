package org.graphast.model.contraction;

import org.graphast.model.Edge;

public interface CHEdge extends Edge{

	public static final short EDGE_BLOCKSIZE = 8;
	
	public int getOriginalEdgeCounter();
	
	int getContractedNodeIdSegment();

	int getContractedNodeIdOffset();
	
	int getIngoingSkippedEdgeSegment();

	int getIngoingSkippedEdgeOffset();
	
	int getOutgoingSkippedEdgeSegment();

	int getOutgoingSkippedEdgeOffset();
	
	public long getIngoingSkippedEdge();
	
	public long getOutgoingSkippedEdge();
	
	public long getContractedNodeId();
	
	boolean isShortcut();
	
	public void setShortcut(boolean isShortcut);
	
	public void setOriginalEdgeCounter(int originalEdgeCounter);
	
	public void setIngoingSkippedEdge(long ingoingSkippedEdge);
	
	public void setOutgoingSkippedEdge(long outgoingSkippedEdge);
	
	public void setContractedNodeId(long contractedNodeId);
	
}
