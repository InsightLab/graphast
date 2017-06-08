package org.graphast.query.knn.bidirectionalknn;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.bidirectionalastar.LowerBoundDistanceEntry;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.util.DistanceUtils;

public class SearchEntry implements Comparable<Object> {

	private Queue<LowerBoundDistanceEntry> unsettleNodes = new PriorityQueue<>();
	private Map<Long, Integer> settleNodes = new HashMap<>();
	private Map<Long, RouteEntry> parentNodes = new HashMap<>();
	private DistanceEntry meetingNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);

	Map<Long, Integer> forwardUnsettleNodesAux = new HashMap<>();
	Map<Long, Integer> backwardUnsettleNodesAux = new HashMap<>();

	Node source;
	Node target;

	public SearchEntry(Node source, Node target) {
		this.source = source;
		this.target = target;

		backwardUnsettleNodesAux.put(source.getId(), 0);
		
		int lowerBound = (int) Math.sqrt(Math.pow((target.getLatitude() - source.getLatitude()), 2)
				+ Math.pow((target.getLongitude() - source.getLongitude()), 2));
		unsettleNodes.add(new LowerBoundDistanceEntry(source.getId(), 0, lowerBound, -1));

	}

	public Queue<LowerBoundDistanceEntry> getUnsettleNodes() {
		return unsettleNodes;
	}

	public void setUnsettleNodes(Queue<LowerBoundDistanceEntry> unsettleNodes) {
		this.unsettleNodes = unsettleNodes;
	}

	public Map<Long, Integer> getSettleNodes() {
		return settleNodes;
	}

	public void setSettleNodes(Map<Long, Integer> settleNodes) {
		this.settleNodes = settleNodes;
	}

	public Map<Long, RouteEntry> getParentNodes() {
		return parentNodes;
	}

	public void setParentNodes(Map<Long, RouteEntry> parentNodes) {
		this.parentNodes = parentNodes;
	}

	public DistanceEntry getMeetingNode() {
		return meetingNode;
	}

	public void setMeetingNode(DistanceEntry meetingNode) {
		this.meetingNode = meetingNode;
	}

	public Map<Long, Integer> getForwardUnsettleNodesAux() {
		return forwardUnsettleNodesAux;
	}

	public void setForwardUnsettleNodesAux(Map<Long, Integer> forwardUnsettleNodesAux) {
		this.forwardUnsettleNodesAux = forwardUnsettleNodesAux;
	}

	public Map<Long, Integer> getBackwardUnsettleNodesAux() {
		return backwardUnsettleNodesAux;
	}

	public void setBackwardUnsettleNodesAux(Map<Long, Integer> backwardUnsettleNodesAux) {
		this.backwardUnsettleNodesAux = backwardUnsettleNodesAux;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	@Override
	public int compareTo(Object o) {
		return new Integer(unsettleNodes.peek().getLowerBound())
				.compareTo(((SearchEntry) o).unsettleNodes.peek().getLowerBound());
	}

}
