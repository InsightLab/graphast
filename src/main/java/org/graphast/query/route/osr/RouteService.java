package org.graphast.query.route.osr;

import java.util.ArrayList;
import java.util.Date;

import org.graphast.model.Node;


public interface RouteService {

	public Sequence search(Node q, Node d, Date time, ArrayList<Integer> c);

}
