package org.graphast.piecewise;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.graphast.model.GraphImpl;
import org.graphast.model.NodeImpl;
import org.junit.Test;

public class GeneratorGraphFunctionPiecewiseTest {

	
	@Test
	public void createGraphTest() throws PiecewiseException {
		
		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/examplepiecewise");
		
		NodeImpl node1 = new NodeImpl(1l, 10d, 10d, "label node 1");
		graph.addNode(node1);
		
		NodeImpl node2 = new NodeImpl(1l, 10d, 10d, "label node 1");
		graph.addNode(node2);
	}
}
