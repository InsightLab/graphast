package org.graphast.query.route.shortestpath.dijkstra;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.enums.GraphBoundsType;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.model.Bound;
import org.graphast.query.model.QueueEntry;

/**
 * Class with the generic form of Dijkstra Algorithm.
 * This class has:
 * -shortestPath method in many versions like(besides the basic):
 *  	-POIs
 *		-Categories
 *  	-TS
 * -expandVertex method(besides the basic):
 * 		-upperBound
 * 		-lowerBound
 */
public class DijkstraGeneric {
	private GraphBounds graph;
	
	public DijkstraGeneric(GraphBounds ga){
		this.graph = ga;
	}
	
	/**This is the basic expandVertex method with unsettled and settle nodes structure(Better form to initiate tests)
	 * 
	 * @param e QueueEntry of a node with travel time 
	 * @param settleNodes Set of settled node's id 
	 * @param shortestDistances HashMap with node's id and shortestDistance 
	 * @param unsettledNodes Set of unsettled node's id 
	 * 
	 * */
	public void expandVertex(QueueEntry e, Set<Long> settledNodes, HashMap<Long, Integer> shortestDistances,
			PriorityQueue<QueueEntry> unsettledNodes){
		Long2IntMap adj = graph.accessNeighborhood(graph.getNode(e.getId()));
        if(adj != null){
        	for (long v : adj.keySet()){
        		long vid = v;
                if (settledNodes.contains(vid))    continue;
                
                int shortDist = getShortestDistance(e.getId(), shortestDistances) + adj.get(v);
                
                if (shortDist < getShortestDistance(vid, shortestDistances))
                {
                	// assign new shortest distance and mark unsettled
                	
                	QueueEntry eNew = new QueueEntry(vid, shortDist);
                	unsettledNodes.remove(eNew);
                	unsettledNodes.add(eNew);
                    shortestDistances.put(vid, shortDist);
                }
            } 
        }
	}
	
	public void expandVertexUpperBound(QueueEntry e, Set<Long> settledNodes, HashMap<Long, Integer> shortestDistances,
			PriorityQueue<QueueEntry> unsettledNodes){
		Long2IntMap adj = accessNeighborhoodUpperBound(graph.getNode(e.getId()));
        if(adj != null){
        	for (long v : adj.keySet()){
        		long vid = v;
                if (settledNodes.contains(vid))    continue;
                
                int shortDist = getShortestDistance(e.getId(), shortestDistances) + adj.get(v);
                
                if (shortDist < getShortestDistance(vid, shortestDistances))
                {
                	// assign new shortest distance and mark unsettled
                	
                	QueueEntry eNew = new QueueEntry(vid, shortDist);
                	unsettledNodes.remove(eNew);
                	unsettledNodes.add(eNew);
                    shortestDistances.put(vid, shortDist);
                }
            } 
        }
	}
	
	public void expandVertexLowerBound(QueueEntry e, Set<Long> settledNodes, HashMap<Long, Integer> shortestDistances,
			PriorityQueue<QueueEntry> unsettledNodes){
		Long2IntMap adj = accessNeighborhoodLowerBound(graph.getNode(e.getId()));
        if(adj != null){
        	for (long v : adj.keySet()){
        		long vid = v;
                if (settledNodes.contains(vid))    continue;
                
                int shortDist = getShortestDistance(e.getId(), shortestDistances) + adj.get(v);
                
                if (shortDist < getShortestDistance(vid, shortestDistances))
                {
                	// assign new shortest distance and mark unsettled
                	
                	QueueEntry eNew = new QueueEntry(vid, shortDist);
                	unsettledNodes.remove(eNew);
                	unsettledNodes.add(eNew);
                    shortestDistances.put(vid, shortDist);
                }
            } 
        }
	}
	
	/** The basic shortestPath (find distances) with settle and unsettled structure (Better method to initiate tests with Dijkstra)
	 * 
	 * @param v long id for a node
	 * @return a HashMap with shortestDistances(int) from node v to all others nodes. (HashMap<Long, Integer>) 
	 * 
	 * */
	public HashMap<Long, Integer> shortestPath(long v){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
	    Set<Long> settledNodes = new HashSet<Long>();
	    HashMap<Long, Integer> shortestDistances = new HashMap<Long, Integer>();
	    HashMap<Long, Integer> distance = new HashMap<Long, Integer>();
	    
	    shortestDistances.put(v, 0);
	    distance.put(v, 0);
	    QueueEntry e = new QueueEntry(v, 0);
        unsettledNodes.add(e);
        
        while ((e = unsettledNodes.poll()) != null){
        	if(!settledNodes.contains(e.getId())){
        		settledNodes.add(e.getId());
                distance.put(e.getId(), e.getTravelTime());
                
                expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
        	}
        } 
        return distance;
	}
	
