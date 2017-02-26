package org.graphast.query.knnch.lowerbounds;

public class LowerBoundEntry implements Comparable<LowerBoundEntry> {
	
	long poiId;
	Double distance;
	
	public LowerBoundEntry(long poiId, double distance) {
		this.poiId = poiId;
		this.distance = distance;
	}
	
	public int compareTo(LowerBoundEntry lbe) {
		return this.distance.compareTo(lbe.distance);
	}
	
	public String toString() {
		return "( " + this.poiId + " " + this.distance + " )";
	}
	
}
