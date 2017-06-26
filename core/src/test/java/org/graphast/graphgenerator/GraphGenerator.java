package org.graphast.graphgenerator;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.graphast.util.FileUtils;
import org.graphast.util.NumberUtils;
import org.graphhopper.config.Config;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.ch.PrepareContractionHierarchies;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.ShortestWeighting;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.LevelGraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Helper;
import com.graphhopper.util.PointList;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

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

		String osmFile = this.getClass().getResource("/monaco-latest.osm.pbf").getPath();
		String poiFile = this.getClass().getResource("/monaco-latest.csv").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		GraphBounds graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).execute();

		POIImporter.importPoIList(graph, poiFile);

		CostGenerator.generateAllSyntheticEdgesCosts(graph);

		return graph;

	}

	public CHGraph generateMonacoCH() {
		String osmFile = this.getClass().getResource("/monaco-latest.osm.pbf").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");
		//
		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());
		//
		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public CHGraph generateTinyMonacoCH() {
		String osmFile = this.getClass().getResource("/tiny-monaco.osm.pbf").getPath();
		String graphHopperTinyMonacoDir = Configuration.USER_HOME + "/graphhopper/test/tiny-monaco";
		String graphastTinyMonacoDir = Configuration.USER_HOME + "/graphast/test/tiny-monaco";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperTinyMonacoDir, graphastTinyMonacoDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");
		//
		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());
		//
		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	// generateToyGraphhopperGraph1
	public CHGraph generateContractedGraphhopperExample1() {

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager);
		LevelGraphStorage graphStorage = gb.levelGraphCreate();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 0, 2);
		na.setNode(1, 0, 1);
		na.setNode(2, 0, 4);
		na.setNode(3, 0, 3);
		na.setNode(4, 0, 5);
		na.setNode(5, 1, 1);

		graphStorage.edge(0, 1, 1, false);
		graphStorage.edge(1, 0, 1, false);
		graphStorage.edge(0, 2, 1, false);
		graphStorage.edge(2, 0, 1, false);
		graphStorage.edge(0, 4, 3, false);
		graphStorage.edge(4, 0, 3, false);
		graphStorage.edge(1, 2, 2, false);
		graphStorage.edge(2, 1, 2, false);
		graphStorage.edge(2, 3, 1, false);
		graphStorage.edge(3, 2, 1, false);
		graphStorage.edge(4, 3, 2, false);
		graphStorage.edge(3, 4, 2, false);
		graphStorage.edge(5, 1, 2, false);
		graphStorage.edge(1, 5, 2, false);

		// this.printGraphhopperGraph(graphStorage.getAllEdges());

		PrepareContractionHierarchies pch = new PrepareContractionHierarchies(encodingManager.getEncoder("car"),
				new ShortestWeighting());
		pch.setGraph(graphStorage);
		pch.doWork();

		// this.printGraphhopperGraph(graphStorage.getAllEdges());

		GraphHopper gh = new GraphHopper();
		gh.loadGraph((LevelGraphStorage) pch.getG());

		// TODO Change the location and use config.properties
		String graphHopperTestDir = Configuration.USER_HOME + "/graphhopper/test/contractedGraphhopperExample1";
		String graphastTestDir = Configuration.USER_HOME + "/graphast/test/contractedGraphhopperExample1";

		// Object that will perform the importation from graphHopper to Graphast
		OSMImporterImpl osmImporter = new OSMImporterImpl();
		osmImporter.setGraphastDir(graphastTestDir);
		osmImporter.setGraphHopperDir(graphHopperTestDir);

		CHGraph graph = osmImporter.executeCH(gh);

		graph.save();
		return graph;
	}

	// generateToyGraphhopperGraph1
	public CHGraph generateContractedGraphhopperExample4() {

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager);
		LevelGraphStorage graphStorage = gb.levelGraphCreate();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 0, 2);
		na.setNode(1, 0, 1);
		na.setNode(2, 0, 4);
		na.setNode(3, 0, 3);
		na.setNode(4, 0, 5);
		na.setNode(5, 1, 1);
		na.setNode(6, 0, 2);
		na.setNode(7, 0, 1);
		na.setNode(8, 0, 4);
		na.setNode(9, 0, 3);
		na.setNode(10, 0, 5);
		na.setNode(11, 0, 2);
		na.setNode(12, 0, 1);
		na.setNode(13, 0, 4);
		na.setNode(14, 0, 3);
		na.setNode(15, 0, 5);
		na.setNode(16, 1, 1);
		na.setNode(17, 0, 2);
		na.setNode(18, 0, 1);
		na.setNode(19, 0, 4);
		na.setNode(20, 0, 3);
		na.setNode(21, 0, 5);
		na.setNode(22, 0, 2);
		na.setNode(23, 0, 1);
		na.setNode(24, 0, 4);
		na.setNode(25, 0, 3);
		na.setNode(26, 0, 5);
		na.setNode(27, 1, 1);
		na.setNode(28, 0, 2);
		na.setNode(29, 0, 1);
		na.setNode(30, 0, 4);
		na.setNode(31, 0, 3);

		graphStorage.edge(16, 0, 1, false);
		graphStorage.edge(0, 16, 1, false);
		graphStorage.edge(0, 9, 1, false);
		graphStorage.edge(9, 0, 1, false);
		graphStorage.edge(0, 17, 1, false);
		graphStorage.edge(17, 0, 1, false);
		graphStorage.edge(9, 10, 1, false);
		graphStorage.edge(10, 9, 1, false);
		graphStorage.edge(10, 11, 1, false);
		graphStorage.edge(11, 10, 1, false);
		graphStorage.edge(11, 28, 1, false);
		graphStorage.edge(28, 11, 1, false);
		graphStorage.edge(28, 29, 1, false);
		graphStorage.edge(29, 28, 1, false);
		graphStorage.edge(29, 30, 1, false);
		graphStorage.edge(30, 29, 1, false);
		graphStorage.edge(30, 31, 1, false);
		graphStorage.edge(31, 30, 1, false);
		graphStorage.edge(31, 4, 1, false);
		graphStorage.edge(4, 31, 1, false);

		graphStorage.edge(17, 1, 1, false);
		graphStorage.edge(1, 17, 1, false);
		graphStorage.edge(15, 1, 1, false);
		graphStorage.edge(1, 15, 1, false);
		graphStorage.edge(14, 1, 1, false);
		graphStorage.edge(1, 14, 1, false);
		graphStorage.edge(14, 18, 1, false);
		graphStorage.edge(18, 14, 1, false);
		graphStorage.edge(18, 19, 1, false);
		graphStorage.edge(19, 18, 1, false);
		graphStorage.edge(19, 20, 1, false);
		graphStorage.edge(20, 19, 1, false);
		graphStorage.edge(20, 15, 1, false);
		graphStorage.edge(15, 20, 1, false);
		graphStorage.edge(19, 21, 1, false);
		graphStorage.edge(21, 19, 1, false);
		graphStorage.edge(21, 16, 1, false);
		graphStorage.edge(16, 21, 1, false);
		graphStorage.edge(1, 2, 1, false);
		graphStorage.edge(2, 1, 1, false);
		graphStorage.edge(2, 3, 1, false);
		graphStorage.edge(3, 2, 1, false);
		graphStorage.edge(3, 4, 1, false);
		graphStorage.edge(4, 3, 1, false);

		graphStorage.edge(4, 5, 1, false);
		graphStorage.edge(5, 6, 1, false);
		graphStorage.edge(6, 7, 1, false);
		graphStorage.edge(7, 13, 1, false);
		graphStorage.edge(13, 12, 1, false);
		graphStorage.edge(12, 4, 1, false);

		graphStorage.edge(7, 8, 1, false);
		graphStorage.edge(8, 7, 1, false);
		graphStorage.edge(8, 22, 1, false);
		graphStorage.edge(22, 8, 1, false);
		graphStorage.edge(22, 23, 1, false);
		graphStorage.edge(23, 22, 1, false);
		graphStorage.edge(23, 24, 1, false);
		graphStorage.edge(24, 23, 1, false);
		graphStorage.edge(24, 25, 1, false);
		graphStorage.edge(25, 24, 1, false);
		graphStorage.edge(25, 27, 1, false);
		graphStorage.edge(27, 25, 1, false);
		graphStorage.edge(27, 5, 1, false);
		graphStorage.edge(5, 27, 1, false);
		graphStorage.edge(25, 26, 1, false);
		graphStorage.edge(26, 25, 1, false);

		// this.printGraphhopperGraph(graphStorage.getAllEdges());

		PrepareContractionHierarchies pch = new PrepareContractionHierarchies(encodingManager.getEncoder("car"),
				new ShortestWeighting());
		pch.setGraph(graphStorage);
		pch.doWork();

		// this.printGraphhopperGraph(graphStorage.getAllEdges());

		GraphHopper gh = new GraphHopper();
		gh.loadGraph((LevelGraphStorage) pch.getG());

		// TODO Change the location and use config.properties
		String graphHopperTestDir = Configuration.USER_HOME + "/graphhopper/test/contractedGraphhopperExample4";
		String graphastTestDir = Configuration.USER_HOME + "/graphast/test/contractedGraphhopperExample4";

		// Object that will perform the importation from graphHopper to Graphast
		OSMImporterImpl osmImporter = new OSMImporterImpl();
		osmImporter.setGraphastDir(graphastTestDir);
		osmImporter.setGraphHopperDir(graphHopperTestDir);

		CHGraph graph = osmImporter.executeCH(gh);

		graph.save();
		return graph;
	}

	public CHGraph generateGraphhopperExample1() {

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager);
		LevelGraphStorage graphStorage = gb.levelGraphCreate();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 0, 2);
		na.setNode(1, 0, 1);
		na.setNode(2, 0, 4);
		na.setNode(3, 0, 3);
		na.setNode(4, 0, 5);
		na.setNode(5, 1, 1);

		graphStorage.edge(0, 1, 1, false);
		graphStorage.edge(1, 0, 1, false);
		graphStorage.edge(0, 2, 1, false);
		graphStorage.edge(2, 0, 1, false);
		graphStorage.edge(0, 4, 3, false);
		graphStorage.edge(4, 0, 3, false);
		graphStorage.edge(1, 2, 2, false);
		graphStorage.edge(2, 1, 2, false);
		graphStorage.edge(2, 3, 1, false);
		graphStorage.edge(3, 2, 1, false);
		graphStorage.edge(4, 3, 2, false);
		graphStorage.edge(3, 4, 2, false);
		graphStorage.edge(5, 1, 2, false);
		graphStorage.edge(1, 5, 2, false);

		// this.printGraphhopperGraph(graphStorage.getAllEdges());

		GraphHopper gh = new GraphHopper();
		gh.loadGraph(graphStorage);

		// TODO Change the location and use config.properties
		String graphHopperTestDir = Configuration.USER_HOME + "/graphhopper/test/graphhopperExample1";
		String graphastTestDir = Configuration.USER_HOME + "/graphast/test/graphhopperExample1";

		// Object that will perform the importation from graphHopper to Graphast
		OSMImporterImpl osmImporter = new OSMImporterImpl();
		osmImporter.setGraphastDir(graphastTestDir);
		osmImporter.setGraphHopperDir(graphHopperTestDir);

		CHGraph graph = osmImporter.executeCH(gh);

		graph.save();
		return graph;
	}

	public CHGraph generateMonacoCHWithPoI() {
		String osmFile = this.getClass().getResource("/monaco-latest.osm.pbf").getPath();
		String graphHopperMonacoDir = Configuration.USER_HOME + "/graphhopper/test/monaco";
		String graphastMonacoDir = Configuration.USER_HOME + "/graphast/test/monaco";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperMonacoDir, graphastMonacoDir).executeCH();

