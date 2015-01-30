package org.graphast.query.route.osr;

import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Collection;
import java.util.Set;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
import org.graphast.query.knn.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;

public class BoundsRoute extends AbstractBoundsSearch {

	public BoundsRoute(Graph ga, long host, int index){
		super(host, index);
		DijkstraGeneric d = new DijkstraGeneric(ga);
		IntSet categories = ga.getCategories();
		
		Set<Integer> categories = ga.getCategories();
		
		for(int i = 0; i < ga.getNumberOfNodes(); i++){
			long position = i*Node.NODE_BLOCKSIZE;
			long vid = ga.getNodes().getInt(position);
			Collection<Bound> bound = d.shortestPathCategories(vid, categories);
			bounds.put(vid,  bound.toString());
		} 
	
	}
	
	public BoundsRoute(long host, int index){
		super(host, index);
	}
	
	public Bound getBound(int id, int cat){
		if(bounds.containsKey(Integer.toString(id))){
			String[] str = StringUtils.stringToObjectArray((String) bounds.get(Integer.toString(id)));
			for(int i = 0; i < str.length; i++){
				Bound b = new Bound(str[i]);
				if(b.getId() == cat){
					return b;
				}
			}
		}
		return new Bound();
	}
	
}
