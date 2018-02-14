package org.insightlab.graphast.query.cost_functions;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.components.temporal_components.TemporalEdgeComponent;

public class TemporalMeanCostFunction implements CostFunction {

	@Override
	public double getCost(Edge e) throws Exception {
		TemporalEdgeComponent component = e.getComponent(TemporalEdgeComponent.class);
		if (component == null)
			throw new Exception("Temporal component not found");
		return component.getMeanCost();
	}

}
