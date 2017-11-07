package org.graphast.storage;

import static org.junit.Assert.*;

import org.graphast.model.Graph;
import org.graphast.query.shortestpath.DijkstraStrategy;
import org.graphast.query.shortestpath.ShortestPathStrategy;
import org.graphast.query.utils.DistanceVector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(10);
		vector.print(10, 40);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		StorageUtils.deleteMMapGraph("graphs/MMap/random_graph");
	}

}
