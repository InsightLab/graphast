package org.graphast.query.route.osr;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Collection;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.model.AbstractBoundsSearch;
import org.graphast.query.knn.model.Bound;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraGeneric;
import org.graphast.util.StringUtils;

public class BoundsRoute extends AbstractBoundsSearch {

	//TODO DOUBLE CHECK THIS CONSTRUCTOR!
	public BoundsRoute(Graph ga, long host, int index){
		super();
		DijkstraGeneric d = new DijkstraGeneric(ga);
		IntSet categories = ga.getCategories();
		
		for(int i = 0; i < ga.getNumberOfNodes(); i++){
			long position = i*Node.NODE_BLOCKSIZE;
			long vid = ga.getNodes().getInt(position);
			ObjectCollection<Bound> bound = d.shortestPathCategories(vid, categories);
			bounds.put(vid,  bound);
		} 
	
	}
	
	public BoundsRoute(){
		super();
	}
	
	public Bound getBound(int id, int cat){
		if(bounds.containsKey(id)){
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
