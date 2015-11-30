package org.graphast.importer;

import org.graphast.config.Configuration;
import org.graphast.model.GraphBounds;
import org.junit.Assert;
import org.junit.Test;

public class OSMDBImporterTest {
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	@Test
	public void createGraphTest1k() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_1k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(15, graph.getCategories().size());
		Assert.assertEquals(809+15, graph.getNumberOfNodes());
		Assert.assertEquals(844+15, graph.getNumberOfEdges());
	}
	
	@Test
	public void createGraphTest10k() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_10k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(77, graph.getCategories().size());
		Assert.assertEquals(6947, graph.getNumberOfNodes());
		Assert.assertEquals(8411, graph.getNumberOfEdges());
	}
	
	@Test
	public void createGraphTest100k() {	
		OSMDBImporter importer = new OSMDBImporter("view_exp_100k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(425, graph.getCategories().size());
		Assert.assertEquals(75919, graph.getNumberOfNodes());
		Assert.assertEquals(98653, graph.getNumberOfEdges());
	}
	
	@Test
	public void createGraphTest300k() {	
		OSMDBImporter importer = new OSMDBImporter("view_exp_300mil", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(425, graph.getCategories().size());
		Assert.assertEquals(236216, graph.getNumberOfNodes());
		Assert.assertEquals(275720, graph.getNumberOfEdges());
	}
}