//		POIImporter.importPoIList(graph, "src/test/resources/monaco-latest.csv");

		graph.save();

		return graph;
	}
	
	public CHGraph generateAndorra() {
		String osmFile = Configuration.USER_HOME + "/graphast/test/andorra-latest.osm.pbf";
		String graphHopperAndorraDir = Configuration.USER_HOME + "/graphhopper/test/andorra";
		String graphastAndorraDir = Configuration.USER_HOME + "/graphast/test/andorra";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperAndorraDir, graphastAndorraDir).executeCH();

		graph.save();

		return graph;
	}

	public CHGraph generateBerlinCH() {
		String osmFile = Configuration.USER_HOME + "/graphast/test/berlin-latest.osm.pbf";
		String graphHopperBerlinDir = Configuration.USER_HOME + "/graphhopper/test/berlin";
		String graphastBerlinDir = Configuration.USER_HOME + "/graphast/test/berlin";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperBerlinDir, graphastBerlinDir).executeCH();

//		POIImporter.importPoIList(graph, "src/test/resources/monaco-latest.csv");

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());
		//
		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}
	
	public CHGraph generateSeattleCH() {
		String osmFile = this.getClass().getResource("/seattle-latest.osm.pbf").getPath();
		String graphHopperSeattleDir = Configuration.USER_HOME + "/graphhopper/test/seattle";
		String graphastSeattleDir = Configuration.USER_HOME + "/graphast/test/seattle";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperSeattleDir, graphastSeattleDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public CHGraph generateGreeceCH() {
		String osmFile = Configuration.USER_HOME + "/graphast/test/greece-latest.osm.pbf";
		String graphHopperGreeceDir = Configuration.USER_HOME + "/graphhopper/test/greece";
		String graphastGreeceDir = Configuration.USER_HOME + "/graphast/test/greece";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperGreeceDir, graphastGreeceDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public CHGraph generateGermanyCH() {
		String osmFile = this.getClass().getResource("/germany-latest.osm.pbf").getPath();
		String graphHopperGermanyDir = Configuration.USER_HOME + "/graphhopper/test/germany";
		String graphastGermanyDir = Configuration.USER_HOME + "/graphast/test/germany";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperGermanyDir, graphastGermanyDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");
		//
		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());
		//
		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public CHGraph generateSpainCH() {
		String osmFile = this.getClass().getResource("/spain-latest.osm.pbf").getPath();
		String graphHopperSpainDir = Configuration.USER_HOME + "/graphhopper/test/spain";
		String graphastSpainDir = Configuration.USER_HOME + "/graphast/test/spain";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperSpainDir, graphastSpainDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");
		//
		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());
		//
		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

		graph.save();

		return graph;
	}

	public CHGraph generateTokyoBiggerCH() {
		String osmFile = this.getClass().getResource("/tokyoBigger.osm.pbf").getPath();
		String graphHopperTokyoBiggerDir = Configuration.USER_HOME + "/graphhopper/test/tokyoBigger";
		String graphastTokyoBiggerDir = Configuration.USER_HOME + "/graphast/test/tokyoBigger";

		CHGraph graph = new OSMImporterImpl(osmFile, graphHopperTokyoBiggerDir, graphastTokyoBiggerDir).executeCH();

		// POIImporter.importPoIList(graph,
		// "src/test/resources/monaco-latest.csv");

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.createHyperPOIS();

		// System.out.println("#nodes: " + graph.getNumberOfNodes());
		// System.out.println("#edges: " + graph.getNumberOfEdges());

		// graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());
		// graph.setMaxLevel((int) (graph.getNumberOfNodes() + 1));

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

	/**
	 * Method to generate a graph based on the MIT example found here:
	 * http://goo.gl/IlFPbo
	 * 
	 * (Lecture 18: Shortest Paths IV - Speeding up Dijkstra)
	 * 
	 * @return graph The graph generated
	 */
	public CHGraph generateMITExample() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphMITExample");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 30, 0);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 40, 20);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 50, 10);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 3, 1, "Edge 0 to 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 5, 1, "Edge 0 to 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 3, 1, "Edge 1 to 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 4l, 5, 1, "Edge 2 to 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 3, 1, "Edge 3 to 4");
		graph.addEdge(edge);

		graph.save();

		return graph;

	}

	/**
	 * Method to generate a graph based on a second MIT example.
	 * 
	 * @return graph The graph generated
	 */
	public CHGraph generateMITExample2() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphMITExample2");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 30, 30);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 40, 40);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 50, 50);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 60, 60);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 70, 70);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 80, 80);
		graph.addNode(node);

		node = new CHNodeImpl(8l, 90, 90);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 4, 1, "Edge 0 to 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 7, 1, "Edge 0 to 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 3l, 5, 1, "Edge 0 to 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 5l, 9, 1, "Edge 1 to 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 4l, 4, 1, "Edge 2 to 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 6l, 6, 1, "Edge 2 to 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 3, 1, "Edge 3 to 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 5l, 12, 1, "Edge 4 to 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 6l, 2, 1, "Edge 4 to 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 7l, 5, 1, "Edge 4 to 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 8l, 13, 1, "Edge 5 to 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 8l, 9, 1, "Edge 6 to 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 8l, 3, 1, "Edge 7 to 8");
		graph.addEdge(edge);

		graph.save();

		return graph;

	}

	public CHGraph generateMITExample3() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphMITExample3");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 10, 10);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 20, 20);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 30, 30);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 40, 40);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 50, 50);
		graph.addNode(node);

		node = new CHNodeImpl(5l, 60, 60);
		graph.addNode(node);

		node = new CHNodeImpl(6l, 70, 70);
		graph.addNode(node);

		node = new CHNodeImpl(7l, 80, 80);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 2, 1, "Edge 0 to 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 3, 1, "Edge 0 to 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 4l, 5, 1, "Edge 0 to 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 2, 1, "Edge 1 to 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 5l, 3, 1, "Edge 2 to 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 2, 1, "Edge 3 to 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 7l, 5, 1, "Edge 4 to 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 7l, 3, 1, "Edge 5 to 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 7l, 2, 1, "Edge 6 to 7");
		graph.addEdge(edge);

		graph.save();

		return graph;

	}

	/**
	 * Modified graph from generateMITExample to test the Bidirectional A*
	 * algorithm
	 * 
	 * @return graph The graph generated
	 */
	public CHGraph generateMITExample4() {

		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/graphMITExample4");

		CHEdge edge;
		CHNode node;

		node = new CHNodeImpl(0l, 100, 100);
		graph.addNode(node);

		node = new CHNodeImpl(1l, 100, 130);
		graph.addNode(node);

		node = new CHNodeImpl(2l, 121.79449, 145);
		graph.addNode(node);

		node = new CHNodeImpl(3l, 100, 160);
		graph.addNode(node);

		node = new CHNodeImpl(4l, 100, 190);
		graph.addNode(node);

		edge = new CHEdgeImpl(0l, 1l, 30, 1, "Edge 0 to 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 2l, 50, 1, "Edge 0 to 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 30, 1, "Edge 1 to 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 4l, 50, 1, "Edge 2 to 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 30, 1, "Edge 3 to 4");
		graph.addEdge(edge);

		graph.save();

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

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of createExampleGraph() graph from
	 * GraphHopper PrepareContractionHierarchiesTest.java class. This modified
	 * version have HyperPoI's HyperEdges.
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

		// graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of testDirectedGraph() graph from
	 * GraphHopper PrepareContractionHierarchiesTest.java class. This modified
	 * version have HyperPoI's HyperEdges.
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

		// graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		edge = new CHEdgeImpl(0l, 2l, 10, 1, "Edge 0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 0l, 10, 1, "Edge 1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 9l, 10, 1, "Edge 2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 2l, 10, 1, "Edge 3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 8l, 15, 1, "Edge 4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 2l, 15, 1, "Edge 5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 10, 1, "Edge 6");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 2l, 10, 1, "Edge 7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 100, 1, "Edge 8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(1l, 3l, 10, 1, "Edge 9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 1l, 10, 1, "Edge 10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 10, 1, "Edge 11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 10, 1, "Edge 12");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 5l, 12, 1, "Edge 13");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 3l, 12, 1, "Edge 14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 6l, 10, 1, "Edge 15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 3l, 10, 1, "Edge 16");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 7l, 12, 1, "Edge 17");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 3l, 12, 1, "Edge 18");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of testDirectedGraph3() graph from
	 * GraphHopper PrepareContractionHierarchiesTest.java class. This modified
	 * version have HyperPoI's HyperEdges.
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

		// graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

		graph.save();

		return graph;

	}

	
	public CHGraph generateGraphHopperExample4Test() {
		
		CHGraph graph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/bigTest");
		
		String defaultGraphLoc = Config.getProperty("example.graph.dir");

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager).setLocation(defaultGraphLoc).setStore(true);
		LevelGraphStorage graphStorage = gb.levelGraphCreate();

		NodeAccess na = graphStorage.getNodeAccess();
		na.setNode(0, 10, 30);
		na.setNode(1, 10, 10);
		na.setNode(2, 20, 10);
		na.setNode(3, 30, 10);
		na.setNode(4, 40, 10);
		na.setNode(5, 50, 0);
		na.setNode(6, 60, 0);
		na.setNode(7, 70, 10);
		na.setNode(8, 80, 10);
		na.setNode(9, 20, 30);
		na.setNode(10, 30, 30);
		na.setNode(11, 40, 30);
		na.setNode(12, 50, 20);
		na.setNode(13, 60, 20);
		na.setNode(14, 10, 0);
		na.setNode(15, -10, 10);
		na.setNode(16, -10, 30);
		na.setNode(17, 10, 20);
		na.setNode(18, 10, -10);
		na.setNode(19, 10, -20);
		na.setNode(20, 0, -10);
		na.setNode(21, 0, -20);
		na.setNode(22, 80, 0);
		na.setNode(23, 80, -10);
		na.setNode(24, 70, -10);
		na.setNode(25, 50, -10);
		na.setNode(26, 40, -30);
		na.setNode(27, 40, -10);
		na.setNode(28, 40, 50);
		na.setNode(29, 50, 50);
		na.setNode(30, 50, 40);
		na.setNode(31, 50, 30);
		na.setNode(32, 70, 30);
		na.setNode(33, 10, 50);
		
		graphStorage.edge(0, 9, 10, false);
		graphStorage.edge(9, 0, 10, false);
		graphStorage.edge(0, 16, 20, false);
		graphStorage.edge(16, 0, 20, false);
		graphStorage.edge(0, 17, 10, false);
		graphStorage.edge(17, 0, 10, false);
		graphStorage.edge(0, 19, 100, false);
		graphStorage.edge(0, 33, 20, false);
		graphStorage.edge(1, 2, 10, false);
		graphStorage.edge(2, 1, 10, false);
		graphStorage.edge(1, 14, 10, false);
		graphStorage.edge(14, 1, 10, false);
		graphStorage.edge(1, 15, 20, false);
		graphStorage.edge(15, 1, 20, false);
		graphStorage.edge(1, 17, 10, false);
		graphStorage.edge(17, 1, 10, false);
		graphStorage.edge(2, 3, 10, false);
		graphStorage.edge(3, 2, 10, false);
		graphStorage.edge(3, 4, 10, false);
		graphStorage.edge(4, 3, 10, false);
		graphStorage.edge(4, 5, 14.142136, false);
		graphStorage.edge(4, 31, 22.36068, false);
		graphStorage.edge(31, 4, 22.36068, false);
		graphStorage.edge(5, 6, 10, false);
		graphStorage.edge(5, 27, 14.142136, false);
		graphStorage.edge(27, 5, 14.142136, false);
		graphStorage.edge(6, 7, 14.142136, false);
		graphStorage.edge(7, 13, 14.142136, false);
		graphStorage.edge(7, 8, 10, false);
		graphStorage.edge(8, 7, 10, false);
		graphStorage.edge(8, 22, 10, false);
		graphStorage.edge(22, 8, 10, false);
		graphStorage.edge(9, 10, 10, false);
		graphStorage.edge(10, 9, 10, false);
		graphStorage.edge(10, 11, 10, false);
		graphStorage.edge(11, 10, 10, false);
		graphStorage.edge(11, 28, 20, false);
		graphStorage.edge(28, 11, 20, false);
		graphStorage.edge(12, 4, 14.142136, false);
		graphStorage.edge(13, 12, 10, false);
		graphStorage.edge(13, 32, 14.142136, false);
		graphStorage.edge(32, 13, 14.142136, false);
		graphStorage.edge(14, 18, 10, false);
		graphStorage.edge(18, 14, 10, false);
		graphStorage.edge(16, 21, 50.990195, false);
		graphStorage.edge(21, 16, 50.990195, false);
		graphStorage.edge(18, 19, 10, false);
		graphStorage.edge(19, 18, 10, false);
		graphStorage.edge(19, 20, 14.142136, false);
		graphStorage.edge(20, 19, 14.142136, false);
		graphStorage.edge(19, 21, 10, false);
		graphStorage.edge(21, 19, 10, false);
		graphStorage.edge(22, 23, 10, false);
		graphStorage.edge(23, 22, 10, false);
		graphStorage.edge(23, 24, 10, false);
		graphStorage.edge(24, 23, 10, false);
		graphStorage.edge(24, 25, 20, false);
		graphStorage.edge(25, 24, 20, false);
		graphStorage.edge(25, 26, 22.36068, false);
		graphStorage.edge(26, 25, 22.36068, false);
		graphStorage.edge(25, 27, 10, false);
		graphStorage.edge(27, 25, 10, false);
		graphStorage.edge(28, 29, 10, false);
		graphStorage.edge(29, 28, 10, false);
		graphStorage.edge(29, 30, 10, false);
		graphStorage.edge(30, 29, 10, false);
		graphStorage.edge(30, 31, 10, false);
		graphStorage.edge(31, 30, 10, false);

		graphStorage.flush();
		graphStorage.close();

		graphStorage = (LevelGraphStorage) gb.load();

		PrepareContractionHierarchies pch = new PrepareContractionHierarchies(encodingManager.getEncoder("car"),
				new ShortestWeighting());
		pch.setGraph(graphStorage);
		pch.doWork();
		pch.getG();
		
		
