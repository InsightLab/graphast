package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

public class TemporalMeanCostFunction implements CostFunction {

	@Override
	public double getCost(Edge e) throws Exception {
		CostListEdgeComponent component = e.getComponent(CostListEdgeComponent.class);
		if (component == null)
			throw new Exception("Temporal component not found");
		return component.getMeanCost();
	}

}
