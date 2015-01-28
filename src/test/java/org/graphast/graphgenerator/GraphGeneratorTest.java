package org.graphast.graphgenerator;

import static org.junit.Assert.assertEquals;

import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphGeneratorTest {
	private static Graph graphMonaco;
	private static Graph graphExample;
	private static GraphBounds graphExample2;

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
	public void Test() {
		
		graphExample2.getCosts();
		
		System.out.println(graphExample2.getUpperBound());
		System.out.println(graphExample2.getLowerBound());
		
	}
	
	@AfterClass
	public static void tearDown() {
		FileUtils.deleteDir("/tmp/graphhopper/test");
		FileUtils.deleteDir("/tmp/graphast/test");
	}
}
