package org.graphast.importer;

import java.util.HashMap;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

import org.graphast.config.Configuration;
import org.graphast.model.Edge;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.junit.Assert;
import org.junit.Test;

public class OSMDBImporterTest {
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	@Test
	public void createGraphTest1k() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_1k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		//Assert.assertEquals(15, graph.getCategories().size());
		//Assert.assertEquals(809+15, graph.getNumberOfNodes());
		//Assert.assertEquals(844+15, graph.getNumberOfEdges());
		
		//Assert.assertEquals(14, graph.getCategories().size());
		//Assert.assertEquals(809, graph.getNumberOfNodes());
		//Assert.assertEquals(844, graph.getNumberOfEdges());
		
		for (int i = 0; i < graph.getNumberOfNodes(); i++) {
			Node node = graph.getNode(i);
			HashMap<Node, Integer> accessNeighborhood = graph.accessNeighborhood(node, 0);
			int size = accessNeighborhood.size();
			//System.out.println(String.format("the node %s contens %s neighbors", i, size));
			System.out.println(String.format("%s,%s", i, size));
			
		}
	}
	
	@Test
	public void costEdgeTest() {
		OSMDBImporter importer = new OSMDBImporter("view_exp_1k", PATH_GRAPH);
		GraphBounds graphBounds = importer.execute();
		
		for (int i = 0; i < graphBounds.getNumberOfEdges() - 1; i++) {
			Edge edge = graphBounds.getEdge(i);
			Assert.assertEquals(96, edge.getCosts().length);
		}
	}
	
	@Test
	public void createGraphTest10k() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_10k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(73, graph.getCategories().size()); //77
		Assert.assertEquals(6870, graph.getNumberOfNodes()); //6947
		Assert.assertEquals(8334, graph.getNumberOfEdges()); //
		
		for (int i = 0; i < graph.getNumberOfNodes(); i++) {
			Node node = graph.getNode(i);
			HashMap<Node, Integer> accessNeighborhood = graph.accessNeighborhood(node, 0);
			int size = accessNeighborhood.size();
			//System.out.println(String.format("the node %s contens %s neighbors", i, size));
			System.out.println(String.format("%s,%s", i, size));
			
		}
	}
	
	@Test
	public void createGraphTest100k() {	
		OSMDBImporter importer = new OSMDBImporter("view_exp_100k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(379, graph.getCategories().size());//379
		Assert.assertEquals(75489, graph.getNumberOfNodes()); //75919
		Assert.assertEquals(98223, graph.getNumberOfEdges()); //98653
		
		for (int i = 0; i < graph.getNumberOfNodes(); i++) {
			Node node = graph.getNode(i);
			HashMap<Node, Integer> accessNeighborhood = graph.accessNeighborhood(node, 0);
			int size = accessNeighborhood.size();
			System.out.println(String.format("%s,%s", i, size));
			
		}
	}
	
	@Test
	public void createGraphTest50k() {	
		OSMDBImporter importer = new OSMDBImporter("view_exp_50k", PATH_GRAPH);
		GraphBounds graph = importer.execute();
		Assert.assertNotNull(graph);
		Assert.assertEquals(328, graph.getCategories().size()); // 425
		Assert.assertEquals(33595, graph.getNumberOfNodes());  // 236216
		Assert.assertEquals(42385, graph.getNumberOfEdges());
		
		for (int i = 0; i < graph.getNumberOfNodes(); i++) {
			Node node = graph.getNode(i);
			HashMap<Node, Integer> accessNeighborhood = graph.accessNeighborhood(node, 0);
			int size = accessNeighborhood.size();
			System.out.println(String.format("%s,%s", i, size));
			
		}
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
