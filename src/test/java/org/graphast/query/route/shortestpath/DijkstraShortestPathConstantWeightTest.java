package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.graphast.importer.OSMImporter;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastImpl;
import org.junit.Before;
import org.junit.Test;

public class DijkstraShortestPathConstantWeightTest {
	//private Graphast fg;
	private String osmFile;
	private String graphHopperDir;
	private String graphastDir;
	
	@Before
	public void setup() {
		osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		graphHopperDir = "/tmp/graphhopper/test/monaco";
		graphastDir = "/tmp/graphast/test/monaco";
		
//		fg = new GraphastImpl("/tmp/graphast/test/sample");
//		
//		GraphastNode v = new GraphastNode(3l, 10d, 10d);
//		fg.addNode(v);
//		
//		v = new GraphastNode(4l, 10d, 20d);
//		fg.addNode(v);
//		
//		v = new GraphastNode(2l, 10d, 30d);
//		fg.addNode(v);
//		
//		v = new GraphastNode(6l, 10d, 40d);
//		fg.addNode(v);
//		
//		v = new GraphastNode(7l, 11d, 32d);
//		fg.addNode(v);
//		
//		v = new GraphastNode(7, 11, 32, "Banco");
//		fg.addNode(v);
//		
//		GraphastEdge e = new GraphastEdge(0l, 1l, 10);
////		public GraphastEdge(long fromNode, long toNode, int distance,
////				short[] costs, List<Point> geometry, String label)
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(1l, 0l, 20);
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(0l, 2l, 30);
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(2l, 0l, 40);
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(0l, 3l, 50);
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(2l, 4l, 60);
//		fg.addEdge(e);
//		
//		e = new GraphastEdge(3l, 0l, 70);
//		fg.addEdge(e);
	}
	
	@Test
	public void shortestPathTest2() {
		Graphast graph = new GraphastImpl(graphastDir);
		try {
			graph.load();
		} catch (IOException e) {
			graph = new OSMImporter().execute(osmFile, graphHopperDir, graphastDir);
		}
		assertEquals(1888, graph.getNumberOfNodes());
		assertEquals(944, graph.getNumberOfEdges());
		
		Long source = graph.getNode(43.728424, 7.414896);
		Long target = graph.getNode(43.735437, 7.42122);
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graph);

		// TODO: fix this assertion
		// 1117.9563590469443 (GraphHooper result)
		assertEquals(1117, dj.shortestPath(source, target));
	}
}
