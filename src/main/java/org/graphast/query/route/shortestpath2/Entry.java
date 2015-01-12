package org.graphast.query.route.shortestpath2;

public class Entry implements Comparable<Object>{

    private int id;
    private int travelTime;
    private int arrivalTime;
    private int parent;
	
	public Entry(int id, int travelTime, int arrivalTime, int parent) {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTt() {
		return travelTime;
	}

	public void setTt(int tt) {
		this.travelTime = tt;
	}

	public int getAt() {
		return arrivalTime;
	}

	public void setAt(int at) {
		this.arrivalTime = at;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}
	
}
