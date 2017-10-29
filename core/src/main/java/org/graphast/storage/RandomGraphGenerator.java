package org.graphast.storage;

import java.util.Random;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.structure.MMapGraphStructure;

public class RandomGraphGenerator {
	
	public static Graph generateRandomMMapGraph(int size, float dens) {
		
		String graphName = "random_graph";
		
		StorageUtils.deleteMMapGraph(graphName);
		
		Graph g = new Graph(new MMapGraphStructure(graphName));
		Random r = new Random();
		
		for (int i = 0; i < size; i++)
			g.addNode(new Node(i));
		
		for (int i = 0; i < size; i++) {
			for (int j = i+1; j < size; j++) {
				if (r.nextFloat() < dens) {
					int randomCost = Math.abs(r.nextInt(50)) + 1;
					Edge e = new Edge(i, j, randomCost, r.nextBoolean());
					g.addEdge(e);
				}
			}
		}

		return g;
	}

}
