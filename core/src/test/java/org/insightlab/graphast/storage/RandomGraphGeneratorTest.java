package org.insightlab.graphast.storage;

import static org.junit.Assert.*;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import org.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import org.insightlab.graphast.query.utils.DistanceVector;
import org.insightlab.graphast.storage.RandomGraphGenerator;
import org.insightlab.graphast.storage.StorageUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RandomGraphGeneratorTest {
	
	public static Graph g;
	public static final int nNodes = 2000;
	public static final float density = 0.01f;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RandomGraphGenerator generator = new RandomGraphGenerator();
		g = generator.generateRandomMMapGraph("random_graph", nNodes, density);
	}

	@Test
	public void testSizes() {
		assertEquals("Number of generated nodes", nNodes, g.getNumberOfNodes());
	}
	
	@Test
	public void testDijkstra() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(10);
		vector.print(10, 40);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		StorageUtils.deleteMMapGraph("graphs/MMap/random_graph");
	}

}
