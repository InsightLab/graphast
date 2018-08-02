package org.insightlab.graphast.generators;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

import java.util.Random;

public class CostsGenerator {
	
	private static CostsGenerator instance = null;
	private Random r;
	
	public static CostsGenerator getInstance() {
		if (instance == null)
			instance = new CostsGenerator();
		return instance;
	}
	
	private CostsGenerator() {
		r = new Random();
	}
	
	public Double[] generateCosts(int quantity) {
		Double[] costs = new Double[quantity];
		for (int i = 0; i < quantity; i++)
			costs[i] = r.nextDouble()*9 + 1;
		return costs;
	}
	
	public void generateTemporalCosts(Graph g) {
		for (Edge e : g.getEdges())
			e.addComponent(new CostListEdgeComponent(generateCosts(r.nextInt(23) + 1)));
	}

}
