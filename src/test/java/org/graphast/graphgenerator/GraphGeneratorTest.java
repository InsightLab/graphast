package org.graphast.graphgenerator;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.util.FileUtils;
import org.graphast.util.MapUtils;
import org.graphast.util.SimpleMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphGeneratorTest {
	private static Graph graphMonaco;
	private static Graph graphExample;
	private static GraphBounds graphExample2;
	private static GraphBounds graphPoI;

	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
		graphPoI = new GraphGenerator().generateExamplePoI();
	}
	

	@Test
	public void shortestPathMonacoTest() {
		assertEquals(751, graphMonaco.getNumberOfNodes());
		assertEquals(1306, graphMonaco.getNumberOfEdges());
	}
	
	@Test
	public void shortestPathExampleTest() {
		assertEquals(6, graphExample.getNumberOfNodes());
		assertEquals(10, graphExample.getNumberOfEdges());
	}	
	
	@Test
	public void shortestPathExample2Test() {
		assertEquals(7, graphExample2.getNumberOfNodes());
		assertEquals(8, graphExample2.getNumberOfEdges());
	}
	
	@Test
	public void examplePoITest() {
		
		assertEquals(10, graphPoI.getNumberOfNodes());
		assertEquals(13, graphPoI.getNumberOfEdges());
		
	}
	
	@Test
	public void upperAndLowerBoundsTest() {
		
//		graphExample2.getCosts();
		
		Map<Long, Integer> realValueUpper = new SimpleMap<Long, Integer>(2l,2, 6l,11, 3l,11, 1l,10, 5l,13, 4l,10, 0l,4, 7l,15);
		Map<Long, Integer> realValueLower = new SimpleMap<Long, Integer>(2l,1, 6l,3, 3l,4, 1l,2, 5l,2, 4l,1, 0l,2, 7l,2);

		assertEquals(true, MapUtils.equalMaps(realValueUpper, graphExample2.getEdgesUpperBound()));
		assertEquals(true, MapUtils.equalMaps(realValueLower, graphExample2.getEdgesLowerBound()));

	}
	
	@AfterClass
	public static void tearDown() {
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}
