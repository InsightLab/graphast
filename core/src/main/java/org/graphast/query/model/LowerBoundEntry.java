package org.graphast.query.model;

import org.graphast.query.route.shortestpath.model.TimeEntry;

public class LowerBoundEntry extends TimeEntry{
	
	public int lowerBound;
	
	public LowerBoundEntry(long id, int travelTime, int arrivalTime, long parentId, int lowerBound) {
		
		super(id, travelTime, arrivalTime, parentId);
		this.lowerBound = lowerBound;
	
	}
	
	public boolean equals(LowerBoundEntry o) {
		
		return super.getId() == o.getId();
	
	}
	
	public String toString() {
		
		return "(ID: " + super.getId() + " Travel Time: " + super.getTravelTime() 
				+ " Arrival Time: " + super.getArrivalTime() + " Lower Bound: " + lowerBound + ")";
	
	}

	@Override
	public int compareTo(Object o) {
		
		
//		return new Integer(((LowerBoundEntry)o).lowerBound).compareTo(lowerBound);
		return new Integer(lowerBound).compareTo(((LowerBoundEntry)o).lowerBound);
	
	}

	public int getLowerBound() {
		
		return lowerBound;
	
	}

	public void setLowerBound(int lowerBound) {
		
		this.lowerBound = lowerBound;
	
	}

}