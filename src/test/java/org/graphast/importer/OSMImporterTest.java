package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.junit.Before;
import org.junit.Test;

public class OSMImporterTest {

	private String osmFile;
	private String graphastDir;

	@Before
	public void setup() {
		osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		graphastDir = Configuration.USER_HOME + "/graphast/test/monaco";
	}

	@Test
	public void executeTest() {

		Graph graph = new OSMImporterImpl(osmFile, graphastDir).execute();

		assertEquals(67478, graph.getNumberOfNodes());
		assertEquals(158497, graph.getNumberOfEdges());

	}

}
