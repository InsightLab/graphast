package org.graphast.storage;


import static org.junit.Assert.assertEquals;

import org.graphast.model.Graph;
import org.graphast.query.shortestpath.DijkstraStrategy;
import org.graphast.query.shortestpath.ShortestPathStrategy;
import org.graphast.query.utils.DistanceVector;
import org.graphast.structure.MMapGraphStructure;
import org.junit.BeforeClass;
import org.junit.Test;

public class OSMGraphStorageTest {
	
	private static GraphStorage storage;
	private static Graph g;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		storage = GraphStorageFactory.getOSMGraphStorage();
		g = storage.loadGraph("monaco-latest.osm.pbf", new MMapGraphStructure("monaco"));
	}

	@Test
	public void test() {
		ShortestPathStrategy strategy = new DijkstraStrategy(g);
		DistanceVector vector = strategy.run(1321688739l);
		vector.print(1321688739l, 5113919675l);
		assertEquals("OSM dijkstra test", 6, vector.getDistance(5113919675l), 0);
		assertEquals("OSM dijkstra test", 3, vector.getDistance(1321688478l), 0);
	}

}
