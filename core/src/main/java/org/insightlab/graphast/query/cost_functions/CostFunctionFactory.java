package org.insightlab.graphast.query.cost_functions;

import java.util.Calendar;

public class CostFunctionFactory {
	
	public static CostFunction getDefaultCostFunction() {
		return new DefaultCostFunction();
	}
	
	public static CostFunction getTimeDependentCostFunction() {
		return getTimeDependentCostFunction(InterpolationMethod.LINEAR);
	}
	
	public static TimeDependentCostFunction getTimeDependentCostFunction(InterpolationMethod interpolationMethod) {
		Calendar now = Calendar.getInstance();
		switch(interpolationMethod) {
		case STEP:
			return new TimeDependentStepCostFunction(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
		case LINEAR:
		default:
			return new TimeDependentLinearCostFunction(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
		}
	}

}
