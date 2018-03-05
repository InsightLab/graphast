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

package org.insightlab.graphast.utils;

import static org.junit.Assert.*;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import org.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import org.insightlab.graphast.query.utils.DistanceVector;
import org.insightlab.graphast.serialization.SerializationUtils;
import org.insightlab.graphast.utils.RandomGraphGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RandomGraphGeneratorTest {
	
	public static Graph g;
	public static final int nNodes = 2000;
	public static final float density = 0.01f;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RandomGraphGenerator generator = new RandomGraphGenerator();
		g = generator.generateRandomMMapGraph("random_graph", nNodes, density);
	}

	@Test
	public void testSizes() {
		assertEquals("Number of generated nodes", nNodes, g.getNumberOfNodes());
	}
	
	@Test
	public void testDijkstra() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(10);
		vector.print(10, 40);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		SerializationUtils.deleteMMapGraph("graphs/MMap/random_graph");
	}

}
