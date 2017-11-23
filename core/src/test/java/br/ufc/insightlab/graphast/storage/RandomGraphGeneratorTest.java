package br.ufc.insightlab.graphast.storage;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import br.ufc.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import br.ufc.insightlab.graphast.query.utils.DistanceVector;
import br.ufc.insightlab.graphast.storage.RandomGraphGenerator;
import br.ufc.insightlab.graphast.storage.StorageUtils;

public class RandomGraphGeneratorTest {
	
	public static Graph g;
	public static final int nNodes = 2000;
	public static final float density = 0.01f;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		g = RandomGraphGenerator.generateRandomMMapGraph("random_graph", nNodes, density);
	}

	@Test
	public void testSizes() {
		assertEquals("Number of generated nodes", nNodes, g.getNumberOfNodes());
	}
	
	@Test
	public void testDijkstra() {
		try {
			ShortestPathStrategy strategy = new DijkstraStrategy(g);
			DistanceVector vector = strategy.run(10);
			vector.print(10, 40);
		} catch (Exception e) {
			fail("It shouldn't throw any exception");
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		StorageUtils.deleteMMapGraph("graphs/MMap/random_graph");
	}

}
