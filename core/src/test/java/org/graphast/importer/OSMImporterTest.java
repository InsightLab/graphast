package org.graphast.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.graphast.util.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class OSMImporterTest {

	private static String osmFile;
	private static String graphastDir;
	private static Graph monaco;
	private static Graph andorra;
//	private static Graph washington;

	@BeforeClass
	public static void setup() {
		osmFile = OSMImporterTest.class.getResource("/monaco-150112.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/monaco";
		FileUtils.deleteDir(graphastDir);
		monaco = new OSMImporterImpl(osmFile, graphastDir).execute();
		
		osmFile = OSMImporterTest.class.getResource("/andorra-150305.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/andorra";
		FileUtils.deleteDir(graphastDir);
		andorra = new OSMImporterImpl(osmFile, graphastDir).execute();
		
		POIImporter.importPoIList(monaco, "src/test/resources/monaco-latest.csv");
		
//		osmFile = OSMImporterTest.class.getResource("/washington-latest.osm.pbf").getPath();
//		graphastDir = Configuration.USER_HOME + "/graphast/test/washington";
//		FileUtils.deleteDir(graphastDir);
//		washington = new OSMImporterImpl(osmFile, graphastDir).execute();
	}

	@Test
	public void executeMonacoTest() {
		assertEquals(751, monaco.getNumberOfNodes());
		assertEquals(1306, monaco.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}
	
	@Test
	public void monacoPoIImporterTest() {
		
		assertEquals(161, monaco.getNode(140).getCategory());
		assertEquals("Crémaillère", monaco.getNode(140).getLabel());
		
//		assertEquals(751, monaco.getNumberOfNodes());
		
	}
	
	@Test
	public void monacoCostGeneratorTest() {
		
		
		for (int i = 0; i < monaco.getNumberOfEdges(); i++) {
			
			monaco.setEdgeCosts(i, CostGenerator.generateSyntheticEdgesCosts(monaco.getEdge(i).getDistance()));
			
		}
		
		System.out.println(monaco.getEdge(0).getCosts());
		
	}
	
	@Test
	public void executeAndorraTest() {
		assertEquals(2621, andorra.getNumberOfNodes());
		assertEquals(5326, andorra.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}
	
//	@Test
//	public void executeWashingtonTest() {
//		assertEquals(636016, washington.getNumberOfNodes());
//		assertEquals(1548054, washington.getNumberOfEdges());
//		File dir = new File(graphastDir);
//		assertTrue(dir.isDirectory());
//		
//	}
	
	@Test
	public void geometryTest() {
		assertEquals(monaco.getNode(monaco.getEdge(0).getFromNode()).getLatitude(),
				monaco.getEdge(0).getGeometry().get(0).getLatitude(), 0);
		assertEquals(43.739037, monaco.getEdge(0).getGeometry().get(1).getLatitude(), 0);
		assertEquals(monaco.getNode(monaco.getEdge(0).getToNode()).getLatitude(),
				monaco.getEdge(0).getGeometry().get(2).getLatitude(), 0);
		
		assertEquals(monaco.getNode(monaco.getEdge(0).getFromNode()).getLongitude(),
				monaco.getEdge(0).getGeometry().get(0).getLongitude(), 0);
		assertEquals(7.425796, monaco.getEdge(0).getGeometry().get(1).getLongitude(), 0);
		assertEquals(monaco.getNode(monaco.getEdge(0).getToNode()).getLongitude(),
				monaco.getEdge(0).getGeometry().get(2).getLongitude(), 0);
	}

}