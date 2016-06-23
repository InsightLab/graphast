package org.graphast.graphgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.geometry.Point;
import org.graphast.importer.CostGenerator;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.importer.POIImporter;
import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
import org.graphast.model.NodeImpl;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHGraphImpl;
import org.graphast.model.contraction.CHNode;
import org.graphast.model.contraction.CHNodeImpl;
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

	public Graph generateExample1() {

		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/example1");

		NodeImpl v = new NodeImpl(3l, 10d, 10d, "label node 0");
		graph.addNode(v);

		v = new NodeImpl(4l, 43.7294668047756, 7.413772473047058);
		graph.addNode(v);

		int[] nodeCosts = new int[] { 1, 2, 3, 4 };
		v = new NodeImpl(2l, 10d, 30d, nodeCosts);
		graph.addNode(v);

		v = new NodeImpl(6l, 10d, 40d);
		graph.addNode(v);

		v = new NodeImpl(7l, 11d, 32d);
		graph.addNode(v);

		v = new NodeImpl(7, 11, 32, "Banco");
		graph.addNode(v);

		int[] costs = CostGenerator.generateSyntheticEdgesCosts(10);
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 20));
		Edge e = new EdgeImpl(0l, 1l, 10, costs, points, "rua1");
		graph.addEdge(e);

		costs = CostGenerator.generateSyntheticEdgesCosts(20);
		points = new ArrayList<Point>();
		points.add(new Point(10, 20));
		points.add(new Point(10, 15));
		points.add(new Point(10, 10));
		e = new EdgeImpl(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);

		costs = CostGenerator.generateSyntheticEdgesCosts(30);
		points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 30));
		e = new EdgeImpl(0l, 2l, 30, costs, points, "rua3");
		graph.addEdge(e);

		costs = CostGenerator.generateSyntheticEdgesCosts(40);
		points = new ArrayList<Point>();
		points.add(new Point(10, 30));
		points.add(new Point(10, 10));
		e = new EdgeImpl(2l, 0l, 40, costs, points, "rua4");
		graph.addEdge(e);

		costs = CostGenerator.generateSyntheticEdgesCosts(50);
		points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 40));
		e = new EdgeImpl(0l, 3l, 50, costs, points, "");
		graph.addEdge(e);

		e = new EdgeImpl(2l, 4l, 60);
		graph.addEdge(e);

		e = new EdgeImpl(3l, 0l, 70);
		graph.addEdge(e);

		return graph;
	}

	public GraphBounds generateExample2() {

		GraphBounds graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/example2");

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

		int[] costs = { 3, 2, 3, 4 };
		e = new EdgeImpl(0l, 1l, 1, costs);
		graph.addEdge(e);

		costs = new int[] { 4, 2, 6, 8, 10 };
		e = new EdgeImpl(0l, 2l, 5, costs);
		graph.addEdge(e);

		costs = new int[] { 1, 2 };
		e = new EdgeImpl(1l, 2l, 3, costs);
		graph.addEdge(e);

		costs = new int[] { 4, 4, 7, 6, 11 };

		e = new EdgeImpl(2l, 3l, 3, costs);

		graph.addEdge(e);

		costs = new int[] { 1, 10 };
		e = new EdgeImpl(3l, 4l, 3, costs);
		graph.addEdge(e);

		costs = new int[] { 2, 12, 13 };
		e = new EdgeImpl(3l, 5l, 4, costs);
		graph.addEdge(e);

		costs = new int[] { 3, 9, 10, 11 };
		e = new EdgeImpl(4l, 5l, 2, costs);
		graph.addEdge(e);

		costs = new int[] { 5, 2, 4, 6, 8, 15 };
		e = new EdgeImpl(5l, 6l, 1, costs);
		graph.addEdge(e);
		graph.createBounds();

		return graph;

	}

	public GraphBounds generateMonaco() {

		String osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		GraphBounds graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).execute();

		POIImporter.importPoIList(graph, "src/test/resources/monaco-latest.csv");

		CostGenerator.generateAllSyntheticEdgesCosts(graph);

		return graph;

	}

	public CHGraph generateMonacoCH() {
		String osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).executeCH();

		POIImporter.importPoIList(graph, "src/test/resources/monaco-latest.csv");

		System.out.println("#nodes: " + graph.getNumberOfNodes());
		System.out.println("#edges: " + graph.getNumberOfEdges());

		graph.createHyperPOIS();

		System.out.println("#nodes: " + graph.getNumberOfNodes());
		System.out.println("#edges: " + graph.getNumberOfEdges());

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public GraphBounds generateSeattle() throws NumberFormatException, IOException {

		String osmFile = this.getClass().getResource("/seattle.osm.pbf").getPath();
		String graphHopperSeattleDir = Configuration.USER_HOME + "/graphhopper/test/seattle";
		String graphastSeattleDir = Configuration.USER_HOME + "/graphast/test/seattle";

		GraphBounds graph = new OSMImporterImpl(osmFile, graphHopperSeattleDir, graphastSeattleDir).execute();
		// System.out.println("Importação de POIS iniciada!");
		// POIImporter.importPoIList(graph,
		// "src/test/resources/seattlepois.csv");
		// System.out.println("Importação de POIS finalizada!");
		System.out.println("Geração de custos aleatórios iniciada!");

		CostGenerator.generateAllSyntheticEdgesCosts(graph);
		System.out.println("Geração de custos aleatórios finalizada!");
		return graph;

	}

	public Graph generateExample3() {

		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/example3");

		NodeImpl v = new NodeImpl(3l, 10d, 10d, "label node 0");
		graph.addNode(v);

		v = new NodeImpl(4l, 43.7294668047756, 7.413772473047058);
		graph.addNode(v);

		int[] nodeCosts = new int[] { 1, 2, 3, 4 };
		v = new NodeImpl(2l, 10d, 30d, nodeCosts);
		graph.addNode(v);

		v = new NodeImpl(6l, 10d, 40d);
		graph.addNode(v);

		v = new NodeImpl(7l, 11d, 32d);
		graph.addNode(v);

		v = new NodeImpl(7, 11, 32, "Banco");
		graph.addNode(v);

		int[] costs = new int[] { 1, 2, 3, 4 };
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 20));
		Edge e = new EdgeImpl(0l, 1l, 10, costs, points, "rua1");
		graph.addEdge(e);

		costs = new int[] { 2, 4, 6, 8, 10 };
		points = new ArrayList<Point>();
		points.add(new Point(10, 20));
		points.add(new Point(10, 15));
		points.add(new Point(10, 10));
		e = new EdgeImpl(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);

		costs = new int[] { 2 };
		points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 30));
		e = new EdgeImpl(0l, 2l, 30, costs, points, "rua3");
		graph.addEdge(e);

		costs = new int[] { 2, 4, 6, 8, 10 };
		points = new ArrayList<Point>();
		points.add(new Point(10, 30));
		points.add(new Point(10, 10));
		e = new EdgeImpl(2l, 0l, 40, costs, points, "rua4");
		graph.addEdge(e);

		costs = new int[] { 3 };
		points = new ArrayList<Point>();
		points.add(new Point(10, 10));
		points.add(new Point(10, 40));
		e = new EdgeImpl(0l, 3l, 50, costs, points, "");
		graph.addEdge(e);

		e = new EdgeImpl(2l, 4l, 60);
		graph.addEdge(e);

		costs = new int[] { 5 };
		e = new EdgeImpl(3l, 0l, 70, costs);
		graph.addEdge(e);

		return graph;
	}

	public GraphBounds generateExample4() {
		GraphBounds graph = new GraphImpl(Configuration.USER_HOME + "/graphast/example");

		Edge e;
		NodeImpl v;

		v = new NodeImpl(0l, -3.74077, -38.55735, 0);
		graph.addNode(v);

		v = new NodeImpl(1l, -3.74003, -38.55693, 1);
		graph.addNode(v);

		v = new NodeImpl(2l, -3.74049, -38.5563, 2);
		graph.addNode(v);

		v = new NodeImpl(3l, -3.74035, -38.55526, 4);
		graph.addNode(v);

		v = new NodeImpl(4l, -3.73958, -38.55479, 0);
		graph.addNode(v);

		v = new NodeImpl(5l, -3.74001, -38.55415);
		graph.addNode(v);

		v = new NodeImpl(6l, -3.7412, -38.55388);
		graph.addNode(v);

		int[] costs = { 3, 1 };
		e = new EdgeImpl(0l, 1l, 1, costs, null, "Rua A");
		graph.addEdge(e);

		costs = new int[] { 5 };
		e = new EdgeImpl(0l, 2l, 1, costs, null, "Rua B");
		graph.addEdge(e);

		costs = new int[] { 3 };
		e = new EdgeImpl(1l, 2l, 1, costs, null, "Rua C");
		graph.addEdge(e);

		costs = new int[] { 3 };
		e = new EdgeImpl(2l, 3l, 1, costs, null, "Rua D");

		graph.addEdge(e);

		costs = new int[] { 3 };
		e = new EdgeImpl(3l, 4l, 1, costs, null, "Rua E");
		graph.addEdge(e);

		costs = new int[] { 6, 4 };
		e = new EdgeImpl(3l, 5l, 1, costs, null, "Rua F");
		graph.addEdge(e);

		costs = new int[] { 2 };
		e = new EdgeImpl(4l, 5l, 1, costs, null, "Rua G");
		graph.addEdge(e);

		costs = new int[] { 1 };
		e = new EdgeImpl(5l, 6l, 1, costs, null, "Rua H");
		graph.addEdge(e);

		graph.createBounds();
		return graph;
	}

	public GraphBounds generateExample5() {
		GraphBounds graph = new GraphImpl(Configuration.USER_HOME + "/graphast/example");

		Node n1 = new NodeImpl(60054510, 52.3515, 20.9447);
		Node n2 = new NodeImpl(249476123, 52.3499, 20.9461);

		graph.addNode(n1);
		graph.addNode(n2);

		graph.addEdge(new EdgeImpl(n1.getId(), n2.getId(), 112));
		graph.addEdge(new EdgeImpl(n2.getId(), n2.getId(), 0));
		graph.addEdge(new EdgeImpl(n2.getId(), n1.getId(), 195));
		return graph;
	}

	public GraphBounds generateExample6() {
		GraphBounds graph = new GraphImpl(Configuration.USER_HOME + "/graphast/example");
		Node n0 = new NodeImpl(60054477, 52.3524, 20.9440);
		Node n1 = new NodeImpl(60054510, 52.3515, 20.9447);
		Node n2 = new NodeImpl(249476123, 52.3499, 20.9461);
		Node n3 = new NodeImpl(252417127, 52.3509, 20.9452);
		Node n4 = new NodeImpl(252417130, 52.3514, 20.9449);

		graph.addNode(n0);
		graph.addNode(n1);
		graph.addNode(n2);
		graph.addNode(n3);
		graph.addNode(n4);

		graph.addEdge(new EdgeImpl(n4.getId(), n0.getId(), 121)); // 252417130
																	// 60054477
																	// 0.121
		graph.addEdge(new EdgeImpl(n0.getId(), n1.getId(), 112)); // 60054477
																	// 60054510
																	// 0.112
		graph.addEdge(new EdgeImpl(n3.getId(), n2.getId(), 123)); // 252417127
																	// 249476123
																	// 0.123
		graph.addEdge(new EdgeImpl(n2.getId(), n2.getId(), 0));
		graph.addEdge(new EdgeImpl(n1.getId(), n2.getId(), 195)); // 60054510
																	// 249476123
																	// 0.195
		return graph;
	}

	public GraphBounds generateExamplePoI() {

		GraphBounds graph = new GraphImpl(Configuration.USER_HOME + "/graphast/test/examplePoI");

		Node node;
		Edge edge;

		// NODES

		node = new NodeImpl(0l, 0.0d, 1.0d);
		graph.addNode(node);

		node = new NodeImpl(1l, 0.0d, 10.0d);
		int[] costs = new int[] { 30300000, 1500000, 1500000, 1500000, 900000, 900000, 59100000, 30300000 };
		node.setCategory(1);
		node.setLabel("Bradesco");
		node.setCosts(costs);
		graph.addNode(node);

		node = new NodeImpl(2l, 0.0d, 20.0d);
		graph.addNode(node);

		node = new NodeImpl(3l, 0.0d, 30.0d);
		graph.addNode(node);

		node = new NodeImpl(4l, 0.0d, 40.0d);
		costs = new int[] { 29700000, 900000, 900000, 900000, 1500000, 1500000, 58500000, 17700000 };
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
		costs = new int[] { 30300000, 1500000, 1500000, 1500000, 900000, 900000, 59100000, 30300000 };
		node.setCategory(3);
		node.setLabel("Escola Vila");
		node.setCosts(costs);
		graph.addNode(node);

		// TODO Create a constructor for edge without the distance

		// EDGES

		edge = new EdgeImpl(1l, 0l, 1l, 10);
		costs = new int[] { 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 1200000, 1200000,
				1200000, 900000, 900000, 900000, 1500000, 1500000, 1500000, 1500000, 900000, 900000, 900000, 900000,
				900000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(2l, 1l, 2l, 20);
		costs = new int[] { 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(3l, 1l, 7l, 30);
		costs = new int[] { 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000, 1320000,
				1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000, 720000, 720000, 720000,
				720000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(4l, 2l, 3l, 40);
		costs = new int[] { 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000,
				600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 600000,
				600000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(5l, 3l, 4l, 50);
		costs = new int[] { 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000, 1320000,
				1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000, 720000, 720000, 720000,
				720000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(6l, 4l, 8l, 60);
		costs = new int[] { 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000,
				720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000,
				720000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(7l, 4l, 9l, 70);
		costs = new int[] { 720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 1080000, 1080000,
				1080000, 600000, 600000, 600000, 1500000, 1500000, 1500000, 1500000, 600000, 600000, 600000, 600000,
				600000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(8l, 5l, 0l, 80);
		costs = new int[] { 720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 1080000, 1080000,
				1080000, 600000, 600000, 600000, 1080000, 1080000, 1080000, 1080000, 600000, 600000, 600000, 600000,
				600000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(9l, 6l, 5l, 90);
		costs = new int[] { 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 1200000, 1200000,
				1200000, 900000, 900000, 900000, 1500000, 1500000, 1500000, 1500000, 900000, 900000, 900000, 900000,
				900000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(10l, 7l, 2l, 100);
		costs = new int[] { 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(11l, 7l, 6l, 110);
		costs = new int[] { 720000, 720000, 600000, 600000, 600000, 600000, 600000, 600000, 600000, 1080000, 1080000,
				1080000, 600000, 600000, 600000, 1080000, 1080000, 1080000, 1080000, 600000, 600000, 600000, 600000,
				600000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(12l, 8l, 7l, 120);
		costs = new int[] { 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 720000, 1320000, 1320000,
				1320000, 720000, 720000, 720000, 1800000, 1800000, 1800000, 1800000, 720000, 720000, 720000, 720000,
				720000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		edge = new EdgeImpl(13l, 9l, 8l, 130);
		costs = new int[] { 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000, 900000,
				900000 };
		edge.setCosts(costs);
		graph.addEdge(edge);

		graph.createBounds();
		graph.save();
		return graph;

	}

	public CHGraph generateAndorra() {

		String osmFile = DijkstraConstantWeight.class.getResource("/andorra-150305.osm.pbf").getPath();
		String graphHopperAndorraDir = Configuration.USER_HOME + "/graphhopper/test/andorra";
		String graphastAndorraDir = Configuration.USER_HOME + "/graphast/test/andorra";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperAndorraDir, graphastAndorraDir).executeCH();

		return graph;

	}
	
	/*
	 * Based on the createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class.
	 */
	public CHGraph generateGraphHopperExample() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 20, 0);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 10, 0);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 0, 20);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 3, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 0l, 3, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 2, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 2, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 2, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 2, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 1l, 2, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 5l, 2, 1, "Edge 13");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}
	
	/*
	 * This test uses a modified version of createExampleGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	public CHGraph generateGraphHopperExampleWithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExampleWithPoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10, 1);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 20, 0);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 10, 0);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 0, 20, 1);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 3, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 0l, 3, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 2, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 2, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 2, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 2, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 1l, 2, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 5l, 2, 1, "Edge 13");
		graph.addEdge(edge);

		graph.createHyperPOIS();
		
		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}
	
	/*
	 * Based on the testDirectedGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class.
	 */
		public CHGraph generateGraphHopperExample2() {

			CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample2");

			CHEdge edge;
			CHNode node;

			node = new CHNodeImpl(0l, 30, 10);
			graph.addNode(node);

			node = new CHNodeImpl(1l, 0, 5);
			graph.addNode(node);

			node = new CHNodeImpl(2l, 20, 10);
			graph.addNode(node);

			node = new CHNodeImpl(3l, 10, 10);
			graph.addNode(node);

			// TODO Create a constructor without the originalEdgeCounter
			edge = new CHEdgeImpl(3l, 2l, 3, 1, "Edge 0");
			graph.addEdge(edge);
			
			edge = new CHEdgeImpl(2l, 3l, 10, 1, "Edge 1");
			graph.addEdge(edge);
			
			edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 2");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(3l, 0l, 1, 1, "Edge 3");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(1l, 3l, 1, 1, "Edge 4");
			graph.addEdge(edge);
			
			edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 5");
			graph.addEdge(edge);

			graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
			graph.setMaxLevel((int) graph.getNumberOfNodes());

			graph.save();

			return graph;

		}

		/*
		 * This test uses a modified version of testDirectedGraph() graph from GraphHopper
		 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
		 * HyperEdges.
		 */
		public CHGraph generateGraphHopperExample2WithPoIs() {

			CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample2WithPoIs");

			CHEdge edge;
			CHNode node;

			node = new CHNodeImpl(0l, 30, 10, 1);
			graph.addNode(node);

			node = new CHNodeImpl(1l, 0, 5);
			graph.addNode(node);

			node = new CHNodeImpl(2l, 20, 10, 1);
			graph.addNode(node);

			node = new CHNodeImpl(3l, 10, 10);
			graph.addNode(node);

			// TODO Create a constructor without the originalEdgeCounter
			edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 0");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(3l, 0l, 1, 1, "Edge 1");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 2");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(1l, 3l, 1, 1, "Edge 3");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(2l, 3l, 10, 1, "Edge 4");
			graph.addEdge(edge);

			edge = new CHEdgeImpl(3l, 2l, 3, 1, "Edge 5");
			graph.addEdge(edge);

			graph.createHyperPOIS();

			graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
			graph.setMaxLevel((int) graph.getNumberOfNodes());

			graph.save();

			return graph;

		}
	
		/*
		 * Based on the testDirectedGraph3() graph from GraphHopper
		 * PrepareContractionHierarchiesTest.java class.
		 */
	public CHGraph generateGraphHopperExample3() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample3");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 0, 20);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 5, 30);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 15, 30);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 30, 20);
		graph.addNode(node);

		node = new CHNodeImpl(9l, 30, 10);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 2l, 2, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 2, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 9l, 2, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 2l, 2, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 8l, 2, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 2l, 2, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 2, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 2, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 10, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 2, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 1l, 2, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 2, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 2, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 5l, 2, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 3l, 2, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 2, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 3l, 2, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 7l, 2, 1, "Edge 17");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 3l, 2, 1, "Edge 18");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of testDirectedGraph3() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	public CHGraph generateGraphHopperExample3WithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExampleWith3PoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 0, 20);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 5, 30, 1);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 15, 30, 1);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 30, 20);
		graph.addNode(node);

		node = new CHNodeImpl(9l, 30, 10);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 2l, 2, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 2, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 9l, 2, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 2l, 2, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 8l, 2, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 2l, 2, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 2, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 2, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 10, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 2, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 1l, 2, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 2, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 2, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 5l, 2, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 3l, 2, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 2, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 3l, 2, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 7l, 2, 1, "Edge 17");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 3l, 2, 1, "Edge 18");
		graph.addEdge(edge);

		graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;

	}

	/*
	 * Based on the initRoundaboutGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class.
	 */
	public CHGraph generateGraphHopperExample4() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample4");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 30, 10);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 40, 10);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 50, 0);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 60, 0);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 70, 10);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 80, 10);
		graph.addNode(node);

		node = new CHNodeImpl(9l, 20, 30);
		graph.addNode(node);

		node = new CHNodeImpl(10l, 30, 30);
		graph.addNode(node);

		node = new CHNodeImpl(11l, 40, 30);
		graph.addNode(node);

		node = new CHNodeImpl(12l, 50, 20);
		graph.addNode(node);

		node = new CHNodeImpl(13l, 60, 20);
		graph.addNode(node);

		node = new CHNodeImpl(14l, 10, 0);
		graph.addNode(node);

		node = new CHNodeImpl(15l, -10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(16l, -10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(17l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(18l, 10, -10);
		graph.addNode(node);

		node = new CHNodeImpl(19l, 10, -20);
		graph.addNode(node);

		node = new CHNodeImpl(20l, 0, -10);
		graph.addNode(node);

		node = new CHNodeImpl(21l, 0, -20);
		graph.addNode(node);

		node = new CHNodeImpl(22l, 80, 0);
		graph.addNode(node);

		node = new CHNodeImpl(23l, 80, -10);
		graph.addNode(node);

		node = new CHNodeImpl(24l, 70, -10);
		graph.addNode(node);

		node = new CHNodeImpl(25l, 50, -10);
		graph.addNode(node);

		node = new CHNodeImpl(26l, 40, -30);
		graph.addNode(node);

		node = new CHNodeImpl(27l, 40, -10);
		graph.addNode(node);

		node = new CHNodeImpl(28l, 40, 50);
		graph.addNode(node);

		node = new CHNodeImpl(29l, 50, 50);
		graph.addNode(node);

		node = new CHNodeImpl(30l, 50, 40);
		graph.addNode(node);

		node = new CHNodeImpl(31l, 50, 30);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(16l, 0l, 1, 1, "Edge 0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 16l, 1, 1, "Edge 1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 9l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 17l, 1, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 0l, 1, 1, "Edge 5");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(9l, 10l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 9l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 11l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 10l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 28l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 11l, 1, 1, "Edge 11");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(28l, 29l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 28l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 30l, 1, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 29l, 1, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 31l, 1, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(31l, 30l, 1, 1, "Edge 17");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(31l, 4l, 1, 1, "Edge 18");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 31l, 1, 1, "Edge 19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 1l, 1, 1, "Edge 20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 17l, 1, 1, "Edge 21");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(15l, 1l, 1, 1, "Edge 22");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 15l, 1, 1, "Edge 23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 1l, 1, 1, "Edge 24");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 14l, 1, 1, "Edge 25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 18l, 1, 1, "Edge 26");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(18l, 14l, 1, 1, "Edge 27");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(18l, 19l, 1, 1, "Edge 28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 18l, 1, 1, "Edge 29");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 20l, 1, 1, "Edge 30");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(20l, 19l, 1, 1, "Edge 31");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(20l, 15l, 1, 1, "Edge 32");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(15l, 20l, 1, 1, "Edge 33");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 21l, 1, 1, "Edge 34");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(21l, 19l, 1, 1, "Edge 35");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(21l, 16l, 1, 1, "Edge 36");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 21l, 1, 1, "Edge 37");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 38");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 39");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 40");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 41");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 42");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 43");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 5l, 1, 1, "Edge 44");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 45");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(6l, 7l, 1, 1, "Edge 46");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(7l, 13l, 1, 1, "Edge 47");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 12l, 1, 1, "Edge 48");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(12l, 4l, 1, 1, "Edge 49");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 8l, 1, 1, "Edge 50");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 7l, 1, 1, "Edge 51");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(8l, 22l, 1, 1, "Edge 52");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(22l, 8l, 1, 1, "Edge 53");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(22l, 23l, 1, 1, "Edge 54");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(23l, 22l, 1, 1, "Edge 55");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(23l, 24l, 1, 1, "Edge 56");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 23l, 1, 1, "Edge 57");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(24l, 25l, 1, 1, "Edge 58");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 24l, 1, 1, "Edge 59");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 27l, 1, 1, "Edge 60");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(27l, 25l, 1, 1, "Edge 61");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(27l, 5l, 1, 1, "Edge 62");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 27l, 1, 1, "Edge 63");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 26l, 1, 1, "Edge 64");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(26l, 25l, 1, 1, "Edge 65");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of initRoundaboutGraph() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	public CHGraph generateGraphHopperExample4WithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample4WithPoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 30, 10);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 40, 10, 1);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 50, 0);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 60, 0);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 70, 10);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 80, 10);
		graph.addNode(node);

		node = new CHNodeImpl(9l, 20, 30);
		graph.addNode(node);

		node = new CHNodeImpl(10l, 30, 30);
		graph.addNode(node);

		node = new CHNodeImpl(11l, 40, 30);
		graph.addNode(node);

		node = new CHNodeImpl(12l, 50, 20);
		graph.addNode(node);

		node = new CHNodeImpl(13l, 60, 20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(14l, 10, 0);
		graph.addNode(node);

		node = new CHNodeImpl(15l, -10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(16l, -10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(17l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(18l, 10, -10);
		graph.addNode(node);

		node = new CHNodeImpl(19l, 10, -20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(20l, 0, -10);
		graph.addNode(node);

		node = new CHNodeImpl(21l, 0, -20);
		graph.addNode(node);

		node = new CHNodeImpl(22l, 80, 0);
		graph.addNode(node);

		node = new CHNodeImpl(23l, 80, -10);
		graph.addNode(node);

		node = new CHNodeImpl(24l, 70, -10);
		graph.addNode(node);

		node = new CHNodeImpl(25l, 50, -10);
		graph.addNode(node);

		node = new CHNodeImpl(26l, 40, -30, 1);
		graph.addNode(node);

		node = new CHNodeImpl(27l, 40, -10);
		graph.addNode(node);

		node = new CHNodeImpl(28l, 40, 50);
		graph.addNode(node);

		node = new CHNodeImpl(29l, 50, 50);
		graph.addNode(node);

		node = new CHNodeImpl(30l, 50, 40);
		graph.addNode(node);

		node = new CHNodeImpl(31l, 50, 30);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 16l, 1, 1, "Edge 0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 9l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 17l, 1, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 0l, 1, 1, "Edge 5");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(9l, 10l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 9l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 11l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 10l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 28l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 11l, 1, 1, "Edge 11");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(28l, 29l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 28l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 30l, 1, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 29l, 1, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 31l, 1, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(31l, 30l, 1, 1, "Edge 17");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(31l, 4l, 1, 1, "Edge 18");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 31l, 1, 1, "Edge 19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 1l, 1, 1, "Edge 20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 17l, 1, 1, "Edge 21");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(15l, 1l, 1, 1, "Edge 22");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 15l, 1, 1, "Edge 23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 1l, 1, 1, "Edge 24");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 14l, 1, 1, "Edge 25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 18l, 1, 1, "Edge 26");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(18l, 14l, 1, 1, "Edge 27");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(18l, 19l, 1, 1, "Edge 28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 18l, 1, 1, "Edge 29");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 20l, 1, 1, "Edge 30");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(20l, 19l, 1, 1, "Edge 31");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(20l, 15l, 1, 1, "Edge 32");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(15l, 20l, 1, 1, "Edge 33");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 21l, 1, 1, "Edge 34");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(21l, 19l, 1, 1, "Edge 35");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(21l, 16l, 1, 1, "Edge 36");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 21l, 1, 1, "Edge 37");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 38");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 39");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 40");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 41");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 42");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 43");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 6l, 1, 1, "Edge 44");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 45");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(6l, 7l, 1, 1, "Edge 46");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(7l, 13l, 1, 1, "Edge 47");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 12l, 1, 1, "Edge 48");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(12l, 4l, 1, 1, "Edge 49");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 8l, 1, 1, "Edge 50");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 7l, 1, 1, "Edge 51");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(8l, 22l, 1, 1, "Edge 52");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(22l, 8l, 1, 1, "Edge 53");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(22l, 23l, 1, 1, "Edge 54");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(23l, 22l, 1, 1, "Edge 55");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(23l, 24l, 1, 1, "Edge 56");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 23l, 1, 1, "Edge 57");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(24l, 25l, 1, 1, "Edge 58");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 24l, 1, 1, "Edge 59");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 27l, 1, 1, "Edge 60");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(27l, 25l, 1, 1, "Edge 61");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(27l, 5l, 1, 1, "Edge 62");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 27l, 1, 1, "Edge 63");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 26l, 1, 1, "Edge 64");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(26l, 25l, 1, 1, "Edge 65");
		graph.addEdge(edge);
		
		graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes() + 1);

		graph.save();

		return graph;

	}

	public CHGraph generateGraphHopperTest() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperTest");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 40, 10, 1);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 70, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 60, 20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 10, -20, 1);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter		

		edge = new CHEdgeImpl(4l, 0l, 1, 1, "Edge 20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 1, 1, "Edge 21");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 38");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 39");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 40");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 40"); //
