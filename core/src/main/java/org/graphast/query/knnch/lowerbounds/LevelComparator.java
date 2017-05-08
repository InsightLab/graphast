package org.graphast.query.knnch.lowerbounds;

import java.util.Comparator;

import org.graphast.query.route.shortestpath.model.DistanceEntry;

public class LevelComparator implements Comparator<DistanceEntry>{

	@Override
	public int compare(DistanceEntry d1, DistanceEntry d2) {
		// TODO Auto-generated method stub
		if (d1.getParent() > d2.getParent())
        {
            return -1;
        }
        if (d1.getParent() < d2.getParent())
        {
            return 1;
        }
        return 0;
	}


	
	
}
