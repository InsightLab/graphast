package org.graphast.query.rnn;

import java.util.Date;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;

public interface IRNNTimeDependent {
	
	public NearestNeighbor search(Node root, Date timeout, Date timestamp) throws PathNotFoundException;

}
