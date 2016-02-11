package org.graphast.query.route.shortestpath.model;

public class DistanceEntry extends EntryImpl implements Comparable<Object> {

    private int distance;
	
	public DistanceEntry(long id, int distance, long parent) {
		super(id, parent);
    	this.distance = distance;
	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		return new Integer(distance).compareTo( ((DistanceEntry)o).distance );
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#toString()
	 */
	@Override
	public String toString() {
		return "Entry [id=" + getId() + ", distance=" + distance
				+ ", parent=" + getParent() + "]";
	}
}
