package org.insightlab.graphast.model.components.temporal_components;

import java.util.Arrays;
import java.util.List;

import org.insightlab.graphast.model.components.EdgeComponent;

public class TemporalEdgeComponent extends EdgeComponent {
	
	private List<Double> costList;
	
	public TemporalEdgeComponent(Double ...costs) {
		costList = Arrays.asList(costs);
	}
	
	public double getSpecificCost(int index) {
		return costList.get(index);
	}
	
	public double getMeanCost() {
		return costList.stream().reduce(0., (x1, x2) -> x1 + x2) / costList.size();
	}

}