//		GraphStorage gs = gh.getGraph();
//		EdgeIterator edgeIterator = gs.getAllEdges();
		
		LevelGraphStorage graphLevel = (LevelGraphStorage) pch.getG();
		
		EdgeIterator edgeIterator = graphLevel.getAllEdges();
		
		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		int count2= 0;
		int countInvalidDirection = 0;
		int countBidirectional = 0;
		int countOneWay = 0;
		int countOneWayInverse = 0;
		while(edgeIterator.next()) {
			count++;

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			int distance = (int)NumberUtils.round(edgeIterator.getDistance(), 0); // Convert distance from meters to millimeters
			String label = edgeIterator.getName();

			int fromNodeLevel = graphLevel.getLevel(externalFromNodeId);
			int toNodeLevel = graphLevel.getLevel(externalToNodeId);
			
			double latitudeFrom = latLongToDouble(latLongToInt(graphLevel.getNodeAccess().getLatitude(externalFromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(graphLevel.getNodeAccess().getLongitude(externalFromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(graphLevel.getNodeAccess().getLatitude(externalToNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(graphLevel.getNodeAccess().getLongitude(externalToNodeId)));			

			CHNodeImpl fromNode, toNode;

			long fromNodeId, toNodeId;

			if(!hashExternalIdToId.containsKey(externalFromNodeId)){

				fromNode = new CHNodeImpl(externalFromNodeId, latitudeFrom, longitudeFrom);
				fromNode.setLevel(fromNodeLevel);
				graph.addNode(fromNode);
				fromNodeId = (long)fromNode.getId();
				hashExternalIdToId.put(externalFromNodeId, fromNodeId);
			} else {
				fromNodeId = hashExternalIdToId.get(externalFromNodeId);
			}

			if(!hashExternalIdToId.containsKey(externalToNodeId)){
				toNode = new CHNodeImpl(externalToNodeId, latitudeTo, longitudeTo);
				toNode.setLevel(toNodeLevel);
				graph.addNode(toNode);
				toNodeId = (long)toNode.getId();
				hashExternalIdToId.put(externalToNodeId, toNodeId);
			} else {
				toNodeId = hashExternalIdToId.get(externalToNodeId);
			}

			int direction = 9999;
			try {
				direction = getDirection(edgeIterator.getFlags());
			} catch (Exception e) {
				countInvalidDirection++;
			}
			
			if(fromNodeId == toNodeId) {
				
				PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
				List<Point> geometry = new ArrayList<Point>();
				for(int i =0; i < pl.size(); i++) {
					Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
					geometry.add(p);
				}
				
				count2++;
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				
				continue;
			}
			
			
//			if(fromNodeId == toNodeId) {
//				count2++;
//				logger.info("Edge not created, because fromNodeId({}) == toNodeId({})", fromNodeId, toNodeId);
//				continue;
//			}
			
			//geometry
			PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
			List<Point> geometry = new ArrayList<Point>();
			for(int i =0; i < pl.size(); i++) {
				Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
				geometry.add(p);
			}
			
			if(direction == 0) {          // Bidirectional
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countBidirectional++;
				
			} else if(direction == 1) {   // One direction: base -> adj
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWay++;
			} else if(direction == -1) {  // One direction: adj -> base
				CHEdge edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWayInverse++;
			} else {
			}
		}


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
		
		node = new CHNodeImpl(32l, 70, 30);
		graph.addNode(node);
		
		node = new CHNodeImpl(33l, 10, 50);
		graph.addNode(node);

		// TODO Create a constructor without the originalEdgeCounter
		edge = new CHEdgeImpl(0l, 9l, 10, 1, "Edge 0-9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 0l, 10, 1, "Edge 9-0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 16l, 20, 1, "Edge 0-16");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 0l, 20, 1, "Edge 16-0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 17l, 10, 1, "Edge 0-17");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 0l, 10, 1, "Edge 17-0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 9l, 100, 1, "Edge 0-9");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 33l, 20, 1, "Edge 0-33");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 2l, 10, 1, "Edge 1-2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 10, 1, "Edge 2-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 14l, 10, 1, "Edge 1-14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 1l, 10, 1, "Edge 14-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 15l, 20, 1, "Edge 1-15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(15l, 1l, 20, 1, "Edge 15-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 17l, 10, 1, "Edge 1-17");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(17l, 1l, 10, 1, "Edge 17-1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 10, 1, "Edge 2-3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 10, 1, "Edge 3-2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 10, 1, "Edge 3-4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 10, 1, "Edge 4-3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 5l, 14, 1, "Edge 4-5");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(4l, 31l, 22, 1, "Edge 4-31");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(31l, 4l, 22, 1, "Edge 31-4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 10, 1, "Edge 5-6");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(5l, 27l, 14, 1, "Edge 5-27");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(27l, 5l, 14, 1, "Edge 27-5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 7l, 14, 1, "Edge 6-7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 13l, 14, 1, "Edge 7-13");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(7l, 8l, 10, 1, "Edge 7-8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 7l, 10, 1, "Edge 8-7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 22l, 10, 1, "Edge 8-22");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(22l, 8l, 10, 1, "Edge 22-8");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(9l, 10l, 10, 1, "Edge 9-10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 9l, 10, 1, "Edge 10-9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 11l, 10, 1, "Edge 10-11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 10l, 10, 1, "Edge 11-10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 28l, 20, 1, "Edge 11-28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 11l, 20, 1, "Edge 28-11");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(12l, 4l, 14, 1, "Edge 12-4");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 12l, 10, 1, "Edge 13-12");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 32l, 14, 1, "Edge 13-32");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(32l, 13l, 14, 1, "Edge 32-13");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(14l, 18l, 10, 1, "Edge 14-18");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(18l, 14l, 10, 1, "Edge 18-14");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 21l, 51, 1, "Edge 16-21");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(21l, 16l, 51, 1, "Edge 21-16");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(18l, 19l, 10, 1, "Edge 18-19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 18l, 10, 1, "Edge 19-18");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 20l, 14, 1, "Edge 19-20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(20l, 19l, 14, 1, "Edge 20-19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 21l, 10, 1, "Edge 19-21");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(21l, 19l, 10, 1, "Edge 21-19");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(22l, 23l, 10, 1, "Edge 22-23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(23l, 22l, 10, 1, "Edge 23-22");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(23l, 24l, 10, 1, "Edge 23-24");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 23l, 10, 1, "Edge 24-23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 25l, 20, 1, "Edge 24-25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 24l, 20, 1, "Edge 25-24");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 26l, 22, 1, "Edge 25-26");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(26l, 25l, 22, 1, "Edge 26-25");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 27l, 10, 1, "Edge 25-27");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(27l, 25l, 10, 1, "Edge 27-25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 29l, 10, 1, "Edge 28-29");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 28l, 10, 1, "Edge 29-28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 30l, 10, 1, "Edge 29-30");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 29l, 10, 1, "Edge 30-29");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 31l, 10, 1, "Edge 30-31");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(31l, 30l, 10, 1, "Edge 31-30");
		graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of initRoundaboutGraph() graph from
	 * GraphHopper PrepareContractionHierarchiesTest.java class. This modified
	 * version have HyperPoI's HyperEdges.
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

		node = new CHNodeImpl(4l, 40, 10, 4);
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

		node = new CHNodeImpl(13l, 60, 20, 4);
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

		node = new CHNodeImpl(19l, 10, -20, 4);
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

		node = new CHNodeImpl(26l, 40, -30, 4);
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
		edge = new CHEdgeImpl(0l, 9l, 1, 1, "Edge 0-9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(9l, 0l, 1, 1, "Edge 9-0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(0l, 16l, 1, 1, "Edge 0-16");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 0l, 1, 1, "Edge 16-0");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(0l, 17l, 1, 1, "Edge 0-17");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(17l, 0l, 1, 1, "Edge 17-0");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 2l, 1, 1, "Edge 1-2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 2-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 14l, 1, 1, "Edge 1-14");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(14l, 1l, 1, 1, "Edge 14-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 15l, 1, 1, "Edge 1-15");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(15l, 1l, 1, 1, "Edge 15-1");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(1l, 17l, 1, 1, "Edge 1-17");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(17l, 1l, 1, 1, "Edge 17-1");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 2-3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 3-2");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 4l, 1, 1, "Edge 3-4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 3l, 1, 1, "Edge 4-3");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(4l, 5l, 1, 1, "Edge 4-5");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(4l, 31l, 1, 1, "Edge 4-31");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(31l, 4l, 1, 1, "Edge 31-4");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(5l, 6l, 1, 1, "Edge 5-6");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(5l, 27l, 1, 1, "Edge 5-27");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(27l, 5l, 1, 1, "Edge 27-5");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(6l, 7l, 1, 1, "Edge 6-7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(7l, 13l, 1, 1, "Edge 7-13");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(7l, 8l, 1, 1, "Edge 7-8");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 7l, 1, 1, "Edge 8-7");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(8l, 22l, 1, 1, "Edge 8-22");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(22l, 8l, 1, 1, "Edge 22-8");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(9l, 10l, 1, 1, "Edge 9-10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 9l, 1, 1, "Edge 10-9");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(10l, 11l, 1, 1, "Edge 10-11");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 10l, 1, 1, "Edge 11-10");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(11l, 28l, 1, 1, "Edge 11-28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 11l, 1, 1, "Edge 28-11");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(12l, 4l, 1, 1, "Edge 12-4");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(13l, 12l, 1, 1, "Edge 13-12");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(14l, 18l, 1, 1, "Edge 14-18");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(18l, 14l, 1, 1, "Edge 18-14");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(16l, 21l, 1, 1, "Edge 16-21");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(21l, 16l, 1, 1, "Edge 21-16");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(18l, 19l, 1, 1, "Edge 18-19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 18l, 1, 1, "Edge 19-18");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(19l, 20l, 1, 1, "Edge 19-20");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(20l, 19l, 1, 1, "Edge 20-19");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(19l, 21l, 1, 1, "Edge 19-21");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(21l, 19l, 1, 1, "Edge 21-19");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(22l, 23l, 1, 1, "Edge 22-23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(23l, 22l, 1, 1, "Edge 23-22");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(23l, 24l, 1, 1, "Edge 23-24");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 23l, 1, 1, "Edge 24-23");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(24l, 25l, 1, 1, "Edge 24-25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(25l, 24l, 1, 1, "Edge 25-24");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 26l, 1, 1, "Edge 25-26");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(26l, 25l, 1, 1, "Edge 26-25");
		graph.addEdge(edge);
		
		edge = new CHEdgeImpl(25l, 27l, 1, 1, "Edge 25-27");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(27l, 25l, 1, 1, "Edge 27-25");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(28l, 29l, 1, 1, "Edge 28-29");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 28l, 1, 1, "Edge 29-28");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(29l, 30l, 1, 1, "Edge 29-30");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 29l, 1, 1, "Edge 30-29");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(30l, 31l, 1, 1, "Edge 30-31");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(31l, 30l, 1, 1, "Edge 31-30");
		graph.addEdge(edge);

		// graph.createHyperPOIS();

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		// edge = new CHEdgeImpl(2l, 1l, 1, 1, "Edge 40"); //
		// graph.addEdge(edge);

		edge = new CHEdgeImpl(2l, 3l, 1, 1, "Edge 44");
		graph.addEdge(edge);

		// edge = new CHEdgeImpl(3l, 2l, 1, 1, "Edge 44");//
		// graph.addEdge(edge);

		edge = new CHEdgeImpl(3l, 1l, 1, 1, "Edge 45");
		graph.addEdge(edge);

		// edge = new CHEdgeImpl(1l, 3l, 1, 1, "Edge 45");//
		// graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		// edge = new CHEdgeImpl(16l, 0l, 1, 1, "Edge 1");
		// graph.addEdge(edge);

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

		edge = new CHEdgeImpl(19l, 21l, 1, 1, "Edge 34");
		graph.addEdge(edge);

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

		// edge = new CHEdgeImpl(32l, 16l, 1, 1, "Edge 65");
		// graph.addEdge(edge);

		edge = new CHEdgeImpl(16l, 32l, 1, 1, "Edge 65");
		graph.addEdge(edge);

		edge = new CHEdgeImpl(32l, 21l, 1, 1, "Edge 65");
		graph.addEdge(edge);

		// edge = new CHEdgeImpl(21l, 32l, 1, 1, "Edge 65");
		// graph.addEdge(edge);

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		graph.save();

		return graph;

	}

	/*
	 * This test uses a modified version of testCircleBug() graph from
	 * GraphHopper PrepareContractionHierarchiesTest.java class. This modified
	 * version have HyperPoI's HyperEdges.
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

		graph.setMaximumEdgeCount((int) graph.getNumberOfEdges());

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

		graph.save();

		return graph;

	}

	public void printGraphhopperGraph(EdgeIterator edgeIterator) {
		while (edgeIterator.next()) {

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();

			System.out.println("externalEdgeId: " + externalEdgeId + " externalFromNodeId: " + externalFromNodeId
					+ " externalToNodeId: " + externalToNodeId);

		}
	}
	
	public int getDirection(long flags) {
		long direction = (flags & 3);

		if(direction ==  1) {
			return 1;   // One direction: From --> To 
		} else if(direction ==  2) {
			return -1;  // One direction: To --> From
		} else if(direction == 3) {
			return 0;   // Bidirectional: To <--> From
		}
		else {
			throw new IllegalArgumentException("Invalid flag: " + direction);
		}
	}

}