package br.ufc.insightlab.graphast.query.cost_functions;

import br.ufc.insightlab.graphast.model.Edge;

public abstract class TimeDependentCostFunction implements CostFunction {
	
	private int hour, min;
	
	public void setTime(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	
	protected double getRelativePositionInCostList(int listSize) {
		if (hour >= 0 && hour < 24 && min >= 0 && min < 60) {
			int totalMins = hour*60 + min;
			return totalMins * listSize / (24*60.);
		}
		return -1;
	}
	
	public abstract double getCost(Edge e) throws RuntimeException;

}
