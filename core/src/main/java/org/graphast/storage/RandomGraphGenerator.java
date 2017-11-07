package org.graphast.storage;

import java.util.Random;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.structure.MMapGraphStructure;

public class RandomGraphGenerator {
	
	public static Graph generateRandomMMapGraph(String graphName, int size, float dens) {
		
		Graph g = null;
		String dir = "graphs/MMap/" + graphName;
		
		StorageUtils.deleteMMapGraph(dir);
		g = new Graph(new MMapGraphStructure(dir));
		
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