	public Bound shortestPathPoi(long v, int idCat, GraphBoundsType type){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
	    Set<Long> settledNodes = new HashSet<Long>();
	    HashMap<Long, Integer> shortestDistances = new HashMap<Long, Integer>();
	    
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
                if(type.equals(GraphBoundsType.NORMAL)) {
                	expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
				}
				else if (type.equals(GraphBoundsType.LOWER)) {
					expandVertexLowerBound(e, settledNodes, shortestDistances, unsettledNodes);
				}
				else {
					expandVertexUpperBound(e, settledNodes, shortestDistances, unsettledNodes);
				}
                
        	}
        }        
        return new Bound();
	}
	
	
	public ObjectCollection<Bound> shortestPathCategories(long v, Set<Integer> idCat){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
	    Set<Long> settledNodes = new HashSet<Long>();
	    HashMap<Long, Integer> shortestDistances = new HashMap<Long, Integer>();
	    Long2ObjectMap<Bound> bounds = new Long2ObjectOpenHashMap<Bound>();
	    int upper = Integer.MIN_VALUE;
	    int waitingTime;
	    int timeToService;
	    
	    shortestDistances.put(v, 0);
	    QueueEntry e = new QueueEntry(v, 0);
        unsettledNodes.add(e);
        
        while ((e = unsettledNodes.poll()) != null){
            if(bounds.keySet().containsAll(idCat) && e.getTravelTime() > upper){
            	return bounds.values();
            }
            if(!settledNodes.contains(e.getId())){
            	settledNodes.add(e.getId());
            	
                Node poi = ((Graph) graph).getPoi(e.getId());
                if(poi != null){
                	int cat = poi.getCategory();
                	waitingTime = graph.poiGetCost(e.getId());
            		timeToService = e.getTravelTime() + waitingTime;
                	if(bounds.keySet().contains(cat)){
                		upper = updateUpper(bounds);
                	}else{
                		bounds.put((long)cat, new Bound(e.getId(), timeToService));
                		if(timeToService > upper)	upper = timeToService;
                	}
                }
                
                expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
            }
        }        
        return bounds.values();
	}
	
	public int updateUpper(Long2ObjectMap<Bound> bounds){
		int upper = Integer.MIN_VALUE;
		for(Bound b: bounds.values()){
			if(b.getCost() > upper)	upper = b.getCost();
		}
		return upper;
	}
	
	public Bound shortestTS(long v, GraphBoundsType type){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
	    Set<Long> settledNodes = new HashSet<Long>();
	    HashMap<Long, Integer> shortestDistances = new HashMap<Long, Integer>();
	    int wt;
	    int ts;
	    Bound best = new Bound(-1, Integer.MAX_VALUE);
	    
	    shortestDistances.put(v, 0);
	    QueueEntry e = new QueueEntry(v, 0);
        unsettledNodes.add(e);
        
        while ((e = unsettledNodes.poll()) != null){
            if(e.getTravelTime() > best.getCost()){
            	return best;
            }
            
            if(!settledNodes.contains(e.getId())){
            	settledNodes.add(e.getId());
                Node poi = graph.getPoi(e.getId());
                if(poi != null && e.getTravelTime() < best.getCost()){
                	wt = graph.poiGetCost(e.getId());
                	ts = e.getTravelTime() + wt;
                	best = new Bound(e.getId(), ts);
                }
                if(type.equals(GraphBoundsType.NORMAL)) {
                	expandVertex(e, settledNodes, shortestDistances, unsettledNodes);
				}
				else if (type.equals(GraphBoundsType.LOWER)) {
					expandVertexLowerBound(e, settledNodes, shortestDistances, unsettledNodes);
				}
				else {
					expandVertexUpperBound(e, settledNodes, shortestDistances, unsettledNodes);
				}
                
            }
        }        
        return best;
	}
	
	public HashMap<Long, Integer> shortestPath(long v, Set<Long> destination){
		PriorityQueue<QueueEntry> unsettledNodes = new PriorityQueue<QueueEntry>();
	    Set<Long> settledNodes = new HashSet<Long>();
	    HashMap<Long, Integer> shortestDistances = new HashMap<Long, Integer>();
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

	public int getShortestDistance(long id, HashMap<Long, Integer> minCost)
    {
		if(minCost.containsKey(id))
			return minCost.get(id);
		else
         return Integer.MAX_VALUE;
    }
	
	public Long2IntMap accessNeighborhoodUpperBound(Node v){
		Long2IntMap neig = new Long2IntOpenHashMap();
		for (Long e : graph.getOutEdges( v.getId())) {
			Edge edge = graph.getEdge(e);
			long vNeig =  edge.getToNode();
			int cost =  graph.getEdgeUpperCost(e);
			if(!neig.containsKey(vNeig)){
				neig.put(vNeig, cost);
			}else{
				if(neig.get(vNeig) > cost){
					neig.put(vNeig, cost);
				}
			}
		}
		return neig;
	}	
	
	public Long2IntMap accessNeighborhoodLowerBound(Node v){
		Long2IntMap neig = new Long2IntOpenHashMap();
		for (Long e : graph.getOutEdges( v.getId())) {
			Edge edge = graph.getEdge(e);
			long vNeig =  edge.getToNode();
			int cost =  graph.getEdgeLowerCost(e);
			if(!neig.containsKey(vNeig)){
				neig.put(vNeig, cost);
			}else{
				if(neig.get(vNeig) > cost){
					neig.put(vNeig, cost);
				}
			}
		}
		return neig;
	}	
}		
	