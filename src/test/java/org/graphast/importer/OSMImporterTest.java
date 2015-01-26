package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import org.graphast.model.Graph;
import org.junit.Before;
import org.junit.Test;

public class OSMImporterTest {

	private String osmFile;
	private String graphHopperDir;
	private String graphastDir;

	@Before
	public void setup() {
		osmFile = this.getClass().getResource("/monaco-150112.osm.pbf").getPath();
		graphHopperDir = "/tmp/graphhopper/test/monaco";
		graphastDir = "/tmp/graphast/test/monaco";
	}

	@Test
	public void executeTest() {

		Graph graph = new OSMImporterImpl(osmFile, graphHopperDir, graphastDir).execute();

		assertEquals(751, graph.getNumberOfNodes());
		assertEquals(1306, graph.getNumberOfEdges());

	}

}
