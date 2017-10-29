package org.graphast.query.shortestpath;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.query.utils.DistanceElement;
import org.graphast.query.utils.DistanceVector;

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
