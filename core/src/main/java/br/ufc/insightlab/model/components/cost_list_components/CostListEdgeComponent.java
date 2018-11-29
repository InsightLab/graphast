package br.ufc.insightlab.model.components.cost_list_components;

import br.ufc.insightlab.model.components.EdgeComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CostListEdgeComponent extends EdgeComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2801818017386915449L;
	
	private List<Double> costList;
	
	public CostListEdgeComponent() {
		costList = new ArrayList<>();
	}
	
	public CostListEdgeComponent(Double ...costs) {
		costList = Arrays.asList(costs);
	}
	
	public List<Double> getCostList() {
		return costList;
	}
	
	public double getSpecificCost(int index) {
		return costList.get(index);
	}
	
	public double getMeanCost() {
		return costList.stream().reduce(0., (x1, x2) -> x1 + x2) / costList.size();
	}

}
