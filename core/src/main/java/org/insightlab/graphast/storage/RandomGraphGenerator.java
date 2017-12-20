package org.insightlab.graphast.storage;

import java.util.Random;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.structure.MMapGraphStructure;

public class RandomGraphGenerator {
	
	public RandomGraphGenerator() {}
	
	public Graph generateRandomMMapGraph(String graphName, int size, float dens) {
		
		Graph graph = null;
		String dir = "graphs/MMap/" + graphName;
		
		StorageUtils.deleteMMapGraph(dir);
		graph = new Graph(new MMapGraphStructure(dir));
		
		Random rand = new Random();
		
		for (int i = 0; i < size; i++)
			graph.addNode(new Node(i));
		
		for (int i = 0; i < size; i++) {
			for (int j = i+1; j < size; j++) {
				
				if (rand.nextFloat() < dens) {
					int randomCost = Math.abs(rand.nextInt(50)) + 1;
					Edge e = new Edge(i, j, randomCost, rand.nextBoolean());
					
					graph.addEdge(e);
				}
			
			}
		}

		return graph;
	}

}
