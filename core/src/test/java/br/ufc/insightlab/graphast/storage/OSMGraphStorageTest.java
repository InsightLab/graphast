package br.ufc.insightlab.graphast.storage;


import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import br.ufc.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import br.ufc.insightlab.graphast.query.utils.DistanceVector;
import br.ufc.insightlab.graphast.storage.GraphStorage;
import br.ufc.insightlab.graphast.storage.GraphStorageFactory;
import br.ufc.insightlab.graphast.storage.StorageUtils;
import br.ufc.insightlab.graphast.structure.MMapGraphStructure;

public class OSMGraphStorageTest {
	
	private static GraphStorage storage;
	private static Graph g;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StorageUtils.deleteMMapGraph("graphs/MMap/monaco");
		storage = GraphStorageFactory.getOSMGraphStorage();
		String path = OSMGraphStorageTest.class.getClassLoader().getResource("monaco-latest.osm.pbf").getPath();
		g = storage.load(path, new MMapGraphStructure("graphs/MMap/monaco"));
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
