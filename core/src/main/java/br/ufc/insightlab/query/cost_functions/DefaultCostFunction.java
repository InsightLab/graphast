package br.ufc.insightlab.query.cost_functions;

import br.ufc.insightlab.model.Edge;

public class DefaultCostFunction implements CostFunction {

	@Override
	public double getCost(Edge e) {
		return e.getWeight();
	}	
	
}
