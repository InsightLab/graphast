package org.graphast.graphgenerator;

import java.util.ArrayList;
import java.util.List;

import org.graphast.geometry.Point;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphImpl;
import org.graphast.model.NodeImpl;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Helper;

public class GraphGenerator {

	public Graph generateExample() {
		String graphastExampleDir = "/tmp/graphast/test/example";
		String graphHopperExampleDir = "/tmp/graphhopper/test/example";
		Graph graph = new GraphImpl(graphHopperExampleDir);

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager).setLocation(graphHopperExampleDir).setStore(true);
		GraphStorage graphStorage = gb.create();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 0, 2);
		na.setNode(1, 0, 1);
		na.setNode(2, 0, 4);
		na.setNode(3, 0, 3);
		na.setNode(4, 0, 5);
		na.setNode(5, 1, 1);

		EdgeIteratorState iter1 = graphStorage.edge(1, 5, 1.1, true);
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
		graph = new OSMImporterImpl(graphHopperExampleDir, graphastExampleDir).execute();
		
		return graph;

	}
	
	public Graph generateExample2() {
		
 		Graph graph = new GraphImpl("/tmp/graphhopper/test/example2");

 		Edge e;
 		NodeImpl v;

 		v = new NodeImpl(0l, 0d, 10d);
 		graph.addNode(v);

 		v = new NodeImpl(1l, 10d, 0d);
 		graph.addNode(v);

 		v = new NodeImpl(2l, 30d, 20d);
 		graph.addNode(v);

 		v = new NodeImpl(3l, 40d, 20d);
 		graph.addNode(v);

 		v = new NodeImpl(4l, 50d, 30d);
 		graph.addNode(v);

 		v = new NodeImpl(5l, 60d, 20d);
 		graph.addNode(v);

 		v = new NodeImpl(6l, 60d, 0d);
 		graph.addNode(v);

 		e = new EdgeImpl(0l, 1l, 1);
 		graph.addEdge(e);

 		e = new EdgeImpl(0l, 2l, 5);
 		graph.addEdge(e);

 		e = new EdgeImpl(1l, 2l, 3);
 		graph.addEdge(e);

 		e = new EdgeImpl(2l, 3l, 3);
 		graph.addEdge(e);

 		e = new EdgeImpl(3l, 4l, 3);
 		graph.addEdge(e);

 		e = new EdgeImpl(3l, 5l, 4);
 		graph.addEdge(e);

 		e = new EdgeImpl(4l, 5l, 2);
 		graph.addEdge(e);

 		e = new EdgeImpl(5l, 6l, 1);
 		graph.addEdge(e);

 		return graph;
 		
	}
	
	public Graph generateMonaco() {
		String osmFile = DijkstraConstantWeight.class.getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = "/tmp/graphhopper/test/monaco";
		String graphastMonacoDir = "/tmp/graphast/test/monaco";
		
		Graph graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).execute();
		
		return graph;
	}
	
	public Graph generateExample3() {
		Graph graph = new GraphImpl("/tmp/graphast/test/example3");
		
		NodeImpl v = new NodeImpl(3l, 10d, 10d, "label node 0");
		graph.addNode(v);
		
		v = new NodeImpl(4l, 43.7294668047756,7.413772473047058);
		graph.addNode(v);
		
		v = new NodeImpl(2l, 10d, 30d);
		graph.addNode(v);
		
		v = new NodeImpl(6l, 10d, 40d);
		graph.addNode(v);
		
		v = new NodeImpl(7l, 11d, 32d);
		graph.addNode(v);
		
		v = new NodeImpl(7, 11, 32, "Banco");
		graph.addNode(v);
		
		short[] costs = {1,2,3,4};
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,20));
		Edge e = new EdgeImpl(0l, 1l, 10, costs, points, "rua1");
		graph.addEdge(e);
		
		costs = new short[]{2,4,6,8,10};
		points = new ArrayList<Point>();
		points.add(new Point(10,20));
		points.add(new Point(10,15));
		points.add(new Point(10,10));
		e = new EdgeImpl(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,30));
		e = new EdgeImpl(0l, 2l, 30, costs, points, "rua3");
		graph.addEdge(e);
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,30));
		points.add(new Point(10,10));
		e = new EdgeImpl(2l, 0l, 40, costs, points, "rua4");
		graph.addEdge(e);
		
		costs = new short[]{3};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,40));
		e = new EdgeImpl(0l, 3l, 50, costs, points, "");
		graph.addEdge(e);
		
		e = new EdgeImpl(2l, 4l, 60);
		graph.addEdge(e);
		
		e = new EdgeImpl(3l, 0l, 70);
		graph.addEdge(e);
		
		return graph;
	}
	
}