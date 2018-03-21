package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.exceptions.ComponentNotFoundException;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

import java.util.List;

public class TimeDependentStepCostFunction extends TimeDependentCostFunction {

	public TimeDependentStepCostFunction(int hour, int min) {
		this.setTime(hour, min);
	}
	
	public double getStepCost(List<Double> costList) {
		double relativePosition = this.getRelativePositionInCostList(costList.size());
		if (relativePosition == -1) return -1;

		int index = (int) Math.floor(relativePosition);

		return costList.get(index);
	}

	@Override
	public double getCost(Edge e) throws RuntimeException {
		CostListEdgeComponent component = e.getComponent(CostListEdgeComponent.class);
		if (component == null)
			throw new ComponentNotFoundException(CostListEdgeComponent.class);
		
		return getStepCost(component.getCostList());
	}

}
