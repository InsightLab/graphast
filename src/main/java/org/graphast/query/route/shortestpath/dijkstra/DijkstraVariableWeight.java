package org.graphast.query.route.shortestpath.dijkstra;

import static org.graphast.util.NumberUtils.convertToInt;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.Bound;
import org.graphast.query.model.QueueEntry;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

public class DijkstraVariableWeight extends Dijkstra {

	public DijkstraVariableWeight(Graph graph){
		super(graph);
	}
	
	public DijkstraVariableWeight(GraphBounds graphBounds){
		super(graphBounds);
	}

	public void expandVertex(QueueEntry e, LongSet settledNodes, Long2IntMap shortestDistances,
			PriorityQueue<QueueEntry> unsettledNodes){
		
		expandVertex(e, settledNodes, shortestDistances, unsettledNodes, (short) 0);
	
	}
	
	public void expandVertex(QueueEntry e, LongSet settledNodes, Long2IntMap shortestDistances,
			PriorityQueue<QueueEntry> unsettledNodes, short graphType){
		
		Long2IntMap adjacents = graphBounds.accessNeighborhood(graphBounds.getNode(e.getId()), graphType, 0);
		
		if(adjacents != null) {
			
			for (long v : adjacents.keySet()) {
				
				long nodeId = v;
				
				if (settledNodes.contains(nodeId))    continue;

				int shortDist = getShortestDistance(e.getId(), shortestDistances) + adjacents.get(v);

				if (shortDist < getShortestDistance(nodeId, shortestDistances))
				{
					// assign new shortest distance and mark unsettled

					QueueEntry eNew = new QueueEntry(nodeId, shortDist);
					unsettledNodes.remove(eNew);
					unsettledNodes.add(eNew);
					shortestDistances.put(nodeId, shortDist);
				}
			} 
		}
	}

	public Int2DoubleMap shortestPath(long v){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
		LongSet settledNodes = new LongOpenHashSet();
		Long2IntMap shortestDistances = new Long2IntOpenHashMap();
		Int2DoubleMap distance = new Int2DoubleOpenHashMap();

		shortestDistances.put(v, 0);
		distance.put(convertToInt(v), 0);
		QueueEntry e = new QueueEntry(v, 0);
		unsettledNodes.add(e);

		while ((e = unsettledNodes.poll()) != null){
			if(!settledNodes.contains(e.getId())){
				settledNodes.add(e.getId());
				distance.put(convertToInt(e.getId()), e.getTravelTime());

				expandVertex(e, settledNodes, shortestDistances, unsettledNodes, (short)0);
			}
		} 
		return distance;
	}

	public Bound shortestPathPoi(long v, int idCat){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
		LongSet settledNodes = new LongOpenHashSet();
		Long2IntMap shortestDistances = new Long2IntOpenHashMap();

		shortestDistances.put(v, 0);
		QueueEntry e = new QueueEntry(v, 0);
		unsettledNodes.add(e);

		while ((e = unsettledNodes.poll()) != null){
			if(!settledNodes.contains(e.getId())){
				settledNodes.add(e.getId());

				Node poi = ((Graph) graph).getPoi(e.getId());
				if(poi != null){

					if(idCat == -1)	return new Bound(e.getId(), e.getTravelTime());
					else{
						if(poi.getCategory() == idCat){
							return new Bound(e.getId(), e.getTravelTime());
						}
					}
				}

				expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
			}
		}        
		return new Bound();
	}

