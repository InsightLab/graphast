package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.graphast.model.Graphast;
import org.graphast.model.GraphastImpl;
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

		Graphast graph = new OSMImporter().execute(osmFile, graphHopperDir, graphastDir);

		assertEquals(751, graph.getNumberOfNodes());
		assertEquals(957, graph.getNumberOfEdges());

	}

}
