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
		return "DistanceEntry [id=" + getId() + ", distance=" + distance
				+ ", parent=" + getParent() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + distance;
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
		DistanceEntry other = (DistanceEntry) obj;
		if (!getId().equals(other.getId()))
			return false;
//		if (parent != other.parent)
//			return false;
		return true;
	}
	
	
}
