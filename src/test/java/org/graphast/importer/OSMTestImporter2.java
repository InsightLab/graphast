package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import org.graphast.model.Graph;
import org.junit.Before;
import org.junit.Test;

public class OSMTestImporter2 {

	private String graphHopperDir;
	private String graphastDir;

	@Before
	public void setup() {
	
		graphHopperDir = this.getClass().getResource("/example").getPath();
		graphastDir = "/tmp/graphast/test/example";
	}

	@Test
	public void executeTest() {

		Graph graph = new OSMImporterImpl(null, graphHopperDir, graphastDir).execute();

		assertEquals(6, graph.getNumberOfNodes());
		assertEquals(9, graph.getNumberOfEdges());

	}
	
}
