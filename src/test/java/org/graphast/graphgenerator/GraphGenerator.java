package org.graphast.graphgenerator;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastNode;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Helper;

public class GraphGenerator {

	public void generateExample() {

		String defaultGraphLoc = "/tmp/graphhopper/test/example";

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager).setLocation(defaultGraphLoc).setStore(true);
		GraphStorage graphStorage = gb.create();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 0, 2);
		na.setNode(1, 0, 1);
		na.setNode(2, 0, 4);
		na.setNode(3, 0, 3);
		na.setNode(4, 0, 5);
		na.setNode(5, 1, 1);

		EdgeIteratorState iter1 = graphStorage.edge(1, 5, 1, true);
		iter1.setWayGeometry(Helper.createPointList(3.5, 4.5, 5, 6));
		EdgeIteratorState iter2 = graphStorage.edge(5, 0, 2, false);
		iter2.setWayGeometry(Helper.createPointList(1.5, 1, 2, 3));
		EdgeIteratorState iter3 = graphStorage.edge(0, 3, 3, true);
		EdgeIteratorState iter4 = graphStorage.edge(3, 2, 4, false);
		EdgeIteratorState iter5 = graphStorage.edge(2, 3, 5, false);
		EdgeIteratorState iter6 = graphStorage.edge(2, 4, 6, true);
		EdgeIteratorState iter7 = graphStorage.edge(5, 4, 7, false);

		iter1.setName("Named Street 1");
		iter2.setName("Named Street 2");
		iter3.setName("Named Street 3");
		iter4.setName("Named Street 4");
		iter5.setName("Named Street 5");
		iter6.setName("Named Street 6");
		iter7.setName("Named Street 7");
		

		graphStorage.flush();
		graphStorage.close();

	}
	
	public Graphast generateExample2() {
		
 		Graphast graphast = new GraphastImpl("/tmp/graphhopper/test/example2");

 		GraphastEdge e;
 		GraphastNode v;

 		v = new GraphastNode(0l, 0d, 10d);
 		graphast.addNode(v);

 		v = new GraphastNode(1l, 10d, 0d);
 		graphast.addNode(v);

 		v = new GraphastNode(2l, 30d, 20d);
 		graphast.addNode(v);

 		v = new GraphastNode(3l, 40d, 20d);
 		graphast.addNode(v);

 		v = new GraphastNode(4l, 50d, 30d);
 		graphast.addNode(v);

 		v = new GraphastNode(5l, 60d, 20d);
 		graphast.addNode(v);

 		v = new GraphastNode(6l, 60d, 0d);
 		graphast.addNode(v);

 		e = new GraphastEdge(0l, 1l, 1);
 		graphast.addEdge(e);

 		e = new GraphastEdge(0l, 2l, 5);
 		graphast.addEdge(e);

 		e = new GraphastEdge(1l, 2l, 3);
 		graphast.addEdge(e);

 		e = new GraphastEdge(2l, 3l, 3);
 		graphast.addEdge(e);

 		e = new GraphastEdge(3l, 4l, 3);
 		graphast.addEdge(e);

 		e = new GraphastEdge(3l, 5l, 4);
 		graphast.addEdge(e);

 		e = new GraphastEdge(4l, 5l, 2);
 		graphast.addEdge(e);

 		e = new GraphastEdge(5l, 6l, 1);
 		graphast.addEdge(e);

 		return graphast;
 		
	}

}
