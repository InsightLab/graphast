package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

public class TemporalSpecificCostFunction implements CostFunction {
	
	private int index;
	
	public TemporalSpecificCostFunction(int index) {
		this.index = index;
	}

	@Override
	public double getCost(Edge e) throws Exception {
		CostListEdgeComponent component = e.getComponent(CostListEdgeComponent.class);
		if (component == null)
			throw new Exception("Temporal component not found");
		return component.getSpecificCost(index);
	}

}
