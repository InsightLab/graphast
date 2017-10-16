package org.graphast.query.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.graphast.model.Node;

public class DistanceVector {
	private HashMap<Node, DistanceElement> vector;
	
	public DistanceVector(Node source) {
		vector = new HashMap<>();
		DistanceElement first = new DistanceElement(source);
		first.changeDistance(0);
		vector.put(source, first);
	}

	public DistanceElement getElement(Node n) {
		if (!vector.containsKey(n))
			vector.put(n, new DistanceElement(n));
		return vector.get(n);
	}
	
	public void print() {
		for (Node n : vector.keySet()) {
			DistanceElement element = vector.get(n);
			System.out.println("Distance to node " + n + ": " + element.getDistance() + " | Previous node: " + element.getParent());
		}
	}
	
	public void print(Node source, Node target) {
		if (getElement(target).getParent() == null) {
			System.out.println("No path between them");
			return;
		}
		ArrayList<Node> l = new ArrayList<>();
		l.add(target);
		Node parent = target;
		while ((parent = vector.get(parent).getParent()) != null) {
			l.add(0, parent);
			if (parent.equals(source)) break;
		}
		
		for (Node n : l)
			System.out.print(" -> " + n);
		System.out.println();
	}
	
	public double getDistance(Node target) {
		return vector.get(target).getDistance();
	}
}
