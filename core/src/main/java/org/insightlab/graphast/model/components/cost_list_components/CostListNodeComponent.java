package org.insightlab.graphast.model.components.cost_list_components;

import java.util.Arrays;
import java.util.List;

import org.insightlab.graphast.model.components.NodeComponent;

public class CostListNodeComponent extends NodeComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4909900732877399952L;
	
	private List<Double> costList;
	
	public CostListNodeComponent(Double ...costs) {
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