	public ObjectCollection<Bound> shortestPathCategories(long nodeId, Set<Integer> categoriesIds, short graphType){

		//TODO Change this PriorityQueue to some FastUtil structure
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();

		LongSet settledNodes = new LongOpenHashSet();
		Long2IntMap shortestDistances = new Long2IntOpenHashMap();

		Int2ObjectMap<Bound> bounds = new Int2ObjectOpenHashMap<Bound>();
		int upper = Integer.MIN_VALUE;
		int waitingTime, timeToService;

		shortestDistances.put(nodeId, 0);

		/*
		 * This queryEntry represents the travel time from this nodeId to 
		 * another vertex (this vertex can be, for example, a PoI. In this 
		 * way, the queryEntry is going to represent the travel time from 
		 * the nodeId to a PoI.).
		 */
		QueueEntry queryEntry = new QueueEntry(nodeId, 0);
		unsettledNodes.add(queryEntry);

		while ((queryEntry = unsettledNodes.poll()) != null){

			if(bounds.keySet().containsAll(categoriesIds) && queryEntry.getTravelTime() > upper){
				return bounds.values();
			}

			if(!settledNodes.contains(queryEntry.getId())) {

				settledNodes.add(queryEntry.getId());

				Node poi = graphBounds.getPoi(queryEntry.getId());

				if(poi != null) {

					int cat = poi.getCategory();

					waitingTime = graphBounds.poiGetCost(queryEntry.getId(), graphType);
					timeToService = queryEntry.getTravelTime() + waitingTime;

					if(bounds.keySet().contains(cat)) {
						
						int cost = bounds.get(cat).getDistance();

						if(timeToService < cost)	bounds.put(cat, new Bound(queryEntry.getId(), timeToService));
//                		if(timeToService < cost)	bounds.put(e.getId(), new Bound(e.getId(), timeToService));
						upper = updateUpper(bounds);
						
					} else {
						
						bounds.put(cat, new Bound(queryEntry.getId(), timeToService));
						if(timeToService > upper)	upper = timeToService;
						
					}
				}

				expandVertex(queryEntry, settledNodes, shortestDistances, unsettledNodes, graphType);
			
			}
		}        

		return bounds.values();

	}

	public int updateUpper(Int2ObjectMap<Bound> bounds){
		int upper = Integer.MIN_VALUE;
		for(Bound b: bounds.values()){
			if(b.getDistance() > upper)	upper = b.getDistance();
		}
		return upper;
	}

	public Bound shortestTS(long v){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
		LongSet settledNodes = new LongOpenHashSet();
		Long2IntMap shortestDistances = new Long2IntOpenHashMap();
		int wt, ts;
		Bound best = new Bound(-1, Integer.MAX_VALUE);

		shortestDistances.put(v, 0);
		QueueEntry e = new QueueEntry(v, 0);
		unsettledNodes.add(e);

		while ((e = unsettledNodes.poll()) != null){
			if(e.getTravelTime() > best.getDistance()){
				return best;
			}

			if(!settledNodes.contains(e.getId())){
				settledNodes.add(e.getId());
				Node poi = graph.getPoi(e.getId());
				if(poi != null){
					if(e.getTravelTime() < best.getDistance()){
						wt = graph.poiGetCost(e.getId());
						ts = e.getTravelTime() + wt;
						best = new Bound(e.getId(), ts);
					}
				}

				expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
			}
		}        
		return best;
	}

	public HashMap<Long, Integer> shortestPath(long v, Set<Long> destination){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
		LongSet settledNodes = new LongOpenHashSet();
		Long2IntMap shortestDistances = new Long2IntOpenHashMap();
		HashMap<Long, Integer> distance = new HashMap<Long, Integer>();

		shortestDistances.put(v, 0);
		if(destination.contains(v))	distance.put(v, 0);
		QueueEntry e = new QueueEntry(v, 0);
		unsettledNodes.add(e);

		while ((e = unsettledNodes.poll()) != null){
			if(!settledNodes.contains(e.getId())){
				settledNodes.add(e.getId());
				if(destination.contains(e.getId())){
					distance.put(e.getId(), e.getTravelTime());  				
				}
				// destination reached, stop
				if(settledNodes.containsAll(destination)) {
					break;
				}

				expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
			}
		}

		return distance;
	}

	public int getShortestDistance(long id, Long2IntMap minCost)
	{
		if(minCost.containsKey(id))
			return minCost.get(id);
		else
			return Integer.MAX_VALUE;
	}

	@Override
	public Path shortestPath(Node source, Node target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path shortestPath(long source, long target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path shortestPath(long source, long target, Date time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void expandVertex(Node target, DistanceEntry removed,
			HashMap<Long, Integer> wasTraversed,
			PriorityQueue<DistanceEntry> queue,
			HashMap<Long, RouteEntry> parents) {
		// TODO Auto-generated method stub
		
	}
}		
