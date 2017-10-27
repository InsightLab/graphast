package org.graphast.query.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DistanceVector {
	private HashMap<Integer, DistanceElement> vector;
	
	public DistanceVector(int sourceId) {
		vector = new HashMap<>();
		DistanceElement first = new DistanceElement(sourceId);
		first.changeDistance(0);
		vector.put(sourceId, first);
	}

	public DistanceElement getElement(int id) {
		if (!vector.containsKey(id))
			vector.put(id, new DistanceElement(id));
		return vector.get(id);
	}
	
	public void print() {
		for (Integer n : vector.keySet()) {
			DistanceElement element = vector.get(n);
			System.out.println("Distance to node " + n + ": " + element.getDistance() + " | Previous node: " + element.getParentId());
		}
	}
	
	public void print(int sourceId, int targetId) {
		if (getElement(targetId).getParentId() == -1) {
			System.out.println("No path between them");
			return;
		}
		ArrayList<Integer> l = new ArrayList<>();
		l.add(targetId);
		int parentId = targetId;
		while ((parentId = vector.get(parentId).getParentId()) != -1) {
			l.add(0, parentId);
			if (parentId == sourceId) break;
		}
		
		for (Integer id : l)
			System.out.print(" -> " + id);
		System.out.println();
	}
	
	public double getDistance(int targetId) {
		return vector.get(targetId).getDistance();
	}
}
