package br.ufc.insightlab.graphast.storage;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufc.insightlab.graphast.exceptions.OSMFormatNotSupported;
import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import br.ufc.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import br.ufc.insightlab.graphast.query.utils.DistanceVector;
import br.ufc.insightlab.graphast.storage.GraphStorage;
import br.ufc.insightlab.graphast.storage.GraphStorageFactory;
import br.ufc.insightlab.graphast.storage.StorageUtils;
import br.ufc.insightlab.graphast.structure.DefaultGraphStructure;
import br.ufc.insightlab.graphast.structure.MMapGraphStructure;

public class OSMGraphStorageTest {
	
	private static GraphStorage storage;
	private static Graph g;

//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		StorageUtils.deleteMMapGraph("graphs/MMap/monaco");
//		storage = GraphStorageFactory.getOSMGraphStorage();
//		String path = OSMGraphStorageTest.class.getClassLoader().getResource("monaco-latest.osm.pbf").getPath();
//		g = storage.load(path, new MMapGraphStructure("graphs/MMap/monaco"));
//	}

	@Test
	public void testLoadPBF() {
		storage = GraphStorageFactory.getOSMGraphStorage();
		String path = OSMGraphStorageTest.class.getClassLoader().getResource("monaco-latest.osm.pbf").getPath();
		try{
			g = storage.load(path, new DefaultGraphStructure());
			assertEquals(false, (g==null));
//			assertEquals("Error on number of edges",42057, g.getNumberOfEdges());
//			assertEquals("Error on number of nodes",40452, g.getNumberOfNodes());
		} catch(FileNotFoundException e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadBZ2() {
		storage = GraphStorageFactory.getOSMGraphStorage();
		String path = OSMGraphStorageTest.class.getClassLoader().getResource("monaco-latest.osm.bz2").getPath();
		try{
			g = storage.load(path, new DefaultGraphStructure());
			assertEquals(false, (g==null));
//			assertEquals("Error on number of edges",42057, g.getNumberOfEdges());
//			assertEquals("Error on number of nodes",40452, g.getNumberOfNodes());
		} catch(FileNotFoundException e){
			fail(e.getMessage());
		}
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testLoadFileException() throws FileNotFoundException {
		storage = GraphStorageFactory.getOSMGraphStorage();
		String path = "no.pbf";
		try{
			g = storage.load(path, new DefaultGraphStructure());

		} catch(FileNotFoundException e){
			throw e;
		}
	}
	
	@Test(expected = OSMFormatNotSupported.class)
	public void testLoadFormatException() throws FileNotFoundException {
		storage = GraphStorageFactory.getOSMGraphStorage();
		String path = OSMGraphStorageTest.class.getClassLoader().getResource("file.format").getPath();
		try{
			g = storage.load(path, new DefaultGraphStructure());

		} catch(OSMFormatNotSupported e){
			throw e;
		}
	}
	

}
