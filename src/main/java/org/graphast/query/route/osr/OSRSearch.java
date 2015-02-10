package org.graphast.query.route.osr;

import static org.graphast.util.NumberUtils.convertToInt;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DateUtils;

public class OSRSearch {

	private Graph graph;
	private BoundsRoute bounds;
	private Dijkstra dijkstra;

	protected static int wasRemoved = -1;


	public OSRSearch(Graph graph, BoundsRoute bounds, Graph reverseGraph){
		this.graph = graph;
		this.bounds = bounds;
		//Double check this instantiation
		this.dijkstra = new DijkstraConstantWeight(reverseGraph);
	}

	public ArrayList<Long> reconstructPath(Node origin, Node destination, RouteQueueEntry route, 
			HashMap<Integer, HashMap<Integer, Integer>> parents){
		ArrayList<Long> path = new ArrayList<Long>();
		//ArrayList<Integer> path = new ArrayList<Integer>();
		int qId = convertToInt(origin.getId());
		int size = route.getR().size();
		for(int id = 0; id < size; id++){
			path.addAll(pathToPoi(id, qId, convertToInt(route.getR().get(id).getId()), parents));
			qId = convertToInt(route.getR().get(id).getId());
		}

		path.addAll(pathToDestination(destination, size, convertToInt(route.getR().get(size - 1).getId()), parents));
		path.add(destination.getId());
		return path;
	}

	private ArrayList<Long> pathToPoi(int pos, int id, int poiId, HashMap<Integer, HashMap<Integer, Integer>> parents){
		
		ArrayList<Long> path = new ArrayList<Long>();
		long parent = parents.get(pos + 1).get(poiId);
		
		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get(parent);
		}
		path.add((long)id);
		Collections.reverse(path);
		return path;
	}

	private ArrayList<Long> pathToDestination(Node d, int pos, int id, 
			HashMap<Integer, HashMap<Integer, Integer>> parents){
		int did = convertToInt(d.getId());
		ArrayList<Long> path = new ArrayList<Long>();
		long parent = parents.get(pos).get(did);
		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get(parent);
		}
		path.add((long)id);
		Collections.reverse(path);
		return path;
	}

	public Sequence search(Node origin, Node destination, Date time, ArrayList<Integer> categories){

		PriorityQueue<RouteQueueEntry> queue = new PriorityQueue<RouteQueueEntry>();
		HashMap<Integer, HashMap<Integer, Integer>> parents = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> wasTraversed = new HashMap<Integer, HashMap<Integer, Integer>>();

		// Is this 'destinationPaths' keeping all shortest paths 
		// from the destination node of OSR to all other nodes?
		Int2ObjectMap<Path> destinationPaths = new Int2ObjectOpenHashMap<Path>();
		destinationPaths = dijkstra.shortestPath(destination);

		Sequence seq = new Sequence();
		int t = DateUtils.dateToMinutes(time);
		int wt, ts, upper = Integer.MAX_VALUE;
		int nextCat, nextId;
		init(origin, destination, categories, t, queue, destinationPaths);
		RouteQueueEntry removed = null;
		ArrayList<NearestNeighborTC> reachedNN;

		while(!queue.isEmpty()){
			removed = queue.poll();
			addWasTraversed(removed.getR().size(), convertToInt(removed.getId()), wasRemoved, wasTraversed);
			addParent(removed.getR().size(), convertToInt(removed.getId()), convertToInt(removed.getParent()), parents);

			if(removed.getId() == convertToInt(destination.getId())){
				if(removed.getR().size() >= categories.size()){
					return new Sequence(removed.getId(), 
							removed.getTravelTime(), 
							reconstructPath(origin, destination, removed, parents), removed.getR());
				}
			}

			if(removed.getLowerBound() > upper)	return seq;

			HashMap<Node, Integer> neig = graph.accessNeighborhood(graph.getNode(removed.getId()), removed.getArrivalTime());

			for (Node v : neig.keySet()) {
				int vid = convertToInt(v.getId());
				nextId = removed.getR().size();
				int tt = removed.getTravelTime() + neig.get(v);
				wt = 0;
				reachedNN = new ArrayList<NearestNeighborTC>(removed.getR());

				if(nextId < categories.size()){
					nextCat = categories.get(nextId);
					Node poi = ((Graph) graph).getPoi(vid);
					if(poi != null){
						if(poi.getCategory() == nextCat){
							wt = ((Graph) graph).poiGetCost(vid, removed.getArrivalTime());
							ts = wt + tt;
							NearestNeighborTC nn = new NearestNeighborTC(vid, tt, wt, ts);
							reachedNN.add(nn);
							nextId++;
						}
					}
				}
				int at = graph.getArrival(removed.getArrivalTime() + wt, neig.get(v));
				int lb = (int) lowerBound(vid, nextId, categories, destinationPaths);
				RouteQueueEntry newEntry = new RouteQueueEntry(	vid, tt, at, convertToInt(removed.getId()), tt + lb, reachedNN);

				int pos = newEntry.getR().size();
				if(!wasRemoved(vid, pos, categories, wasTraversed)){
					if(!isInQ(vid, pos, tt, wasTraversed, categories)){
						if(isInQ(vid, pos, wasTraversed)){
							int cost = wasTraversed.get(pos).get(vid);
							if(cost>newEntry.getTravelTime()){
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

	private double lowerBound(int id, int pos, ArrayList<Integer> categories, Int2ObjectMap<Path> destination){
		double max = destination.get(id).getPathCost();
		if(pos < categories.size()){
			int distance;
			for(int i = pos; i < categories.size(); i++){
				distance = bounds.getBound(id, categories.get(i)).getDistance();
				if(distance > max)	max = distance;
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

	private void init(Node origin, Node d, ArrayList<Integer> categories, int t, PriorityQueue<RouteQueueEntry> queue, 
			Int2ObjectMap<Path> destinationPaths){

		int pos = 0;
		ArrayList<NearestNeighborTC> reached = new ArrayList<NearestNeighborTC>();
		int travelTime, waitingTime, timeToService;
		travelTime = 0;
		int originId = convertToInt(origin.getId());

		Node poi = ((Graph) graph).getPoi(originId);

		if(poi != null) {

			if(poi.getCategory() == categories.get(0)){
				pos++;
				waitingTime = ((Graph) graph).poiGetCost(originId, t);
				timeToService = travelTime + waitingTime;
				NearestNeighborTC nn = new NearestNeighborTC(originId, travelTime, waitingTime, timeToService);
				reached.add(nn);
			}
		}

		int lb = convertToInt(lowerBound(originId, pos, categories, destinationPaths));

		queue.offer(new RouteQueueEntry(originId, travelTime, t, -1, t + lb, reached));
	}

	public Graph getGraphAdapter() {
		return graph;
	}

	public void setGraphAdapter(Graph graphAdapter) {
		this.graph = graphAdapter;
	}

}
