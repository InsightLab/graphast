package org.graphast.query.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.query.model.AbstractBoundsSearchPoI;
import org.graphast.query.model.Bound;
import org.graphast.query.model.LowerBoundEntry;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public abstract class AbstractKNNService implements KNNService{
	protected Graph network;
	protected AbstractBoundsSearchPoI minBounds;
	protected AbstractBoundsSearchPoI maxBounds;
	
	protected static int wasRemoved = -1;
	
	public AbstractKNNService(Graph network, AbstractBoundsSearchPoI minBounds, AbstractBoundsSearchPoI maxBounds){
		this.network = network;
		this.minBounds = minBounds;
		this.maxBounds = maxBounds;
	}
	
	protected void init(long vid, int t, int k, int kth, PriorityQueue<LowerBoundEntry> queue, PriorityQueue<UpperEntry> upperCandidates, 
			HashMap<Long, Integer> isIn, HashMap<Long, Long> parents){
		Bound bMin = minBounds.getBounds().get(vid).iterator().next();
		Bound bMax = maxBounds.getBounds().get(vid).iterator().next();
		long unn = bMax.getId();
		int utdd = t + bMax.getCost();
		queue.offer(new LowerBoundEntry(	vid, 
									0, 
									t, 
									-1,
									t + bMin.getCost()));
		
		includeCandidate(k, unn, utdd, kth, upperCandidates, isIn);	
		parents.put(vid, null);
	}
	
	protected ArrayList<Long> reconstructPath(long id, HashMap<Long, Long> parents){
		long parent = parents.get(id);
		ArrayList<Long> path = new ArrayList<Long>();
		path.add(id);
		while(parent != -1){
			path.add(parent);
			parent = parents.get(parent);
		}
		Collections.reverse(path);
		return path;
	}
	
	protected void includeCandidate(int k, long unn, int utdd, int kth, 
			PriorityQueue<UpperEntry> upperCandidates, HashMap<Long, Integer> isIn){
		if(!isIn.containsKey(unn)){
			if(upperCandidates.size() < k){
				upperCandidates.offer(new UpperEntry(unn, utdd));
				isIn.put(unn, utdd);
			}else{
				UpperEntry e = upperCandidates.peek();
				if(e.utdd > utdd){
					isIn.remove(e.unn);
					upperCandidates.poll();
					upperCandidates.offer(new UpperEntry(unn, utdd));
					isIn.put(unn, utdd);
				}
			}
			
		}else if(isIn.get(unn)>utdd){
			updateCandidates(unn, utdd, upperCandidates);
		}
		if(upperCandidates.size()==k){
			kth = upperCandidates.peek().utdd;
		}
	}
	
	protected void updateCandidates(long unn, int utdd, PriorityQueue<UpperEntry> upperCandidates) {
		UpperEntry toBeRemoved = null;
		for (UpperEntry u : upperCandidates) {
			if(u.unn == unn){
				toBeRemoved = u;
				break;
			}
		}
		upperCandidates.remove(toBeRemoved);
		upperCandidates.offer(new UpperEntry(unn, utdd));
	}
	
	protected void expandVertex(LowerBoundEntry removed, int kth, HashMap<Long, Integer> wasTraversed, int k,
			PriorityQueue<LowerBoundEntry> queue, PriorityQueue<UpperEntry> upperCandidates, HashMap<Long, Integer> isIn){
		
		Long2IntMap neig = network.accessNeighborhood(network.getNode(removed.getId()));
		
		for (long v : neig.keySet()) {
			int at = network.getArrival(removed.getArrivalTime(), neig.get(v));
			int tt = removed.getTravelTime() + neig.get(v);
			Bound bMin = minBounds.getBounds().get(v).iterator().next();
			LowerBoundEntry newEntry = new LowerBoundEntry(	v, 
													tt, 
													at, 
													removed.getId(),
													tt + bMin.getCost());
			if(kth >= newEntry.getLowerBound()){
				if(!wasTraversed.containsKey(v)){					
					queue.offer(newEntry);
					wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
				}else{
					int cost = wasTraversed.get(v);
					if(cost != wasRemoved){
						if(cost>newEntry.getTravelTime()){
							queue.remove(newEntry);
							queue.offer(newEntry);
							wasTraversed.remove(newEntry.getId());
							wasTraversed.put(newEntry.getId(), newEntry.getTravelTime());
						}
					}
				}
				Bound bMax = maxBounds.getBounds().get(v).iterator().next();
				includeCandidate(k, bMax.getId(), tt + bMax.getCost(), kth, upperCandidates, isIn);
			}
		}
	}
}
