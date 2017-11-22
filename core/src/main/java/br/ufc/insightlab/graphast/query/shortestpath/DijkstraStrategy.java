package br.ufc.insightlab.graphast.query.shortestpath;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import br.ufc.insightlab.graphast.exceptions.NodeNotFoundException;
import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.query.utils.DistanceElement;
import br.ufc.insightlab.graphast.query.utils.DistanceVector;

public class DijkstraStrategy implements ShortestPathStrategy {
	
	private Graph g;
	
	public DijkstraStrategy(Graph g) {
		this.g = g;
	}

	@Override
	public DistanceVector run(long sourceId) {
		return this.run(sourceId, -1);
	}

	@Override
	public DistanceVector run(long sourceId, long targetId) {
		if(!g.containsNode(sourceId))
			throw new NodeNotFoundException(sourceId);
		if(targetId != -1 && !g.containsNode(targetId))
			throw new NodeNotFoundException(targetId);
		
		DistanceVector vector = new DistanceVector(sourceId);
		Queue<DistanceElement> toVisit = new PriorityQueue<>();
		
		toVisit.add(vector.getElement(sourceId));
		while(!toVisit.isEmpty()){
			DistanceElement min = toVisit.poll();
			if(targetId != -1 && targetId == min.getNodeId()){
				return vector;
			}
			
			if(min.isVisited()) continue;
			min.setVisited(true);
			//System.out.println(min.getIndex());
			Iterator<Edge> it = g.getOutEdges(min.getNodeId());
			while(it.hasNext()) {
				Edge e = it.next();
				DistanceElement neighbor = vector.getElement(e.getAdjacent(min.getNodeId()));
				if (neighbor.isVisited()) continue;
				
				double newDistance = min.getDistance() + e.getCost();
				if(neighbor.getDistance() > newDistance && !neighbor.isVisited()) {
					neighbor.changeDistance(newDistance);
					neighbor.changePrevious(min.getNodeId());
					toVisit.add(neighbor);
				}
			}
		}
		//System.out.println(vector.getDistance(target));
		return vector;
	}

}
