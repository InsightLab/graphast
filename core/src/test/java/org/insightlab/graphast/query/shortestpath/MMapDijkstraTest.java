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

package org.insightlab.graphast.query.shortestpath;

import static org.junit.Assert.assertEquals;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.query.utils.DistanceVector;
import org.insightlab.graphast.serialization.SerializationUtils;
import org.insightlab.graphast.structure.MMapGraphStructure;
import org.junit.BeforeClass;
import org.junit.Test;

public class MMapDijkstraTest {
	
	private static Graph g;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String graphPath = "graphs/MMap/dijkstra_graph";
		
		SerializationUtils.deleteMMapGraph(graphPath);
		g = new Graph(new MMapGraphStructure(graphPath));
		
		Node n0 = new Node(0);
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Edge e0 = new Edge(0, 1, 3);
		e0.setBidirectional(true);
		Edge e1 = new Edge(1, 2, 3);
		e1.setBidirectional(true);
		Edge e2 = new Edge(2, 3, 3);
		e2.setBidirectional(true);
		Edge e3 = new Edge(0, 4, 5);
		e3.setBidirectional(true);
		Edge e4 = new Edge(4, 3, 5);
		e4.setBidirectional(true);
		Edge e5 = new Edge(0, 5, 15);
		e5.setBidirectional(true);
		g.addNodes(n0, n1, n2, n3, n4, n5);
		g.addEdges(e0, e1, e2, e3, e4, e5);
	}

	@Test
	public void testOneToAll() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(0);
		assertEquals("One to All distance test n3", 9, vector.getDistance(3), 0);
		assertEquals("One to All distance test n2", 6, vector.getDistance(2), 0);
		assertEquals("One to All distance test n4", 5, vector.getDistance(4), 0);
		assertEquals("One to All Parent test n3", 2, vector.getElement(3).getParentId());
		assertEquals("One to All Parent test n2", 1, vector.getElement(2).getParentId());
		assertEquals("One to All Parent test n4", 0, vector.getElement(4).getParentId());
	}

	@Test
	public void testOneToOne() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(3, 1);
		assertEquals("One to One distance test n1", 6, vector.getDistance(1), 0);
		assertEquals("One to One Parent test n5", -1, vector.getElement(5).getParentId());
		assertEquals("One to One Parent test n1", 2, vector.getElement(1).getParentId());
	}

}
