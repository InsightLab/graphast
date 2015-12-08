package org.graphast.query.knn;

import java.util.ArrayList;

public class NearestNeighbor implements Comparable<NearestNeighbor> {
    private long id;
    private int distance;
    private double travelTime;
    private ArrayList<Long> path;
    private int numberVisitedNodes;
     
    public NearestNeighbor() {}
     
    public NearestNeighbor(long id, int distance){
        this.id = id;
        this.distance = distance;
        this.path = new ArrayList<Long>();
    }
    
    public NearestNeighbor(long id, int distance, ArrayList<Long> path){
        this.id = id;
        this.distance = distance;
        this.path = path;
    }
    
    public NearestNeighbor(long id, int distance, ArrayList<Long> path, int numberVisitedNodes){
        this(id, distance, path);
        this.numberVisitedNodes = numberVisitedNodes;
    }
    
    public NearestNeighbor(long id, double travelTime, ArrayList<Long> path, int numberVisitedNodes){
    	this.id = id;
        this.path = path;
        this.travelTime = travelTime;
        this.numberVisitedNodes = numberVisitedNodes;
    }
     
     
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
     
    public ArrayList<Long> getPath() {
		return path;
	}

	public void setPath(ArrayList<Long> path) {
		this.path = path;
	}

	public String toString(){
        return "(NN:"+id+" TT: "+distance+ " Path: " + path +
        		" Number Visited Nodes: " + numberVisitedNodes + ")";
    }
 
    public int compareTo(NearestNeighbor o) {
    	int result;
    	if(distance == o.distance)	result = 0;
    	else if(distance < o.distance)	result = -1;
    	else	result = 1;
        return result;
    }
     
    public boolean equals(NearestNeighbor o){
        return((id == o.id) && (distance == o.distance));
    }

	public int getNumberVisitedNodes() {
		return numberVisitedNodes;
	}

	public void setNumberVisitedNodes(int numberVisitedNodes) {
		this.numberVisitedNodes = numberVisitedNodes;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}
}
