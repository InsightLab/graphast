package org.graphast.graphgenerator;

import java.util.ArrayList;
import java.util.List;

import org.graphast.geometry.Point;
import org.graphast.importer.OSMImporter;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastNode;
import org.graphast.query.route.shortestpath.DijkstraShortestPathConstantWeight;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Helper;

public class GraphGenerator {

	public Graphast generateExample() {
		String graphastExampleDir = "/tmp/graphast/test/example";
		String graphHopperExampleDir = "/tmp/graphhopper/test/example";
		Graphast graphast = new GraphastImpl(graphHopperExampleDir);


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
		graphast = new OSMImporter().execute(null, graphHopperExampleDir, graphastExampleDir);
		
		return graphast;

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
	
	public Graphast generateMonaco() {
		String osmFile = DijkstraShortestPathConstantWeight.class.getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = "/tmp/graphhopper/test/monaco";
		String graphastMonacoDir = "/tmp/graphast/test/monaco";
		Graphast graphast = new OSMImporter().execute(osmFile, graphHopperMonacoDir, graphastMonacoDir);
		
		return graphast;
	}
	
	public Graphast generateExample3() {
		Graphast graph = new GraphastImpl("/tmp/graphast/test/example3");
		
		GraphastNode v = new GraphastNode(3l, 10d, 10d, "label node 0");
		graph.addNode(v);
		
		v = new GraphastNode(4l, 10d, 20d);
		graph.addNode(v);
		
		v = new GraphastNode(2l, 10d, 30d);
		graph.addNode(v);
		
		v = new GraphastNode(6l, 10d, 40d);
		graph.addNode(v);
		
		v = new GraphastNode(7l, 11d, 32d);
		graph.addNode(v);
		
		v = new GraphastNode(7, 11, 32, "Banco");
		graph.addNode(v);
		
		short[] costs = {1,2,3,4};
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,20));
		GraphastEdge e = new GraphastEdge(0l, 1l, 10, costs, points, "rua1");
//		public GraphastEdge(long fromNode, long toNode, int distance,
//				short[] costs, List<Point> geometry, String label)
		graph.addEdge(e);
		
		costs = new short[]{2,4,6,8,10};
		points = new ArrayList<Point>();
		points.add(new Point(10,20));
		points.add(new Point(10,15));
		points.add(new Point(10,10));
		e = new GraphastEdge(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,30));
		e = new GraphastEdge(0l, 2l, 30, costs, points, "rua3");
		graph.addEdge(e);
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,30));
		points.add(new Point(10,10));
		e = new GraphastEdge(2l, 0l, 40, costs, points, "rua4");
		graph.addEdge(e);
		
		costs = new short[]{3};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,40));
		e = new GraphastEdge(0l, 3l, 50, costs, points, "");
		graph.addEdge(e);
		
		e = new GraphastEdge(2l, 4l, 60);
		graph.addEdge(e);
		
		e = new GraphastEdge(3l, 0l, 70);
		graph.addEdge(e);
		
		return graph;
	}
	
}