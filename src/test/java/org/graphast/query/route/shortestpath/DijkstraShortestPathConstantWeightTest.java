package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graphast;
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
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
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
		// 1117.9563590469443m = 1117956mm (GraphHooper result)
		assertEquals(1117956, dj.shortestPath(source, target));
	}
	
	@Test
	public void shortestPathExampleTest() {
		assertEquals(6, graphExample.getNumberOfNodes());
		assertEquals(10, graphExample.getNumberOfEdges());
		
		Long source = 1L; // External ID = 5
		Long target = 4L; // External ID = 2
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphExample);
		assertEquals(9000, dj.shortestPath(source, target));
		
		source = 0L; // External ID = 1
		target = 5L; // External ID = 4
		dj = new DijkstraShortestPathConstantWeight(graphExample);
		assertEquals(8100, dj.shortestPath(source, target));		
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
