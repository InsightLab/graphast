/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package org.insightlab.graphast.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import crosby.binary.osmosis.OsmosisReader;

import org.insightlab.graphast.exceptions.OSMFormatNotSupported;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.structure.GraphStructure;

public class OSMGraphStorage implements GraphStorage {
	
	private static GraphStorage instance = null;
	
	private OSMGraphStorage() {}
	
	public static GraphStorage getInstance() {
		if (instance == null) instance = new OSMGraphStorage();
		
		return instance;
	}

	@Override
	public Graph load(String path, GraphStructure structure) throws FileNotFoundException {
		
		File file = new File(path); // the input file
		
		if(!file.exists())
			throw new FileNotFoundException("File " + path + " not exists");
		
		Graph graph = new Graph(structure);
	
		Sink sinkImplementation = new MySink(graph);

		boolean pbf = false;
		
		CompressionMethod compression = CompressionMethod.None;

		if (file.getName().endsWith(".pbf")) {
		    pbf = true;
		} else if (file.getName().endsWith(".bz2")) {
		    compression = CompressionMethod.BZip2;
		} else {
			sinkImplementation.close();
			throw new OSMFormatNotSupported("Format of file" + file.getName() + " not supported");
		}
	
		RunnableSource reader;

		if (pbf) {
			reader = new OsmosisReader(new FileInputStream(file));
		} else {
		    reader = new XmlReader(file, false, compression);
		}

		reader.setSink(sinkImplementation);

		Thread readerThread = new Thread(reader);
		
		readerThread.start();
		
		try {
			
			while (readerThread.isAlive()) {
				readerThread.join();
			}
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();
			graph = null;
			
		}
		
		return graph;
	}

	@Override
	public void save(String path, Graph graph) {
		// TODO Auto-generated method stub
		
	}
	
	private class MySink implements Sink {
		
		private Graph graph;
		// private long counter = 0;
		
		public MySink(Graph graph) {
			this.graph = graph;
		}
		
		public void process(EntityContainer entityContainer) {
			
	        Entity entity = entityContainer.getEntity();
	        
	        if (entity instanceof Way) {
	        	Way w = (Way) entity;
	        	List<WayNode> wayNodeList = w.getWayNodes();
	        	
	        	if (wayNodeList.size() < 2 || w.getTags().isEmpty()) return;
	        	
	        	if(!graph.containsNode(wayNodeList.get(0).getNodeId()))
	        		graph.addNode(wayNodeList.get(0).getNodeId());
	        	
	        	for (int i = 1; i < wayNodeList.size(); i++) {
	        		
	        		if(!graph.containsNode(wayNodeList.get(i).getNodeId()))
	        			graph.addNode(wayNodeList.get(i).getNodeId());
	        		
	        		WayNode from = wayNodeList.get(i - 1);
	        		WayNode to   = wayNodeList.get(i);
	        		
	        		// if ((counter++)%1000 == 0) System.out.println(from.getNodeId());
	        		graph.addEdge(new Edge(from.getNodeId(), to.getNodeId()));
	        	
	        	}
        		
	        }
	        
	    }
		
		public void initialize(Map<String, Object> arg0) { System.out.println("Reading OSM data"); }
		
		public void complete() { System.out.println("Finished reading OSM"); }
		
		public void close() { System.out.println("Closing Task"); }
		
	}

}
