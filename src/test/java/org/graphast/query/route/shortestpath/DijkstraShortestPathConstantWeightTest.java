package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graphast;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.graphast.util.DistanceUtils.euclidianDistance;

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
		
		Long source = graphMonaco.getNode(43.72899201651645,7.414386400901967);
		Long target = graphMonaco.getNode(43.7294668047756,7.413772473047058);
		
		
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphMonaco);
//		dj.shortestPath(source, target);
		
		double diffFrom = euclidianDistance(43.72899201651645,7.414386400901967, 43.729065,7.414243);
		double diffTo = euclidianDistance(43.7294668047756,7.413772473047058, 43.729402,7.413851);
		double shortestPath = dj.shortestPath(source, target);
		// TODO Improve this calculation
		System.out.println((shortestPath/1000.0) - diffFrom - diffTo);
		
		
		// GraphHopper result 	-> 1117.9563590469443m 	= 1117956.359046944mm
		// Graphast result		-> 1136.643m 			= 1136643mm
		assertEquals(3998760, dj.shortestPath(source, target));
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
