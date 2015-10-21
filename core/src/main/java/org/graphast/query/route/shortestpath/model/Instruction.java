package org.graphast.query.route.shortestpath.model;


public class Instruction {
	
	protected int direction;
    protected String label;
    private double cost;
    private int distance;
	
    public Instruction(int direction, String label, double cost, int distance) {
    	
    	this.direction = direction;
    	this.label = label;
    	this.cost = cost;
    	this.distance = distance;
    
    }
    
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
    public String toString()
    {
		
        StringBuilder sb = new StringBuilder();
        
        sb.append("( ");
        sb.append(direction).append(", ");
        sb.append(label).append(", ");
        sb.append(cost);
        sb.append(" )");
    
        return sb.toString();
    
    }
    
}
