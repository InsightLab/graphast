package org.graphast.query.route.osr;

import java.util.ArrayList;

import org.graphast.query.model.LowerBoundEntry;


public class RouteQueueEntry extends LowerBoundEntry {

	private ArrayList<NearestNeighborTC> r;

	public RouteQueueEntry(int id, int travelTime, int arrivalTime, int parent, int lowerBound, ArrayList<NearestNeighborTC> r) {
		super(id, travelTime, arrivalTime, parent, lowerBound);
		this.r = r;
	}

	@Override
	public boolean equals(Object o){
		return super.getId() == ((RouteQueueEntry) o).getId() 
				&& r.size() == ((RouteQueueEntry) o).getR().size();
	}

	public ArrayList<NearestNeighborTC> getR() {
		return r;
	}

	public void setR(ArrayList<NearestNeighborTC> r) {
		this.r = r;
	}

	public String toString(){
		return "( ID:"+super.getId()+" TT:"+super.getTravelTime()+" AT:"+super.getArrivalTime()+" LB:"+lowerBound+ " Reached:" + r + " )";
	}

}
