package org.insightlab.graphast.query.cost_functions;

import java.util.List;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

public class TemporalLinearCostFunction implements CostFunction {
	
	private int hour, min;
	
	public TemporalLinearCostFunction(int hour, int min) {
		this.setTime(hour, min);
	}
	
	public void setTime(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	
	private double getLinearInterpolation(double n1, double n2, double x) {
		return n1 + x * (n2 - n1);
	}
	
	public double getLinearCostByTime(List<Double> costList) {
		if (hour >= 0 && hour < 24 && min >= 0 && min < 60) {
			int totalMins = hour*60 + min;
			double proportion = totalMins * costList.size() / (24*60.);
			int index = (int) Math.floor(proportion);
			
			double x = proportion - index;
			double n1 = costList.get(index);
			double n2 = (index + 1 < costList.size()) ? costList.get(index + 1) : costList.get(0);
			return getLinearInterpolation(n1,  n2, x);
		}
		return -1;
	}

	@Override
	public double getCost(Edge e) throws Exception {
		CostListEdgeComponent component = e.getComponent(CostListEdgeComponent.class);
		if (component == null)
			throw new Exception("Temporal component not found");
		return getLinearCostByTime(component.getCostList());
	}


}
