package org.graphast.query.shortestpath;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.utils.DistanceElement;
import org.graphast.query.utils.DistanceVector;

public class DijkstraStrategy implements ShortestPathStrategy {
	
	private Graph g;
	
	public DijkstraStrategy(Graph g) {
		this.g = g;
	}

	@Override
	public DistanceVector run(Node source) {
		return this.run(source, null);
	}

	@Override
	public DistanceVector run(Node source, Node target) {
		DistanceVector vector = new DistanceVector(source);
		Queue<DistanceElement> toVisit = new PriorityQueue<>();
		
		toVisit.add(vector.getElement(source));
		while(!toVisit.isEmpty()){
			DistanceElement min = toVisit.poll();
			if(target != null && target.equals(min.getNode())){
				return vector;
			}
			
			if(min.isVisited()) continue;
			min.setVisited(true);
			//System.out.println(min.getIndex());
			Iterator<Edge> it = g.getOutEdges(min.getNode());
			while(it.hasNext()) {
				Edge e = it.next();
				DistanceElement neighbor = vector.getElement(e.getAdjacent(min.getNode()));
				if (neighbor.isVisited()) continue;
				
				double newDistance = min.getDistance() + e.getCost();
				if(neighbor.getDistance() > newDistance && !neighbor.isVisited()) {
					neighbor.changeDistance(newDistance);
					neighbor.changePrevious(min.getNode());
					toVisit.add(neighbor);
				}
			}
		}
		//System.out.println(vector.getDistance(target));
		return vector;
	}

}
