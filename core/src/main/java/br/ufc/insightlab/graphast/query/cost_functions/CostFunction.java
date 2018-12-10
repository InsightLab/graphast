package br.ufc.insightlab.graphast.query.cost_functions;

import br.ufc.insightlab.graphast.model.Edge;

public interface CostFunction {
	
	double getCost(Edge e) throws RuntimeException;  //TODO Create specific exception

}
