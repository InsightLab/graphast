package org.graphast.query.route.shortestpath.bidirectionalastar;

import org.graphast.query.route.shortestpath.model.DistanceEntry;

public class LowerBoundDistanceEntry extends DistanceEntry implements Comparable<Object> {

	private int lowerBound;
	
	public LowerBoundDistanceEntry(long id, int distance, int lowerBound, long parent) {
		super(id, distance, parent);
		this.lowerBound = lowerBound;
	}
	
	public int getLowerBound() {
		return this.lowerBound;
	}
	
	@Override
	public int compareTo(Object o) {
		return new Integer(lowerBound).compareTo( ((LowerBoundDistanceEntry)o).lowerBound );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + lowerBound;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LowerBoundDistanceEntry other = (LowerBoundDistanceEntry) obj;
		if (lowerBound != other.lowerBound)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "LowerBoundDistanceEntry [ID: " + getId() + ", Distance: " + getDistance() + ", LowerBound: " + lowerBound + ", ParentID: " + getParent() + "]";
	}
	
}
