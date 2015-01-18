package org.graphast.query.route.shortestpath;

public class Entry implements Comparable<Object>{
    private long id;
    private int travelTime;
    private int arrivalTime;
    private long parent;
	
	public Entry(long id, int travelTime, int arrivalTime, long parent) {
    	this.id = id;
    	this.travelTime = travelTime;
    	this.arrivalTime = arrivalTime;
    	this.parent = parent;
	}

	public boolean equals(Entry o){
        return this.id == o.id;
    }
 
	public int compareTo(Object o) {
		return new Integer(travelTime).compareTo(((Entry)o).travelTime);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(int tt) {
		this.travelTime = tt;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int at) {
		this.arrivalTime = at;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "Entry [id=" + id + ", travelTime=" + travelTime
				+ ", arrivalTime=" + arrivalTime + ", parent=" + parent + "]";
	}
	
}
