package org.graphast.query.knn;

import java.util.ArrayList;

public class NearestNeighborTC extends NearestNeighbor{
	private int wt;
	private int ts;
	
	public NearestNeighborTC(Long id, int distance, ArrayList<Long> path, int wt, int ts){
        super(id, distance, path);
        this.wt = wt;
        this.ts = ts;
    }
	
	public NearestNeighborTC(Long id, int distance, ArrayList<Long> path){
        super(id, distance, path);
    }
	
	public NearestNeighborTC(Long id, int travelTime, int wt, int ts){
        super(id, travelTime);
        this.wt = wt;
        this.ts = ts;
    }

	public int compareTo(NearestNeighborTC o) {
        return new Integer(ts).compareTo(o.ts);
    }
	
	public String toString(){
		String str;
		str = "(NN:"+super.getId()+" TT: "+super.getDistance()+ 
				" WT:" + wt + " TS:" + ts;
		if(!super.getPath().isEmpty())	str += " Path: " + super.getPath() + ")";
		else str += ")";
		return str;
	}

	public int getWt() {
		return wt;
	}

	public void setWt(int wt) {
		this.wt = wt;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}
}