//		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 44");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 44");//
//		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 1l, 1, 1, "Edge 45");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(1l, 3l, 1, 1, "Edge 45");//
//		graph.addEdge(edge);
		
		graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes() + 1);

		graph.save();

		return graph;
	}
	
	public CHGraph generateGraphHopperTest2() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperTest2");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 30);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 30, 10);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 40, 10);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 50, 0);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 60, 0);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 70, 10);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 80, 10);
		graph.addNode(node);

		node = new CHNodeImpl(9l, 20, 30);
		graph.addNode(node);

		node = new CHNodeImpl(10l, 30, 30);
		graph.addNode(node);

		node = new CHNodeImpl(11l, 40, 30);
		graph.addNode(node);

		node = new CHNodeImpl(12l, 50, 20);
		graph.addNode(node);

		node = new CHNodeImpl(13l, 60, 20);
		graph.addNode(node);

		node = new CHNodeImpl(14l, 10, 0);
		graph.addNode(node);

		node = new CHNodeImpl(15l, -10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(16l, -10, 30, 1);
		graph.addNode(node);

		node = new CHNodeImpl(17l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(18l, 10, -10);
		graph.addNode(node);

		node = new CHNodeImpl(19l, 10, -20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(20l, 0, -10);
		graph.addNode(node);

		node = new CHNodeImpl(21l, 0, -20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(22l, 80, 0);
		graph.addNode(node);

		node = new CHNodeImpl(23l, 80, -10);
		graph.addNode(node);

		node = new CHNodeImpl(24l, 70, -10);
		graph.addNode(node);

		node = new CHNodeImpl(25l, 50, -10);
		graph.addNode(node);

		node = new CHNodeImpl(26l, 40, -30);
		graph.addNode(node);

		node = new CHNodeImpl(27l, 40, -10);
		graph.addNode(node);

		node = new CHNodeImpl(28l, 40, 50);
		graph.addNode(node);

		node = new CHNodeImpl(29l, 50, 50);
		graph.addNode(node);

		node = new CHNodeImpl(30l, 50, 40);
		graph.addNode(node);

		node = new CHNodeImpl(31l, 50, 30);
		graph.addNode(node);
		
		node = new CHNodeImpl(32l, -10, -10);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 16l, 1, 1, "Edge 0");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(16l, 0l, 1, 1, "Edge 1");
//		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 9l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 17l, 1, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 0l, 1, 1, "Edge 5");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(9l, 10l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 9l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 11l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 10l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 28l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 11l, 1, 1, "Edge 11");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(28l, 29l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 28l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 30l, 1, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 29l, 1, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 31l, 1, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(31l, 30l, 1, 1, "Edge 17");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(31l, 4l, 1, 1, "Edge 18");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 31l, 1, 1, "Edge 19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 1l, 1, 1, "Edge 20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 17l, 1, 1, "Edge 21");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(15l, 1l, 1, 1, "Edge 22");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 15l, 1, 1, "Edge 23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 1l, 1, 1, "Edge 24");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 14l, 1, 1, "Edge 25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 18l, 1, 1, "Edge 26");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(18l, 14l, 1, 1, "Edge 27");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(18l, 19l, 1, 1, "Edge 28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 18l, 1, 1, "Edge 29");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 20l, 1, 1, "Edge 30");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(20l, 19l, 1, 1, "Edge 31");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(20l, 15l, 1, 1, "Edge 32");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(15l, 20l, 1, 1, "Edge 33");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(19l, 21l, 1, 1, "Edge 34");
//		graph.addEdge(edge);

		edge = new CHEdgeImpl(21l, 19l, 1, 1, "Edge 35");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 38");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 39");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 40");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 41");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 42");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 43");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 6l, 1, 1, "Edge 44");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 45");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(6l, 7l, 1, 1, "Edge 46");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(7l, 13l, 1, 1, "Edge 47");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 12l, 1, 1, "Edge 48");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(12l, 4l, 1, 1, "Edge 49");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 8l, 1, 1, "Edge 50");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 7l, 1, 1, "Edge 51");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(8l, 22l, 1, 1, "Edge 52");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(22l, 8l, 1, 1, "Edge 53");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(22l, 23l, 1, 1, "Edge 54");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(23l, 22l, 1, 1, "Edge 55");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(23l, 24l, 1, 1, "Edge 56");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 23l, 1, 1, "Edge 57");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(24l, 25l, 1, 1, "Edge 58");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 24l, 1, 1, "Edge 59");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 27l, 1, 1, "Edge 60");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(27l, 25l, 1, 1, "Edge 61");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(27l, 5l, 1, 1, "Edge 62");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 27l, 1, 1, "Edge 63");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 26l, 1, 1, "Edge 64");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(26l, 25l, 1, 1, "Edge 65");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(32l, 16l, 1, 1, "Edge 65");
