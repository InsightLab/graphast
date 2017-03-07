package org.graphast.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.graphast.util.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

//TODO Verify all values from assertEquals
public class OSMImporterTest {

	private static String osmFile;
	private static String graphastDir;
	private static Graph monaco;

	@BeforeClass
	public static void setup() {
		osmFile = OSMImporterTest.class.getResource("/monaco-latest.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/monaco";
		FileUtils.deleteDir(graphastDir);
		monaco = new OSMImporterImpl(osmFile, graphastDir).execute();
		
		POIImporter.importPoIList(monaco, "src/test/resources/monaco-latest.csv");
		
	}

	@Test
	public void executeMonacoTest() {
		
		assertEquals(777, monaco.getNumberOfNodes());
		assertEquals(1340, monaco.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}
	
	@Test
	public void monacoPoIImporterTest() {
		
		assertEquals(161, monaco.getNode(140).getCategory());
		assertEquals("Saint-Charles", monaco.getNode(140).getLabel());
		
	}
	
	@Test
	public void monacoCostGeneratorTest() {
		
		for (int i = 0; i < monaco.getNumberOfEdges(); i++) {
			monaco.setEdgeCosts(i, CostGenerator.generateSyntheticEdgesCosts(monaco.getEdge(i).getDistance()));
		}
		
		System.out.println(monaco.getEdge(0).getCosts());
		
	}
	
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