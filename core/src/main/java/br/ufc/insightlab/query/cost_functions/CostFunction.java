package br.ufc.insightlab.query.cost_functions;

import br.ufc.insightlab.model.Edge;

public interface CostFunction {
	
	double getCost(Edge e) throws RuntimeException;  //TODO Create specific exception

}
