package org.graphast.query.route.osr;

import java.util.ArrayList;

import org.graphast.query.model.LowerBoundEntry;

public class RouteQueueEntry extends LowerBoundEntry {

	private ArrayList<NearestNeighborTC> routes;

	public RouteQueueEntry(int id, int travelTime, int arrivalTime, int parent, int lowerBound, ArrayList<NearestNeighborTC> r) {
		super(id, travelTime, arrivalTime, parent, lowerBound);
		this.routes = r;
	}

	@Override
	public boolean equals(Object o){
		return super.getId() == ((RouteQueueEntry) o).getId() 
				&& routes.size() == ((RouteQueueEntry) o).getRoute().size();
	}

	public ArrayList<NearestNeighborTC> getRoute() {
		return routes;
	}

	public void setRoute(ArrayList<NearestNeighborTC> r) {
		this.routes = r;
	}

	public String toString(){
		return "( ID:"+super.getId()+" TT:"+super.getTravelTime()+" AT:"+super.getArrivalTime()+" LB:"+lowerBound+ " Reached:" + routes + " )";
	}
	
}
