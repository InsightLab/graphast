package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graphast;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.graphhopper.util.StopWatch;

import static org.graphast.util.DistanceUtils.distanceLatLong;

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
		
		// To see the error, try this coordinates
		//		from: 43.729825,7.414146
		//		to: 43.730577,7.415487
		
		assertEquals(751, graphMonaco.getNumberOfNodes());
		assertEquals(1306, graphMonaco.getNumberOfEdges());
		
		Long source = graphMonaco.getNode(43.7294668047756,7.413772473047058);
		Long target = graphMonaco.getNode(43.73079058671274,7.415815422292399);
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphMonaco);
		
		//TODO Improve this distance function
		double diffFrom = distanceLatLong(43.7294668047756,7.413772473047058, 43.7294668047756,7.413772473047058);
		double diffTo = distanceLatLong(43.73079058671274,7.415815422292399, 43.73079058671274,7.415815422292399);
		StopWatch sw = new StopWatch();
        sw.start();
		double shortestPath = dj.shortestPath(source, target);
		sw.stop();
		
		System.out.println("execution time:" + sw.getTime());
		int realDistance = (int)(((shortestPath/1000.0) - diffFrom - diffTo)*1000);

		assertEquals(228910, realDistance);

	}
	
	@Test
	public void shortestPathMonacoTest2() {
		
		assertEquals(751, graphMonaco.getNumberOfNodes());
		assertEquals(1306, graphMonaco.getNumberOfEdges());
		
		Long source = graphMonaco.getNode(43.72842465479131, 7.414896579419745);
		Long target = graphMonaco.getNode(43.7354373276704, 7.4212202598427295);
		
		AbstractShortestPathService dj = new DijkstraShortestPathConstantWeight(graphMonaco);
	
		// 1117.9563590469443m = 1117956mm (GraphHooper result)
        assertEquals(1136643, dj.shortestPath(source, target));

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
