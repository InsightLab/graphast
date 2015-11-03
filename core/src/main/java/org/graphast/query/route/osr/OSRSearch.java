package org.graphast.query.route.osr;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.geometry.PoI;
import org.graphast.geometry.PoICategory;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DateUtils;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class OSRSearch {

	protected static AbstractShortestPathService serviceGraph;
	
	private GraphBounds graphBounds;
	private DijkstraLinearFunction dijkstra;
	private BoundsRoute bounds;
	private short graphType;
	
	protected static int wasRemoved = -1;


	public OSRSearch(GraphBounds graphBounds, short graphType){
		this.graphBounds = graphBounds;
		this.graphType = graphType;
		//Double check this instantiation
		this.dijkstra = new DijkstraLinearFunction(this.graphBounds.getReverseGraph());
	}

	public ArrayList<Long> reconstructPath(Node origin, Node destination, RouteQueueEntry route, 
			HashMap<Integer, HashMap<Integer, Integer>> parents){
		ArrayList<Long> path = new ArrayList<Long>();
		//ArrayList<Integer> path = new ArrayList<Integer>();
		int qId = convertToInt(origin.getId());
		int size = route.getRoute().size();
		for(int id = 0; id < size; id++){
			path.addAll(pathToPoi(id, qId, convertToInt(route.getRoute().get(id).getId()), parents));
			qId = convertToInt(route.getRoute().get(id).getId());
		}

		path.addAll(pathToDestination(destination, size, convertToInt(route.getRoute().get(size - 1).getId()), parents));
		path.add(destination.getId());
		return path;
	}

	private ArrayList<Long> pathToPoi(int pos, int id, int poiId, HashMap<Integer, HashMap<Integer, Integer>> parents){

		ArrayList<Long> path = new ArrayList<Long>();
		long parent = parents.get(pos + 1).get(poiId);

		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get((int)parent);
		}
		path.add((long)id);
		Collections.reverse(path);
		return path;
	}

	private ArrayList<Long> pathToDestination(Node d, int pos, int id, 	HashMap<Integer, HashMap<Integer, Integer>> parents){
		int did = convertToInt(d.getId());
		ArrayList<Long> path = new ArrayList<Long>();
		long parent = parents.get(pos).get(did);
		while(parent != id && parent != -1){
			path.add(parent);
			parent = parents.get(pos).get((int)parent);
		}
		path.add((long)id);
		Collections.reverse(path);
		return path;
	}

	public Sequence search(Node origin, Node destination, Date time, List<Integer> categories){

		PriorityQueue<RouteQueueEntry> queue = new PriorityQueue<RouteQueueEntry>();
		HashMap<Integer, HashMap<Integer, Integer>> parents = new HashMap<Integer, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> wasTraversed = new HashMap<Integer, HashMap<Integer, Integer>>();

		// Is this 'destinationPaths' keeping all shortest paths 
		// from the destination node of OSR to all other nodes?
		Long2DoubleMap destinationPaths = new Long2DoubleOpenHashMap();
		destinationPaths = dijkstra.shortestPath(destination.getId());

		Sequence seq = new Sequence();
		int t = DateUtils.dateToMinutes(time);
		int wt, ts, upper = Integer.MAX_VALUE;
		int nextCat, nextId;
		init(origin, destination, categories, t, queue, destinationPaths);
		RouteQueueEntry removed = null;
		ArrayList<NearestNeighborTC> reachedNN;

		while(!queue.isEmpty()){
			removed = queue.poll();
			addWasTraversed(removed.getRoute().size(), convertToInt(removed.getId()), wasRemoved, wasTraversed);
			addParent(removed.getRoute().size(), convertToInt(removed.getId()), convertToInt(removed.getParent()), parents);

			if(removed.getId() == convertToInt(destination.getId())){
				if(removed.getRoute().size() >= categories.size()){
					return new Sequence(removed.getId(), removed.getTravelTime(), reconstructPath(origin, destination, removed, parents), removed.getRoute());
				}
			}

			if(removed.getLowerBound() > upper) {

				return seq;

			}

			Long2IntMap neig = graphBounds.accessNeighborhood(graphBounds.getNode(removed.getId()),(short)0, removed.getArrivalTime());

			for (long v : neig.keySet()) {
				int vid = convertToInt(v);
				nextId = removed.getRoute().size();
				int tt = removed.getTravelTime() + neig.get(v);
				wt = 0;
				reachedNN = new ArrayList<NearestNeighborTC>(removed.getRoute());

				if(nextId < categories.size()){
					nextCat = categories.get(nextId);
					Node poi = ((Graph) graphBounds).getPoi(vid);
					if(poi != null){
						if(poi.getCategory() == nextCat){
							wt = ((Graph) graphBounds).poiGetCost(vid, removed.getArrivalTime());
							ts = wt + tt;
							NearestNeighborTC nn = new NearestNeighborTC(vid, tt, wt, ts);
							reachedNN.add(nn);
							nextId++;
						}
					}
				}
				int at = graphBounds.getArrival(removed.getArrivalTime() + wt, neig.get(v));
				int lb = (int) lowerBound(vid, nextId, categories, destinationPaths);
				RouteQueueEntry newEntry = new RouteQueueEntry(	vid, tt, at, convertToInt(removed.getId()), tt + lb, reachedNN);

				int pos = newEntry.getRoute().size();
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
		}
		return seq;
	}

	private boolean wasRemoved(int id, int pos, List<Integer> c, 
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
			List<Integer> c){
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

	private int lowerBound(int id, int pos, List<Integer> categories, Long2DoubleMap destination){
		int max = (int) destination.get(id);
		if(pos < categories.size()){
			int distance;
			for(int i = pos; i < categories.size(); i++){
				distance = getBoundsRoute().getBound(id, (int)categories.get(i)).getCost();
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

	private void init(Node origin, Node d, List<Integer> categories, int t, PriorityQueue<RouteQueueEntry> queue, 
			Long2DoubleMap destinationPaths){

		int pos = 0;
		ArrayList<NearestNeighborTC> reached = new ArrayList<NearestNeighborTC>();
		int travelTime, waitingTime, timeToService;
		travelTime = 0;
		int originId = convertToInt(origin.getId());

		Node poi = ((Graph) graphBounds).getPoi(originId);

		if(poi != null) {

			if(poi.getCategory() == categories.get(0)){
				pos++;
				waitingTime = ((Graph) graphBounds).poiGetCost(originId, t);
				timeToService = travelTime + waitingTime;
				NearestNeighborTC nn = new NearestNeighborTC(originId, travelTime, waitingTime, timeToService);
				reached.add(nn);
			}
		}

		int lb = lowerBound(originId, pos, categories, destinationPaths);

		queue.offer(new RouteQueueEntry(originId, travelTime, t, -1, t + lb, reached));
	}

	public Graph getGraphAdapter() {
		return graphBounds;
	}

	public void setGraphAdapter(GraphBounds graphAdapter) {
		this.graphBounds = graphAdapter;
	}

	//TODO URGENT REFACTOR IN THIS METHOD
	public Path getFullPath(Node origin, Node destination, Date time, List<Integer> categories) {
		
		List<Long> result = this.search(origin, destination, time, categories).getPath();

		List<Path> allPaths = new ArrayList<Path>();

		serviceGraph = new DijkstraConstantWeight(graphBounds);

		for(int i=0; i<result.size(); i++) {
			if(i==result.size()-1) {
				break;
			}

			Long source = graphBounds.getNodeId(graphBounds.getNode(result.get(i)).getLatitude(),graphBounds.getNode(result.get(i)).getLongitude());
			Long target = graphBounds.getNodeId(graphBounds.getNode(result.get(i+1)).getLatitude(),graphBounds.getNode(result.get(i+1)).getLongitude());

			StopWatch sw = new StopWatch();

			sw.start();
			Path shortestPath = serviceGraph.shortestPath(source, target);
			sw.stop();

			Node possiblePoI = graphBounds.getNode(source);
			List<PoI> temporaryListOfPoIs = new ArrayList<PoI>();
			
			List<Integer> listOfPois = categories;
			
			if(possiblePoI.getCategory()>0) {
				if(listOfPois.contains(possiblePoI.getCategory())) {
					PoICategory poiCategory = new PoICategory(possiblePoI.getCategory());
					PoI temporaryPoI = new PoI(possiblePoI.getLabel(), 
							possiblePoI.getLatitude(), possiblePoI.getLongitude(), poiCategory);
					temporaryListOfPoIs.add(temporaryPoI);
					
					listOfPois.remove((Integer)possiblePoI.getCategory());
				}
			}
			shortestPath.setListOfPoIs(temporaryListOfPoIs);
			allPaths.add(shortestPath);
		}
		Path resultPath = Path.pathsConcatanation(allPaths);
		return resultPath;
	}
	
	private BoundsRoute getBoundsRoute() {
		if (this.bounds == null) {
			this.bounds = new BoundsRoute(this.graphBounds, this.graphType);
			try {
				this.bounds.load();
			} catch (Exception e) {
				this.bounds.createBounds();
				this.bounds.save();
			}
		}
		return this.bounds;
	}

}
