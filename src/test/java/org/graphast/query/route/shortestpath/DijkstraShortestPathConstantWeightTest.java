//package org.graphast.query.route.shortestpath;
//
//import static org.junit.Assert.assertEquals;
//import it.unimi.dsi.fastutil.longs.LongList;
//
//import org.graphast.model.Graphast;
//import org.graphast.model.GraphastEdge;
//import org.graphast.model.GraphastImpl;
//import org.graphast.model.GraphastNode;
//import org.junit.Before;
//import org.junit.Test;
//
//public class DijkstraShortestPathConstantWeightTest {
//	private Graphast fg;
//	private String osmFile;
//	private String graphHopperDir;
//	private String graphastDir;
//	@Before
//	public void setup() {
//		//	osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
//		//	graphHopperDir = "/tmp/graphhopper/test/monaco";
//		//	graphastDir = "/tmp/graphast/test/monaco";
//		//	
//		//	fg = new GraphastImpl(graphastDir);
//		fg = new GraphastImpl(graphastDir);
//
//		GraphastEdge e;
//		GraphastNode v;
//
//		v = new GraphastNode(0l, 0d, 10d);
//		fg.addNode(v);
//
//		v = new GraphastNode(1l, 10d, 0d);
//		fg.addNode(v);
//
//		v = new GraphastNode(2l, 30d, 20d);
//		fg.addNode(v);
//
//		v = new GraphastNode(3l, 40d, 20d);
//		fg.addNode(v);
//
//		v = new GraphastNode(4l, 50d, 30d);
//		fg.addNode(v);
//
//		v = new GraphastNode(5l, 60d, 20d);
//		fg.addNode(v);
//
//		v = new GraphastNode(6l, 60d, 0d);
//		fg.addNode(v);
//
//		e = new GraphastEdge(0l, 1l, 1);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(0l, 2l, 5);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(1l, 2l, 3);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(2l, 3l, 3);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(3l, 4l, 3);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(3l, 5l, 4);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(4l, 5l, 2);
//		fg.addEdge(e);
//
//		e = new GraphastEdge(5l, 6l, 1);
//		fg.addEdge(e);
//	}
//	@Test
//	public void shortestPathTest2() {
//		Long sourceId = fg.getNode(0d, 10d);
//		Long destinationId = fg.getNode(60d, 0d);
//		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(fg);
//		GraphastNode sourceNode = fg.getNode(sourceId);
//		GraphastNode destinationNode = fg.getNode(destinationId);
////		dj.shortestPath(sourceNode, destinationNode);
//		System.out.println(dj.shortestPath(sourceNode, destinationNode));
//		//	GraphastAlgorithms dj = new DijkstraShortestPathWithConstantWeight(fg, source, destination);
//		//	dj.execute();
//	}
//	@Test
//	public void getOutEdgesTest() {
//		LongList neighbors = fg.getOutEdges(0);
//		int position = 0;
//		assertEquals(0, (long) neighbors.get(position++));
//		assertEquals(1,  (long) neighbors.get(position++));
//
//	}
//
//}


package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.OSMImporter;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastImpl;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DijkstraShortestPathConstantWeightTest {
	
	private static Graphast graphMonaco;
	private static Graphast graphExample;
	private static Graphast graphExample2;
	
	@BeforeClass
	public static void setup() {
		
		String osmFile = DijkstraShortestPathConstantWeight.class.getResource("/monaco-150112.osm.pbf").getPath();
		String graphHopperMonacoDir = "/tmp/graphhopper/test/monaco";
		String graphastMonacoDir = "/tmp/graphast/test/monaco";
		
		String graphHopperExampleDir = "/tmp/graphhopper/test/example";
		String graphastExampleDir = "/tmp/graphast/test/example";
		
		graphMonaco = new GraphastImpl(graphastMonacoDir);
		
		try {
			graphMonaco.load();
		} catch (IOException e) {
			graphMonaco = new OSMImporter().execute(osmFile, graphHopperMonacoDir, graphastMonacoDir);
		}
		
		new GraphGenerator().generateExample();
		
		graphExample = new GraphastImpl(graphastExampleDir);
		try {
			graphExample.load();
		} catch (IOException e) {
			graphExample = new OSMImporter().execute(osmFile, graphHopperExampleDir, graphastExampleDir);
		}
		
		graphExample2 = new GraphGenerator().generateExample2();
		
	}
	
	@Test
	public void shortestPathMonacoTest() {
		
		assertEquals(751, graphMonaco.getNumberOfNodes());
		assertEquals(1306, graphMonaco.getNumberOfEdges());
		
		Long source = graphMonaco.getNode(43.728424, 7.414896);
		Long target = graphMonaco.getNode(43.735437, 7.42122);
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphMonaco);
		System.out.println(dj.shortestPath(source, target));
//		 TODO: fix this assertion
//		 1117.9563590469443 (GraphHooper result)
//		 assertEquals(1117, dj.shortestPath(source, target));
	}
	
	@Test
	public void shortestPathExampleTest() {
		
		assertEquals(6, graphExample.getNumberOfNodes());
		assertEquals(10, graphExample.getNumberOfEdges());
		
		Long source = 1L;
		Long target = 4L;
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphExample);
		assertEquals(8, dj.shortestPath(source, target));
		
	}
	
	@Test
	public void shortestPathExample2Test() {
		
		assertEquals(7, graphExample2.getNumberOfNodes());
		assertEquals(8, graphExample2.getNumberOfEdges());
		
		Long source = 0L;
		Long target = 6L;
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphExample2);
		assertEquals(12, dj.shortestPath(source, target));
		
	}
	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir("/tmp/graphhopper/test");
		FileUtils.deleteDir("/tmp/graphast/test");
		
	}

}
