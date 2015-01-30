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
	private static Graph graph;

	@BeforeClass
	public static void setup() {
		osmFile = OSMImporterTest.class.getResource("/monaco-150112.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/monaco";
		FileUtils.deleteDir(graphastDir);
		graph = new OSMImporterImpl(osmFile, graphastDir).execute();
	}

	@Test
	public void executeTest() {
		assertEquals(751, graph.getNumberOfNodes());
		assertEquals(1306, graph.getNumberOfEdges());
		File dir = new File(graphastDir);
		assertTrue(dir.isDirectory());
		
	}

}
