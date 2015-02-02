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
	private static Graph graphExample3;
	private static Graph graphExamplePoI;

	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
		graphExample3 = new GraphGenerator().generateExample3();
		graphExamplePoI = new GraphGenerator().generateExamplePoI();
	}
	

	@Test
	public void generateMonacoTest() {
		assertEquals(751, graphMonaco.getNumberOfNodes());
		assertEquals(1306, graphMonaco.getNumberOfEdges());
	}
	
	@Test
	public void generateExampleTest() {
		assertEquals(6, graphExample.getNumberOfNodes());
		assertEquals(10, graphExample.getNumberOfEdges());
	}	
	
	@Test
	public void generateExample2Test() {
		assertEquals(7, graphExample2.getNumberOfNodes());
		assertEquals(8, graphExample2.getNumberOfEdges());
	}
	
	@Test
	public void generateExample3Test() {
		assertEquals(6, graphExample3.getNumberOfNodes());
		assertEquals(7, graphExample3.getNumberOfEdges());
	}
	
	@Test
	public void generateExamplePoITest() {
		assertEquals(10, graphExamplePoI.getNumberOfNodes());
		assertEquals(13, graphExamplePoI.getNumberOfEdges());
	}
	
	
	@Test
	public void upperAndLowerBoundsTest() {
		
		graphExample2.getCosts();
		
		Map<Object, Object> realValueUpper = new SimpleMap(2,2,6,11,3,11,1,10,5,13,4,10,0,4,7,15);
		Map<Object, Object> realValueLower = new SimpleMap(2,1, 6,3, 3,4, 1,2, 5,2, 4,1, 0,2, 7,2);

		assertEquals(true, MapUtils.equalMaps(realValueUpper, graphExample2.getUpperBound()));
		assertEquals(true, MapUtils.equalMaps(realValueLower, graphExample2.getLowerBound()));

	}
	
	
	
	@AfterClass
	public static void tearDown() {
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}
