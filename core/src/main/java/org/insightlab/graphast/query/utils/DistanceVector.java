/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package org.insightlab.graphast.query.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Distance Vector class. This class is used in the shortest path strategy calculation to store and manipulate the elements
 * that constitute the path between a source and a target. It has a HasMap attribute that contains a group of (key, value)
 * tuples to access the DistanceElements objects that represent this distance vector, where the key is the node's id and the value
 * is a DistanceElement object representing the node in a DistanceVector instance.
 */
public class DistanceVector {
	
	private HashMap<Long, DistanceElement> vector;
	
	/**
	 * Instantiates a new distance vector, given a source node id. The DistanceVector object is initially set with only one
	 * DistanceElement object in its HashMap, representing the source node with the given id. The source node is initialized
	 * with a distance value of 0.
	 *
	 * @param sourceId the source node id of the distance vector.
	 */
	public DistanceVector(long sourceId) {
		
		vector = new HashMap<>();
		DistanceElement first = new DistanceElement(sourceId);
		
		first.changeDistance(0);
		vector.put(sourceId, first);
		
	}

	/**
	 * Gets an element of the distance vector, based on the given node id. If a DistanceElement object with the given id does
	 * not exist in the vector, a new object with the id is added to it. Then, the element with the given id as key is returned
	 * in the end.
	 *
	 * @param id the id whose associated DistanceElement object is to be returned
	 * @return the value to which the specified id is mapped
	 */
	public DistanceElement getElement(long id) {
		
		if (!vector.containsKey(id))
			vector.put(id, new DistanceElement(id));
		
		return vector.get(id);
		
	}
	
	/**
	 * This function prints the current state of the distance vector, e.g. the nodes constituting this vector and their attributes.
	 */
	public void print() {
		
		for (Long n : vector.keySet()) {
			DistanceElement element = vector.get(n);
			System.out.println("Distance to node " + n + ": " + element.getDistance() + " | Previous node: " + element.getParentId());
		}
		
	}
	
	/**
	 * Prints the path between a source node and a target node, if it exists. If it does not exist, a message indicating this is shown.
	 * Is a path does exist, a sequence of nodes is printed to show the path that the nodes share.
	 *
	 * @param sourceId the id of the source node in the graph.
	 * @param targetId the id of the target node in the graph.
	 */
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
			
			if (parentId == sourceId) 
				break;
			
		}
		
		for (Long id : l)
			System.out.print(" -> " + id);
		
		System.out.println();
		
	}
	
	/**
	 * Gets the distance value of the target node in this distance vector, as calculated by some shortest path strategy.
	 *
	 * @param targetId the id of the target node in the graph.
	 * @return the distance value of the target node.
	 */
	public double getDistance(long targetId) {
		return vector.get(targetId).getDistance();
	}
}
