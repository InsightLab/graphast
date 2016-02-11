package org.graphast.model;

import org.graphast.util.StringUtils;

public class LinearFunction {

	private int startInterval;
	private int endInterval;
	private int startCost;
	private int endCost;
	
	public LinearFunction(){}
	
	public LinearFunction(int startInterval, int startCost, int endInterval, int endCost){
		this.startInterval = startInterval;
		this.endInterval = endInterval;
		this.startCost = startCost;
		this.endCost = endCost;
	}
//
//	public LinearFunction(short) {
//		this.startInterval = costs[0];
//		this.endInterval = ;
//		this.startCost = startCost;
//		this.endCost = endCost;
//	}

	public int calculateCost(int time) {
		return getSlope() * (time - startInterval) + startCost;
	}
		
	public int getSlope(){
		return (endCost - startCost)/(endInterval - startInterval);
	}
	
	public String toString() {
		return StringUtils.append(",", startInterval, startCost, endInterval, endCost);
    }
	
	public void read(String serialized) {  
		int[] objs = StringUtils.splitInt(",", serialized);
        this.startInterval = objs[0];
        this.startCost = objs[1];
    	this.endInterval = objs[2];
    	this.endCost = objs[3];
    }
	
	public int getStartInterval() {
		return startInterval;
	}

	public void setStartInterval(int startInterval) {
		this.startInterval = startInterval;
	}

	public int getEndInterval() {
		return endInterval;
	}

	public void setEndInterval(int endInterval) {
		this.endInterval = endInterval;
	}

	public int getStartCost() {
		return startCost;
	}

	public void setStartCost(int startCost) {
		this.startCost = startCost;
	}

	public int getEndCost() {
		return endCost;
	}

	public void setEndCost(int endCost) {
		this.endCost = endCost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endCost;
		result = prime * result + endInterval;
		result = prime * result + startCost;
		result = prime * result + startInterval;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinearFunction other = (LinearFunction) obj;
		if (endCost != other.endCost)
			return false;
		if (endInterval != other.endInterval)
			return false;
		if (startCost != other.startCost)
			return false;
		if (startInterval != other.startInterval)
			return false;
		return true;
	}
	
}
