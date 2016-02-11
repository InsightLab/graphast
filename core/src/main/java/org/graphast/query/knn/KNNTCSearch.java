package org.graphast.query.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.util.DateUtils;

public class KNNTCSearch extends AbstractKNNService{
	
	public KNNTCSearch(Graph network, BoundsKNNTC minBounds, BoundsKNNTC maxBounds){
		super(network, minBounds, maxBounds);
	}
	
	public List<NearestNeighbor> search(Node v, Date time, int k){
		ArrayList<NearestNeighbor> nn = new ArrayList<NearestNeighbor>();
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		PriorityQueue<LowerBoundEntry> queue = new PriorityQueue<LowerBoundEntry>();
		PriorityQueue<UpperEntry> upperCandidates = new PriorityQueue<UpperEntry>();
		HashMap<Long, Integer> isIn = new HashMap<Long, Integer>();
		HashMap<Long, Long> parents = new HashMap<Long, Long>();
		int kth = Integer.MAX_VALUE;
		int t = DateUtils.dateToMinutes(time);
		int wt, ts, upper = Integer.MAX_VALUE;
		LowerBoundEntry removed = null;
		
		init(v.getId(), t, k, kth, queue, upperCandidates, isIn, parents);
		
		while(!queue.isEmpty()){
			removed = queue.poll();
			wasTraversed.put(removed.getId(), wasRemoved);	
			parents.put(removed.getId(), removed.getParent());
			
			if(removed.getLowerBound() > upper)	return nn.subList(0, k);
			
			Node poi = ((Graph) network).getPoi(removed.getId());
			if(poi != null){
				wt = ((Graph) network).poiGetCost(removed.getId(), removed.getArrivalTime());
				ts = wt + removed.getTravelTime();
				nn.add(new NearestNeighborTC(removed.getId(), removed.getTravelTime(), 
						reconstructPath(removed.getId(), parents), wt, ts));
				Collections.sort(nn);
				if(nn.size() >= k)	upper = ((NearestNeighborTC) nn.get(k-1)).getTs();
			}
			
			expandVertex(removed, kth, wasTraversed, k,queue, upperCandidates, isIn);		
		}
		return nn.subList(0, k);
	}
}
