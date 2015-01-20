package org.graphast.query.route.shortestpath;



public class LowerBoundEntry extends Entry{
	
	public int lb;
	
	public LowerBoundEntry(long id, int tt, int at, long parentId, int lb) {
		super(id,tt,at,parentId);
		this.lb = lb;
	}
	
	public boolean equals(LowerBoundEntry o){
		return super.getId() == o.getId();
	}
	
	public String toString(){
		return "( ID:"+super.getId()+" TT:"+super.getTravelTime()+" AT:"+super.getArrivalTime()+" LB:"+lb+" )";
	}

	@Override
	public int compareTo(Object o) {
		return new Integer(lb).compareTo(((LowerBoundEntry)o).lb);
	}

	public int getLb() {
		return lb;
	}

	public void setLb(int lb) {
		this.lb = lb;
	}
}