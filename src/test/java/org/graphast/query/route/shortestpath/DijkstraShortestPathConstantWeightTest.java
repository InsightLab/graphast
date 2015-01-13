package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.geometry.Point;
import org.graphast.importer.OSMImporter;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastNode;
import org.junit.Before;
import org.junit.Test;

public class DijkstraShortestPathConstantWeightTest {
	private Graphast fg;
	private String osmFile;
	private String graphHopperDir;
	private String graphastDir;
	
	@Before
	public void setup() {
		osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		graphHopperDir = "/tmp/graphhopper/test/monaco";
		graphastDir = "/tmp/graphast/test/monaco";
		
		fg = new GraphastImpl(graphastDir);
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
			new OSMImporter().execute(osmFile, graphHopperDir, graphastDir);
			try {
				graph.load();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		Long source = fg.getNode(43.72842465479131, 7.414896579419745);
		Long destination = fg.getNode(43.7354373276704, 7.4212202598427295);
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(fg);
		System.out.println(dj.shortestPath(fg.getNode(source), fg.getNode(destination)));
		
//		GraphastAlgorithms dj = new DijkstraShortestPathWithConstantWeight(fg, source, destination);
//		dj.execute();
	}
}
