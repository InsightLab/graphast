package org.graphast.graphgenerator;

import java.util.ArrayList;
import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.geometry.Point;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
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
		
		String graphastExampleDir = Configuration.USER_HOME + "/graphast/test/example";
		String graphHopperExampleDir = Configuration.USER_HOME + "/graphhopper/test/example";
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
		graph = new OSMImporterImpl(null, graphHopperExampleDir, graphastExampleDir).execute();

		return graph;

	}

	public GraphBounds generateExample2() {

		GraphBounds graph = new GraphBoundsImpl(Configuration.USER_HOME + "/graphast/test/example2");

		Edge e;
		NodeImpl v;

		v = new NodeImpl(0l, 0d, 10d, 0);
		graph.addNode(v);

		v = new NodeImpl(1l, 10d, 0d, 1);
		graph.addNode(v);

		v = new NodeImpl(2l, 30d, 20d, 2);
		graph.addNode(v);

		v = new NodeImpl(3l, 40d, 20d, 4);
		graph.addNode(v);

		v = new NodeImpl(4l, 50d, 30d, 0);
		graph.addNode(v);

		v = new NodeImpl(5l, 60d, 20d);
		graph.addNode(v);

		v = new NodeImpl(6l, 60d, 0d);
		graph.addNode(v);

		int[] costs = {3,2,3,4};
		e = new EdgeImpl(0l, 1l, 1, costs);
		graph.addEdge(e);

		costs = new int[]{4,2,6,8,10};
		e = new EdgeImpl(0l, 2l, 5, costs);
		graph.addEdge(e);

		costs = new int[]{1,2};
		e = new EdgeImpl(1l, 2l, 3, costs);
		graph.addEdge(e);

		costs = new int[]{4,4,7,6,11};

		e = new EdgeImpl(2l, 3l, 3, costs);

		graph.addEdge(e);

		costs = new int[]{1,10};
		e = new EdgeImpl(3l, 4l, 3, costs);
		graph.addEdge(e);

		costs = new int[]{2,12,13};
		e = new EdgeImpl(3l, 5l, 4, costs);
		graph.addEdge(e);

		costs = new int[]{3,9,10,11};
		e = new EdgeImpl(4l, 5l, 2, costs);
		graph.addEdge(e);

		costs = new int[]{5,2,4,6,8,15};
		e = new EdgeImpl(5l, 6l, 1, costs);
		graph.addEdge(e);
		graph.createBounds();

		return graph;

	}

	public Graph generateMonaco() {
		
		String osmFile = DijkstraConstantWeight.class.getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		Graph graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).execute();

		return graph;
		
	}

	public Graph generateExample3() {
		
		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/example3");

		NodeImpl v = new NodeImpl(3l, 10d, 10d, "label node 0");
		graph.addNode(v);

		v = new NodeImpl(4l, 43.7294668047756,7.413772473047058);
		graph.addNode(v);

		int[] nodeCosts = new int[]{1,2,3,4};
		v = new NodeImpl(2l, 10d, 30d, nodeCosts);
		graph.addNode(v);

		v = new NodeImpl(6l, 10d, 40d);
		graph.addNode(v);

		v = new NodeImpl(7l, 11d, 32d);
		graph.addNode(v);

		v = new NodeImpl(7, 11, 32, "Banco");
		graph.addNode(v);

		int[] costs = new int[]{1,2,3,4};
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,20));
		Edge e = new EdgeImpl(0l, 1l, 10, costs, points, "rua1");
		graph.addEdge(e);

		costs = new int[]{2,4,6,8,10};
		points = new ArrayList<Point>();
		points.add(new Point(10,20));
		points.add(new Point(10,15));
		points.add(new Point(10,10));
		e = new EdgeImpl(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);

		costs = new int[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,30));
		e = new EdgeImpl(0l, 2l, 30, costs, points, "rua3");
		graph.addEdge(e);

		costs = new int[]{2, 4, 6, 8, 10};
		points = new ArrayList<Point>();
		points.add(new Point(10,30));
		points.add(new Point(10,10));
		e = new EdgeImpl(2l, 0l, 40, costs, points, "rua4");
		graph.addEdge(e);

		costs = new int[]{3};
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

	public Graph generateExample4() {
		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphast/example");

		Edge e;
		NodeImpl v;

		v = new NodeImpl(0l, 0d, 10d, 0);
		graph.addNode(v);

		v = new NodeImpl(1l, 10d, 0d, 1);
		graph.addNode(v);

		v = new NodeImpl(2l, 30d, 20d, 2);
		graph.addNode(v);

		v = new NodeImpl(3l, 40d, 20d, 4);
		graph.addNode(v);

		v = new NodeImpl(4l, 50d, 30d, 0);
		graph.addNode(v);

		v = new NodeImpl(5l, 60d, 20d);
		graph.addNode(v);

		v = new NodeImpl(6l, 60d, 0d);
		graph.addNode(v);

		int[] costs = {1, 3};
		e = new EdgeImpl(0l, 1l, 1, costs, null, "Rua A");
		graph.addEdge(e);

		costs = new int[]{5};
		e = new EdgeImpl(0l, 2l, 5, costs, null, "Rua B");
		graph.addEdge(e);

		costs = new int[]{3};
		e = new EdgeImpl(1l, 2l, 3, costs, null, "Rua C");
		graph.addEdge(e);

		costs = new int[]{3};
		e = new EdgeImpl(2l, 3l, 3, costs, null, "Rua D");

		graph.addEdge(e);

		costs = new int[]{3};
		e = new EdgeImpl(3l, 4l, 3, costs, null, "Rua E");
		graph.addEdge(e);

		costs = new int[]{4, 6};
		e = new EdgeImpl(3l, 5l, 4, costs, null, "Rua F");
		graph.addEdge(e);

		costs = new int[]{2};
		e = new EdgeImpl(4l, 5l, 2, costs, null, "Rua G");
		graph.addEdge(e);

		costs = new int[]{1};
		e = new EdgeImpl(5l, 6l, 1, costs, null, "Rua H");
		graph.addEdge(e);

		return graph;
	}

	
	public GraphBounds generateExamplePoI() {

		GraphBounds graph = new GraphBoundsImpl(Configuration.USER_HOME + "/graphast/test/examplePoI");

		Node node;
		Edge edge;

		//NODES

		node = new NodeImpl(0l, 0.0d, 1.0d);
		graph.addNode(node);

		node = new NodeImpl(1l, 0.0d, 10.0d);
		int[] costs = new int[]{30300000, 1500000, 1500000, 1500000, 900000, 900000, 59100000, 30300000};
		node.setCategory(1);
		node.setLabel("Bradesco");
		node.setCosts(costs);
		graph.addNode(node);

		node = new NodeImpl(2l, 0.0d, 20.0d);
		graph.addNode(node);

		node = new NodeImpl(3l, 0.0d, 30.0d);
		graph.addNode(node);

		node = new NodeImpl(4l, 0.0d, 40.0d);
		costs = new int[]{29700000, 900000, 900000, 900000, 1500000, 1500000, 58500000, 17700000};
		node.setCategory(2);
		node.setLabel("Padaria Costa Mendes");
		node.setCosts(costs);
		graph.addNode(node);

		node = new NodeImpl(5l, 10.0d, 0.0d);
		graph.addNode(node);

		node = new NodeImpl(6l, 10.0d, 10.0d);
		graph.addNode(node);

		node = new NodeImpl(7l, 10.0d, 20.0d);
		graph.addNode(node);

		node = new NodeImpl(8l, 10.0d, 30.0d);
		graph.addNode(node);

		node = new NodeImpl(9l, 10.0d, 40.0d);
		costs = new int[]{30300000, 1500000, 1500000, 1500000, 900000, 900000, 59100000, 30300000};
		node.setCategory(3);
		node.setLabel("Escola Vila");
		node.setCosts(costs);
		graph.addNode(node);

		//TODO Create a constructor for edge without the distance

		//EDGES

		edge = new EdgeImpl(1l, 0l, 1l, 10);
		costs = new int[]{900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 1200000,
				1200000, 1200000, 900000, 900000, 900000, 1500000, 1500000, 1500000, 1500000, 900000, 900000,
				900000,	900000, 900000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(2l, 1l, 2l, 20);
		costs = new int[]{900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 
				900000, 900000, 900000, 900000, 900000, 900000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(3l, 1l, 7l, 30);
		costs = new int[]{720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000,
				1320000, 1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000, 720000,
				720000, 720000,	720000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(4l, 2l, 3l, 40);
		costs = new int[]{600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000,
				600000,	600000,	600000,	600000,	600000,	600000,	600000,	600000,	600000,	600000,	600000,
				600000,	600000,	600000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(5l, 3l, 4l, 50);
		costs = new int[]{720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000,
				1320000, 1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000,
				720000, 720000, 720000, 720000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(6l, 4l, 8l, 60);
		costs = new int[]{720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000,
				720000,	720000,	720000,	720000,	720000,	720000,	720000,	720000,	720000,	720000,	720000,
				720000,	720000,	720000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(7l, 4l, 9l, 70);
		costs = new int[]{720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 1080000,
				1080000, 1080000, 600000, 600000, 600000, 1500000, 1500000, 1500000, 1500000, 600000, 600000,
				600000,	600000,	600000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(8l, 5l, 0l, 80);
		costs = new int[]{720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000,
				1080000, 1080000, 1080000, 600000, 600000, 600000, 1080000,	1080000, 1080000,
				1080000, 600000, 600000, 600000, 600000, 600000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(9l, 6l, 5l, 90);
		costs = new int[]{900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				1200000, 1200000, 1200000, 900000, 900000, 900000, 1500000, 1500000, 1500000, 1500000,
				900000, 900000, 900000, 900000, 900000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(10l, 7l, 2l, 100);
		costs = new int[]{900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 
				900000, 900000, 900000, 900000, 900000, 900000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(11l, 7l, 6l, 110);
		costs = new int[]{720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 1080000,
				1080000, 1080000, 600000, 600000, 600000, 1080000, 1080000, 1080000, 1080000, 600000, 600000,
				600000, 600000,	600000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(12l, 8l, 7l, 120);
		costs = new int[]{720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000,
				1320000, 1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000, 720000,
				720000, 720000,	720000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(13l, 9l, 8l, 130);
		costs = new int[]{900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 
				900000, 900000, 900000, 900000, 900000, 900000};
		edge.setCosts(costs);
		graph.addEdge(edge);

		graph.createBounds();

		return graph;
		
	}

	public Graph generateAndorra() {
		
		String osmFile = DijkstraConstantWeight.class.getResource("/andorra-150305.osm.pbf").getPath();
		String graphHopperAndorraDir = Configuration.USER_HOME + "/graphhopper/test/andorra";
		String graphastAndorraDir = Configuration.USER_HOME + "/graphast/test/andorra";

		Graph graph = new OSMImporterImpl(osmFile, graphHopperAndorraDir, graphastAndorraDir).execute();

		return graph;
		
	}

}