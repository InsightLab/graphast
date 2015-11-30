package org.graphast.importer;

import org.graphast.config.Configuration;
import org.graphast.model.GraphBounds;
import org.junit.Assert;
import org.junit.Test;

public class PoiTaxiFortalezaImporterTest {
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";

	@Test
	public void createGraphWithPoiTaxiTest() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_1k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		
		PoiTaxiFortalezaImporter importerWithPoi = new PoiTaxiFortalezaImporter(graph);
		GraphBounds graphWithPoi = importerWithPoi.getGraph();
		
		Assert.assertNotNull(graphWithPoi);
		Assert.assertEquals(809, graph.getNumberOfNodes());
		Assert.assertEquals(844, graph.getNumberOfEdges());
		Assert.assertEquals(15, graph.getCategories().size());
		
	}
}
