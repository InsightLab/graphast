package org.graphast.query.route.shortestpath.bidirectionalastar;

import org.graphast.query.route.shortestpath.model.DistanceEntry;

public class LowerBoundDistanceEntry extends DistanceEntry implements Comparable<Object> {

	private double lowerBound;

	public LowerBoundDistanceEntry(long id, int distance, double lowerBound, long parent) {
		super(id, distance, parent);
		this.lowerBound = lowerBound;
	}

	public double getLowerBound() {
		return this.lowerBound;
	}

	@Override
	public int compareTo(Object o) {
		return new Double(lowerBound).compareTo(((LowerBoundDistanceEntry) o).lowerBound);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(lowerBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(lowerBound) != Double.doubleToLongBits(other.lowerBound))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LowerBoundDistanceEntry [ID: " + getId() + ", Distance: " + getDistance() + ", LowerBound: "
				+ lowerBound + ", ParentID: " + getParent() + "]";
	}

}
