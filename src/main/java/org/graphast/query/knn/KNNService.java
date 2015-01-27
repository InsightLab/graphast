package org.graphast.query.knn;

import java.util.Date;
import java.util.List;

import org.graphast.model.Node;

public interface KNNService {
	List<NearestNeighbor> search(Node v, Date time, int k);
}
