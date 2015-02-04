package org.graphast.query.model;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.longs.Long2IntMap;
=======
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
>>>>>>> modification on Bounds of KNN. need of cost modification



public interface BoundsSearch {
<<<<<<< HEAD
	public Long2IntMap getBounds();
	
	public void setBounds(Long2IntMap bounds) ;
}
=======
	public Long2ShortMap getBounds();
	
<<<<<<< HEAD:src/main/java/org/graphast/query/model/BoundsSearch.java
	public void setBounds(HashMap<Long, Integer> bounds) ;
}
=======
	public void setBounds(Long2ShortMap bounds) ;
}
>>>>>>> modification on Bounds of KNN. need of cost modification:src/main/java/org/graphast/query/knn/model/BoundsSearch.java
>>>>>>> modification on Bounds of KNN. need of cost modification
