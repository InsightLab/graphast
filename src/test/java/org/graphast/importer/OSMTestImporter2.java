package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import org.graphast.model.Graphast;
import org.junit.Before;
import org.junit.Test;

public class OSMTestImporter2 {

	private String graphHopperDir;
	private String graphastDir;

	@Before
	public void setup() {
	
		graphHopperDir = this.getClass().getResource("/example").getPath();
		graphastDir = "/tmp/graphast/test/monaco";
	}

	@Test
	public void executeTest() {

		Graphast graph = new OSMImporter().execute(null, graphHopperDir, graphastDir);

		assertEquals(6, graph.getNumberOfNodes());
		assertEquals(9, graph.getNumberOfEdges());

	}
	
}
