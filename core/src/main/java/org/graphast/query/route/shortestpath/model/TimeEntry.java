package org.graphast.query.route.shortestpath.model;

public class TimeEntry extends EntryImpl implements Comparable<Object>{
	
    private int travelTime;
    private int arrivalTime;
	
	public TimeEntry(long id, int travelTime, int arrivalTime, long parent) {
		super(id, parent);
    	this.travelTime = travelTime;
    	this.arrivalTime = arrivalTime;
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
	
	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		return new Integer(travelTime).compareTo( ((TimeEntry)o).travelTime );
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#toString()
	 */
	@Override
	public String toString() {
		return "Entry [id=" + getId() + ", travelTime=" + travelTime
				+ ", arrivalTime=" + arrivalTime + ", parent=" + getParent() + "]";
	}
}
