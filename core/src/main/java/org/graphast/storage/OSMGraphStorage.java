package org.graphast.storage;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.structure.GraphStructure;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import crosby.binary.osmosis.OsmosisReader;

public class OSMGraphStorage implements GraphStorage {
	
	private static GraphStorage instance = null;
	
	static GraphStorage getInstance() {
		if (instance == null) instance = new OSMGraphStorage();
		return instance;
	}
	
	private OSMGraphStorage() {}

	@Override
	public Graph load(String path, GraphStructure structure) {
		File file = new File(path); // the input file
		
		Graph g = new Graph(structure);

		Sink sinkImplementation = new MySink(g);

		boolean pbf = false;
		CompressionMethod compression = CompressionMethod.None;

		if (file.getName().endsWith(".pbf")) {
		    pbf = true;
		} else if (file.getName().endsWith(".gz")) {
		    compression = CompressionMethod.GZip;
		} else if (file.getName().endsWith(".bz2")) {
		    compression = CompressionMethod.BZip2;
		}
		
		try {

			RunnableSource reader;
	
			if (pbf) {
				reader = new OsmosisReader(new FileInputStream(file));
			} else {
			    reader = new XmlReader(file, false, compression);
			}
	
			reader.setSink(sinkImplementation);
	
			Thread readerThread = new Thread(reader);
			readerThread.start();
	
			while (readerThread.isAlive()) {
				readerThread.join();
			}
		
		} catch (InterruptedException|FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return g;
	}

	@Override
	public void save(String path, Graph g) {
		// TODO Auto-generated method stub
		
	}
	
	private class MySink implements Sink {
		
		private Graph g;
		
		public MySink(Graph g) {
			this.g = g;
		}
		
		public void process(EntityContainer entityContainer) {
	        Entity entity = entityContainer.getEntity();
	        if (entity instanceof Node) {
	        	long id = ((Node)entity).getId();
	            g.addNode(new org.graphast.model.Node(id));
	        } else if (entity instanceof Way) {
	        	Way w = (Way) entity;
	        	List<WayNode> wayNodeList = w.getWayNodes();
	        	for (int i = 0; i < wayNodeList.size()-1; i++) {
	        		long idFrom = wayNodeList.get(i).getNodeId();
	        		long idTo = wayNodeList.get(i+1).getNodeId();
	        		if (idFrom != idTo) {
		        		g.addEdge(new Edge(idFrom, idTo));
	        		}
	        	}
	        }
	    }
	    public void complete() {}
		public void initialize(Map<String, Object> arg0) {}
		public void close() {}
		
	}

}
