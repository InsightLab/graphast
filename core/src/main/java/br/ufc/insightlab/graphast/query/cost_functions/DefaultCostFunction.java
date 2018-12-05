package br.ufc.insightlab.graphast.query.cost_functions;

import br.ufc.insightlab.graphast.model.Edge;

public class DefaultCostFunction implements CostFunction {

	@Override
	public double getCost(Edge e) {
		return e.getWeight();
	}	
	
}
