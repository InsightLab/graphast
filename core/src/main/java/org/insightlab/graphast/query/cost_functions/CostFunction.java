package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.model.Edge;

public interface CostFunction {
	
	double getCost(Edge e) throws Exception;  //TODO Create specific exception

}
