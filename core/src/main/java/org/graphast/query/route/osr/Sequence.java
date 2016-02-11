package org.graphast.query.route.osr;

import java.util.ArrayList;

public class Sequence extends NearestNeighborTC {

	private ArrayList<NearestNeighborTC> pois;

	public Sequence(){
		super(-1, Integer.MAX_VALUE, null, Integer.MAX_VALUE, Integer.MAX_VALUE);
		this.pois = null;
	}

	public Sequence(long id, int travelTime, ArrayList<Long> path, ArrayList<NearestNeighborTC> pois){
		super(id, travelTime, path);
		int waitingTime = 0;
		for(NearestNeighborTC p: pois){
			waitingTime += p.getWaitingTime();
		}
		super.setWaitingTime(waitingTime);
		super.setTimeToService(travelTime + waitingTime);
		this.pois = pois;
	}

	public ArrayList<NearestNeighborTC> getPois() {
		return pois;
	}

	public void setPois(ArrayList<NearestNeighborTC> pois) {
		this.pois = pois;
	}

	public String toString(){
		return "(Travel Time: " + super.getDistance() + " Waiting Time: " + super.getWaitingTime() + " Time to Service: " + super.getTimeToService() + " Path: " + super.getPath() + " POIs:" + pois + ")";
	}
	
}
