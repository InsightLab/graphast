package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BreadthFirstSearchCH {

	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Queue<Long> forwardUnsettleNodes = new LinkedList<>();
	Queue<Long> backwardUnsettleNodes = new LinkedList<>();

	Queue<Long> forwardsSettleNodes = new LinkedList<>();
	Queue<Long> backwardsSettleNodes = new LinkedList<>();

	Node source;
	Node target;

	Path path;

	HashMap<Long, Integer> forwardsUnsettleNodesAux = new HashMap<>();
	HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	long forwardsRemovedNode;
	long backwardsRemovedNode;
	long meetingNode = -1l;
	private CHGraph graph;

	// --METRICS VARIABLES
	int numberOfForwardSettleNodes = 0;
	int numberOfBackwardSettleNodes = 0;
	int numberOfRegularSearches = 0;

	public BreadthFirstSearchCH(CHGraph graph) {

		this.graph = graph;

	}

	public void executeNaiveBFS(Node source, Node target) {

		this.setSource(source);
		this.setTarget(target);

		forwardUnsettleNodes.add(source.getId());
		backwardUnsettleNodes.add(target.getId());

		boolean doForwardSearch = true;
		boolean pathFound = false;
		while (!forwardUnsettleNodes.isEmpty() || !backwardUnsettleNodes.isEmpty()) {

			// Condition to alternate between forward and backward search
			if (forwardUnsettleNodes.isEmpty()) {
				if (backwardSearch()) {
					pathFound = true;
					break;
				}
			} else if (backwardUnsettleNodes.isEmpty()) {
				if (forwardSearch()) {
					pathFound = true;
					break;
				}
			} else {
				if (doForwardSearch) {
					if (forwardSearch()) {
						pathFound = true;
						break;
					}
					doForwardSearch = false;
				} else {
					if (backwardSearch()) {
						pathFound = true;
						break;
					}
					doForwardSearch = true;
				}
			}
		}
		if (!pathFound) {
			System.out.println("Caminho não encontrado!");
		}
	}

	private boolean forwardSearch() {

		forwardsRemovedNode = forwardUnsettleNodes.poll();
		forwardsSettleNodes.add(forwardsRemovedNode);
		numberOfForwardSettleNodes++;

		System.out.println("Nó sendo analizado: " + forwardsRemovedNode);

		expandVertexForward();

		// Stopping criteria of Bidirectional search
		if (meetingNode != -1) {
			System.out.println("Caminho encontrado!");
			return true;
		}

		return false;

	}

	private boolean backwardSearch() {

		backwardsRemovedNode = backwardUnsettleNodes.poll();
		backwardsSettleNodes.add(backwardsRemovedNode);
		numberOfBackwardSettleNodes++;

		System.out.println("Nó sendo analizado: " + backwardsRemovedNode);

		expandVertexBackward();

		// Stopping criteria of Bidirectional search
		if (meetingNode != -1) {
			System.out.println("Caminho encontrado!");
			return true;
		}

		return false;

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode));

		verifyMeetingNodeForwardSearch(forwardsRemovedNode);

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(forwardsRemovedNode).getLevel()) {
				System.out.println("\t Vizinho não considerado: " + vid);
				continue;
			}

			System.out.println("\t Vizinho considerado: " + vid);
			forwardUnsettleNodes.add(vid);

		}
	}

	private void verifyMeetingNodeForwardSearch(long vid) {

		if (backwardsSettleNodes.contains(vid)) {
			meetingNode = (int) vid;
			System.out.println("Meeting node encontrado na busca forward: " + meetingNode);
		}

	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode));

		verifyMeetingNodeBackwardSearch(backwardsRemovedNode);

		for (long vid : neighbors.keySet()) {

			if (graph.getNode(vid).getLevel() < graph.getNode(backwardsRemovedNode).getLevel()) {
				System.out.println("\t Vizinho não considerado: " + vid);
				continue;
			}

			System.out.println("\t Vizinho considerado: " + vid);
			backwardUnsettleNodes.add(vid);

		}
	}

	private void verifyMeetingNodeBackwardSearch(long vid) {

		if (forwardsSettleNodes.contains(vid)) {
			meetingNode = (int) vid;
			System.out.println("Meeting node encontrado na busca backward: " + meetingNode);
		}

	}

	public void setSource(Node source) {
		this.source = source;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public int getNumberOfForwardSettleNodes() {
		return numberOfForwardSettleNodes;
	}

	public int getNumberOfBackwardSettleNodes() {
		return numberOfBackwardSettleNodes;
	}

	public int getNumberOfRegularSearches() {
		return numberOfRegularSearches;
	}

}
