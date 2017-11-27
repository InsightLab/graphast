package org.insightlab.graphast.query.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DistanceVector {
	private HashMap<Long, DistanceElement> vector;
	
	public DistanceVector(long sourceId) {
		vector = new HashMap<>();
		DistanceElement first = new DistanceElement(sourceId);
		first.changeDistance(0);
		vector.put(sourceId, first);
	}

	public DistanceElement getElement(long id) {
		if (!vector.containsKey(id))
			vector.put(id, new DistanceElement(id));
		return vector.get(id);
	}
	
	public void print() {
		for (Long n : vector.keySet()) {
			DistanceElement element = vector.get(n);
			System.out.println("Distance to node " + n + ": " + element.getDistance() + " | Previous node: " + element.getParentId());
		}
	}
	
	public void print(long sourceId, long targetId) {
		if (getElement(targetId).getParentId() == -1) {
			System.out.println("No path between them");
			return;
		}
		ArrayList<Long> l = new ArrayList<>();
		l.add(targetId);
		long parentId = targetId;
		while ((parentId = vector.get(parentId).getParentId()) != -1l) {
			l.add(0, parentId);
			if (parentId == sourceId) break;
		}
		
		for (Long id : l)
			System.out.print(" -> " + id);
		System.out.println();
	}
	
	public double getDistance(long targetId) {
		return vector.get(targetId).getDistance();
	}
}
