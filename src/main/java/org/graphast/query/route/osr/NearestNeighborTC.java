package org.graphast.query.route.osr;

import java.util.ArrayList;

import org.graphast.query.knn.NearestNeighbor;

public class NearestNeighborTC extends NearestNeighbor {

	private int waitingTime;
	private int timeToService;
	
	//TODO Change the type of path from ArrayList<Long> to LongList (from FastUtil)
	public NearestNeighborTC(long id, int distance, ArrayList<Long> path, int waitingTime, int timeToService){
        
		super(id, distance, path);
        this.waitingTime = waitingTime;
        this.timeToService = timeToService;
    
	}
	
	public NearestNeighborTC(long id, int distance, ArrayList<Long> path){
        
		super(id, distance, path);
    
	}
	
	public NearestNeighborTC(long id, int travelTime, int waitingTime, int timeToService){
        super(id, travelTime);
        this.waitingTime = waitingTime;
        this.timeToService = timeToService;
    }

	public int compareTo(NearestNeighborTC o) {
        return new Integer(timeToService).compareTo(o.timeToService);
    }
	
	public String toString(){
		String str;
		str = "(NN: " + super.getId() + " Travel Time: " + super.getDistance() + 
				" Waiting Time: " + waitingTime + " Time to Service: " + timeToService;
		if(!super.getPath().isEmpty())	str += " Path: " + super.getPath() + ")";
		else str += ")";
		return str;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public int getTimeToService() {
		return timeToService;
	}

	public void setTimeToService(int timeToService) {
		this.timeToService = timeToService;
	}
	
}
