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

	@BeforeClass
	public static void setup() {
		osmFile = OSMImporterTest.class.getResource("/monaco-150112.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/monaco";
		FileUtils.deleteDir(graphastDir);
		monaco = new OSMImporterImpl(osmFile, graphastDir).execute();
		
		osmFile = OSMImporterTest.class.getResource("/andorra-latest.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/andorra";
		FileUtils.deleteDir(graphastDir);
		andorra = new OSMImporterImpl(osmFile, graphastDir).execute();
	}

	@Test
	public void executeMonacoTest() {
		assertEquals(751, monaco.getNumberOfNodes());
		assertEquals(1306, monaco.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}
	
	@Test
	public void executeAndorraTest() {
		assertEquals(2621, andorra.getNumberOfNodes());
		assertEquals(5326, andorra.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}

}
