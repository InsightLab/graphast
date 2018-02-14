package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.model.Edge;

public class DefaultCostFunction implements CostFunction {

	@Override
	public double getCost(Edge e) {
		return e.getCost();
	}	
	
}
