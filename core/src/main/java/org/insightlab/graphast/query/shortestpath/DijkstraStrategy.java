package org.insightlab.graphast.query.shortestpath;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.insightlab.graphast.exceptions.NodeNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.query.utils.DistanceElement;
import org.insightlab.graphast.query.utils.DistanceVector;

public class DijkstraStrategy implements ShortestPathStrategy {
	
	private Graph g;
	
	public DijkstraStrategy(Graph g) {
		this.g = g;
	}

	@Override
	public DistanceVector run(long sourceId) throws NodeNotFoundException {
		return this.run(sourceId, -1);
	}

	@Override
	public DistanceVector run(long sourceId, long targetId) throws NodeNotFoundException {
		if(!g.containsNode(sourceId))
			throw new NodeNotFoundException(sourceId);
		if(targetId != -1 && !g.containsNode(targetId))
			throw new NodeNotFoundException(targetId);
		
		DistanceVector vector = new DistanceVector(sourceId);
		Queue<DistanceElement> toVisit = new PriorityQueue<>();
		
		toVisit.add(vector.getElement(sourceId));
		
		while(!toVisit.isEmpty()){
			DistanceElement min = toVisit.poll();
			
			if(targetId != -1 && targetId == min.getNodeId())
				return vector;
			
			if(!min.isVisited()) visitNode(min, vector, toVisit);
		}
		//System.out.println(vector.getDistance(target));
		return vector;
	}
	
	private void visitNode(DistanceElement node, DistanceVector vector, Queue<DistanceElement> toVisit) {
		node.setVisited(true);
		//System.out.println(min.getIndex());
		Iterator<Edge> it = g.getOutEdges(node.getNodeId());
		while(it.hasNext()) {
			Edge e = it.next();
			DistanceElement neighbor = vector.getElement(e.getAdjacent(node.getNodeId()));
			
			if (!neighbor.isVisited()) relaxPath(e, node, neighbor, toVisit);
		}
	}
	
	private void relaxPath(Edge e, DistanceElement node, DistanceElement neighbor, Queue<DistanceElement> toVisit) {
		double newDistance = node.getDistance() + e.getCost();
		if(neighbor.getDistance() > newDistance && !neighbor.isVisited()) {
			neighbor.changeDistance(newDistance);
			neighbor.changePrevious(node.getNodeId());
			toVisit.add(neighbor);
		}
	}

}