//		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 32l, 1, 1, "Edge 65");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(32l, 21l, 1, 1, "Edge 65");
		graph.addEdge(edge);
		
//		edge = new CHEdgeImpl(21l, 32l, 1, 1, "Edge 65");
//		graph.addEdge(edge);
		
		graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes() + 1);

		graph.save();

		return graph;
	}
	
	/*
	 * Based on the testCircleBug() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class.
	 */
	public CHGraph generateGraphHopperExample5() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample5");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 20, 0);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 10, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 10, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 1l, 4, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 4, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 10, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 10, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 3l, 10, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 0l, 10, 1, "Edge 7");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of testCircleBug() graph from GraphHopper
	 * PrepareContractionHierarchiesTest.java class. This modified version have HyperPoI's
	 * HyperEdges.
	 */
	public CHGraph generateGraphHopperExample5WithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample5WithPoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 20, 0);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 10, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 10, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 1l, 4, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 4, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 10, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 10, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 3l, 10, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 0l, 10, 1, "Edge 7");
		graph.addEdge(edge);

		graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}
	
	
	
	
	

	
	
	
	
	public CHGraph generateGraphHopperExample40() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample40");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 10, -20);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 0, -20);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 0, 20);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 10, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 0l, 3, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 5l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 1l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 10, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 3, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 2l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}

	public CHGraph generateGraphHopperExample40WithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample40WithPoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 10, -20, 1);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 0, -20);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 0, 20, 1);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 3, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 0l, 3, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 2, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 2, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 5l, 2, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 1l, 2, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 2, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 2l, 2, 1, "Edge 13");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.createHyperPOIS();

		graph.save();

		return graph;

	}

	

	public CHGraph generateGraphHopperExample6() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample6");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 30, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 40, 10);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 50, 10);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 20, 20);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 5l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 3l, 1, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 5l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.save();

		return graph;

	}

	public CHGraph generateGraphHopperExample6WithPoIs() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphHopperExample6WithPoIs");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 10);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 30, 10);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 40, 10);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 50, 10, 1);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 10, 20);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 20, 20, 1);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 1l, 1, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 0l, 1, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 5l, 1, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 0l, 1, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 1, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 3l, 1, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 5l, 1, 1, "Edge 13");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		graph.setMaxLevel((int) graph.getNumberOfNodes());

		graph.createHyperPOIS();

		graph.save();

		return graph;

	}

}