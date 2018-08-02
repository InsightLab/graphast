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

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.serialization.SerializationUtils;
import org.insightlab.graphast.structure.MMapGraphStructure;

import java.util.Random;

/**
 * This class implements a generator for random graphs.
 *
 */
public class RandomGraphGenerator {
	
	/**
	 * Create a new RandomGraphGenerator using no information.
	 */
	public RandomGraphGenerator() {}
	
	/**
	 * Create a new Graph which has the given graphName, size and dens.
	 * @param graphName the graph's name.
	 * @param size the graph's size.
	 * @param dens the graph's dens.
	 * @return the graph generated.
	 */
	public Graph generateRandomMMapGraph(String graphName, int size, float dens) {
		
		Graph graph = null;
		String dir = "graphs/MMap/" + graphName;
		
		SerializationUtils.deleteMMapGraph(dir);
		graph = new Graph(new MMapGraphStructure(dir));
		
		Random rand = new Random();
		
		for (int i = 0; i < size; i++)
			graph.addNode(new Node(i));
		
		for (int i = 0; i < size; i++) {
			for (int j = i+1; j < size; j++) {
				
				if (rand.nextFloat() < dens) {
					int randomCost = Math.abs(rand.nextInt(50)) + 1;
					Edge e = new Edge(i, j, randomCost);
					e.setBidirectional(rand.nextBoolean());
					
					graph.addEdge(e);
				}
			
			}
		}

		return graph;
	}

}
