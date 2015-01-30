package org.graphast.query.route.osr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

public class OSRSearch implements RouteService {
	private Graph graph;
	private BoundsRoute bounds;
	private Dijkstra dij;

	protected static int wasRemoved = -1;


	public OSRSearch(Graph graphAdapter, BoundsRoute bounds, Graph lbgRev){
		this.graphAdapter = graphAdapter;
		this.bounds = bounds;
		this.dij = new Dijkstra(lbgRev);
	}

	public ArrayList<Integer> reconstructPath(Vertex q, Vertex d, RouteQueueEntry route, 
			HashMap<Integer, HashMap<Integer, Integer>> parents){
		ArrayList<Integer> path = new ArrayList<Integer>();
		int qId = convertToInt(q.getId());
		int size = route.getR().size();
		for(int id = 0; id < size; id++){
			path.addAll(pathToPoi(id, qId, route.getR().get(id).getId(), parents));
			qId = route.getR().get(id).getId();
		}

		path.addAll(pathToDestination(d, size, route.getR().get(size - 1).getId(), parents));
		path.add(convertToInt(d.getId()));
		return path;
	}

	private ArrayList<Integer> pathToPoi(int pos, int id, int pid, HashMap<Integer, HashMap<Integer, Integer>> parents){
		ArrayList<Integer> path = new ArrayList<Integer>();
		int parent = parents.get(pos + 1).get(pid);
		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get(parent);
		}
		path.add(id);
		Collections.reverse(path);
		return path;
	}

	private ArrayList<Integer> pathToDestination(Vertex d, int pos, int id, 
			HashMap<Integer, HashMap<Integer, Integer>> parents){
		int did = convertToInt(d.getId());
		ArrayList<Integer> path = new ArrayList<Integer>();
		int parent = parents.get(pos).get(did);
		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get(parent);
		}
		path.add(id);
		Collections.reverse(path);
		return path;
	}

	public Sequence search(Vertex q, Vertex d, Date time, ArrayList<Integer> c){
		PriorityQueue<RouteQueueEntry> queue = new PriorityQueue<RouteQueueEntry>();
		HashMap<Integer, HashMap<Integer, Integer>> parents = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> wasTraversed = new HashMap<Integer, HashMap<Integer, Integer>>();

		HashMap<Integer, Integer> destination = new HashMap<Integer, Integer>();
		destination = dij.shortestPath(convertToInt(d.getId()));

		Sequence seq = new Sequence();
		int t = DateUtils.dateToMinutes(time);
		int wt, ts, upper = Integer.MAX_VALUE;
		int nextCat, nextId;
		init(q, d, c, t, queue, destination);;
		RouteQueueEntry removed = null;
		ArrayList<NearestNeighborTC> reachedNN;
		while(!queue.isEmpty()){
			removed = queue.poll();
			addWasTraversed(removed.getR().size(), removed.getId(), wasRemoved, wasTraversed);
			addParent(removed.getR().size(), removed.getId(), removed.getParent(), parents);

			if(removed.getId() == convertToInt(d.getId())){
				if(removed.getR().size() >= c.size()){
					return new Sequence(removed.getId(), removed.getTt(), 
							reconstructPath(q, d, removed, parents), removed.getR());
				}
			}

			if(removed.getLb() > upper)	return seq;

			HashMap<Vertex, Integer> neig = graphAdapter.accessNeighborhood(graphAdapter.getVertex(removed.getId()), removed.getAt(), NEIGHBOR);

			for (Vertex v : neig.keySet()) {
				int vid = convertToInt(v.getId());
				nextId = removed.getR().size();
				int tt = removed.getTt() + neig.get(v);
				wt = 0;
				reachedNN = new ArrayList<NearestNeighborTC>(removed.getR());

				if(nextId < c.size()){
					nextCat = c.get(nextId);
					Vertex poi = ((RoadGraphAdapter) graphAdapter).getPoi(vid);
					if(poi != null){
						if((Integer) poi.getProperty(CATEGORY) == nextCat){
							wt = ((RoadGraphAdapter) graphAdapter).poiGetCost(vid, removed.getAt());
							ts = wt + tt;
							NearestNeighborTC nn = new NearestNeighborTC(vid, tt, wt, ts);
							reachedNN.add(nn);
							nextId++;
						}
					}
				}
				int at = graphAdapter.getArrival(removed.getAt() + wt, neig.get(v));
				int lb = lowerBound(vid, nextId, c, destination);
				RouteQueueEntry newEntry = new RouteQueueEntry(	vid, 
						tt, 
						at, 
						removed.getId(),
						tt + lb,
						reachedNN);

				int pos = newEntry.getR().size();
				if(!wasRemoved(vid, pos, c, wasTraversed)){
					if(!isInQ(vid, pos, tt, wasTraversed, c)){
						if(isInQ(vid, pos, wasTraversed)){
							int cost = wasTraversed.get(pos).get(vid);
							if(cost>newEntry.getTt()){
								queue.remove(newEntry);
								queue.offer(newEntry);
								wasTraversed.get(pos).remove(vid);
							}
						}else{
							queue.offer(newEntry);
							addWasTraversed(pos, vid, tt, wasTraversed);
						}
					}
				}
			}	
			//System.out.println("FILA: " + queue);
		}
		return seq;
	}

	private boolean wasRemoved(int id, int pos, ArrayList<Integer> c, 
			HashMap<Integer, HashMap<Integer, Integer>> wasTraversed){
		for(int i = pos; i <= c.size(); i++){
			if(wasTraversed.containsKey(i)){
				if(wasTraversed.get(i).containsKey(id)){
					if(wasTraversed.get(i).get(id).equals(wasRemoved))	return true;
				}
			}
		}
		return false;
	}

	private boolean isInQ(int id, int pos, HashMap<Integer, HashMap<Integer, Integer>> wasTraversed){
		if(wasTraversed.containsKey(pos)){
			if(wasTraversed.get(pos).containsKey(id))	return true;
		}
		return false;
	}

	private boolean isInQ(int id, int pos, int newCost, HashMap<Integer, HashMap<Integer, Integer>> wasTraversed,
			ArrayList<Integer> c){
		for(int i = pos; i <= c.size(); i++){
			if(wasTraversed.containsKey(i)){
				if(wasTraversed.get(i).containsKey(id)){
					if(!wasTraversed.get(i).get(id).equals(wasRemoved)){
						int cost = wasTraversed.get(i).get(id);
						if(cost <= newCost)	return true;
					}
				}
			}
		}
		return false;
	}

	private int lowerBound(int id, int pos, ArrayList<Integer> c, HashMap<Integer, Integer> destination){
		Integer max = destination.get(id);
		if(pos < c.size()){
			int dist;
			for(int i = pos; i < c.size(); i++){
				dist = bounds.getBound(id, c.get(i)).getDistance();
				if(dist > max)	max = dist;
			}
		}
		return max;
	}

	private void addWasTraversed(int pos, int id, int cost, HashMap<Integer, HashMap<Integer, Integer>> wasTraversed){
		if(!wasTraversed.containsKey(pos)){
			wasTraversed.put(pos, new HashMap<Integer, Integer>());
		}
		wasTraversed.get(pos).put(id, cost);
	}

	private void addParent(int pos, int id, int parent, HashMap<Integer, HashMap<Integer, Integer>> parents){
		if(!parents.containsKey(pos)){
			parents.put(pos, new HashMap<Integer, Integer>());
		}
		parents.get(pos).put(id, parent);
	}

	private void init(Vertex q, Vertex d, ArrayList<Integer> c, int t, PriorityQueue<RouteQueueEntry> queue, 
			HashMap<Integer, Integer> destination){
		int pos = 0;
		ArrayList<NearestNeighborTC> reached = new ArrayList<NearestNeighborTC>();
		int tt, wt, ts;
		tt = 0;
		int qid = convertToInt(q.getId());

		Vertex poi = ((RoadGraphAdapter) graphAdapter).getPoi(qid);
		if(poi != null){
			if(poi.getProperty(CATEGORY) == c.get(0)){
				pos++;
				wt = ((RoadGraphAdapter) graphAdapter).poiGetCost(qid, t);
				ts = tt + wt;
				NearestNeighborTC nn = new NearestNeighborTC(qid, tt, wt, ts);
				reached.add(nn);
			}
		}

		int lb = lowerBound(qid, pos, c, destination);

		queue.offer(new RouteQueueEntry(	qid, 
				tt, 
				t, 
				-1,
				t + lb,
				reached));
	}

	public GraphAdapter getGraphAdapter() {
		return graphAdapter;
	}

	public void setGraphAdapter(GraphAdapter graphAdapter) {
		this.graphAdapter = graphAdapter;
	}
}
