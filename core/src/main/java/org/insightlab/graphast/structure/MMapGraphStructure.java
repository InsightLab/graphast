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

package org.insightlab.graphast.structure;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;
import org.insightlab.hugedataaccess.DataAccess;
import org.insightlab.hugedataaccess.MMapDataAccess;
import org.insightlab.hugedataaccess.structures.MMapMap;
import org.insightlab.hugedataaccess.structures.MMapTreeMap;

/**
 * This class implements a MMap graph structure using the interface GraphStructure.
 *
 */
public class MMapGraphStructure implements GraphStructure {
	
	private static final int NODE_SIZE = 4*8;
	private static final int EDGE_SIZE = 4*16;
	
	private static final long INITIAL_NODE_FILE_SIZE = 1024*1024*8l;
	private static final long INITIAL_EDGE_FILE_SIZE = 1024*1024*8l;
	
	private long nodePos = 0;
	private long edgePos = 0;
	
	private MMapMap idMapping;
	private String nodesFile = "nodes.mmap";
	private String edgesFile = "edges.mmap";
	
	private DataAccess nodeAccess;
	private DataAccess edgeAccess;
	
	/**
	 * Create a new MMapGraphStructure for the given path.
	 * @param path the graph's path where the graph will be read or saved.
	 */
	public MMapGraphStructure(String path) {
		
		String directory = path;
		
		if (!directory.endsWith("/")) 
			directory += "/";
		
		idMapping = new MMapTreeMap(directory);
		File f    = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			
			nodeAccess = new MMapDataAccess(directory + nodesFile, INITIAL_NODE_FILE_SIZE);
			edgeAccess = new MMapDataAccess(directory + edgesFile, INITIAL_EDGE_FILE_SIZE);
			
			if (graphExists) {
				
				nodePos = getNumberOfNodes();
				edgePos = getNumberOfEdges();
				
				for (long i = 0; i < nodePos; i++) {
					idMapping.put(nodeAccess.getLong(getNodeIndex(i)), i);
				}	
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * returns the index of the node which has the given id.
	 * @param id the node's id.
	 */
	private long getNodeIndex(long id) {
		return 8 + id*NODE_SIZE;
	}
	
	/**
	 * returns the index of the edge which has the given id.
	 * @param id the edge's id.
	 */
	private long getEdgeIndex(long id) {
		return 8 + id*EDGE_SIZE;
	}
	
	/**
	 * returns the external id of the node which has the given internal id.
	 * @param internalId the node's internal id.
	 */
	private long getExternalIdByInternalId(long internalId) {
		return nodeAccess.getLong( getNodeIndex(internalId) );
	}
	
	/**
	 * Add a new directional edge into the graph.
	 * @param e the edge that will be added into the graph.
	 */
	private void addDirectionalEdge(Edge e) {
		
		if (edgeAccess.getCapacity() < getEdgeIndex(edgePos) + EDGE_SIZE)
			edgeAccess.ensureCapacity(edgeAccess.getCapacity() + INITIAL_EDGE_FILE_SIZE);
		
		long fromId = idMapping.get(e.getFromNodeId());
		long toId   = idMapping.get(e.getToNodeId());
		
		long fromIndex = getNodeIndex(fromId);
		long toIndex   = getNodeIndex(toId);
		
		long edgeIndex = getEdgeIndex(edgePos);
		
		edgeAccess.setLong   ( edgeIndex      , fromId );
		edgeAccess.setLong   ( edgeIndex + 8  , toId );
		edgeAccess.setDouble ( edgeIndex + 16 , e.getCost() );
		edgeAccess.setLong   ( edgeIndex + 24 , nodeAccess.getLong(fromIndex + 8) );
		edgeAccess.setLong   ( edgeIndex + 32 , nodeAccess.getLong(toIndex + 16) );
		
		nodeAccess.setLong( fromIndex + 8  , edgePos);
		nodeAccess.setLong( toIndex   + 16 , edgePos);
		
		edgeAccess.setLong( 0 , ++edgePos );
		
	}
	
	/**
	 * Add a new node into the graph.
	 * @param n the node that will be added.
	 */
	@Override
	public void addNode(Node n) {
		if (nodeAccess.getCapacity() < getNodeIndex(nodePos) + NODE_SIZE)
			nodeAccess.ensureCapacity(nodeAccess.getCapacity() + INITIAL_NODE_FILE_SIZE);
		idMapping.put(n.getId(), nodePos);
		
		long nodeIndex = getNodeIndex(nodePos);
		
		nodeAccess.setLong( nodeIndex      , n.getId() );
		nodeAccess.setLong( nodeIndex + 8  ,    -1l    );
		nodeAccess.setLong( nodeIndex + 16 ,    -1l    );
		
		nodeAccess.setLong( 0 , ++nodePos );
		
	}

	/**
	 * Add a new edge into the graph.
	 * @param e the edge that will be added into the graph.
	 */
	@Override
	public void addEdge(Edge e) {
		
		addDirectionalEdge(e);
		
		if (e.isBidirectional())
			addDirectionalEdge(new Edge(e.getToNodeId(), e.getFromNodeId(), e.getCost()));
	}

	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 */
	@Override
	public boolean containsNode(long id) {
		return idMapping.containsKey(id);
	}
	
	/**
	 * @return an iterator to graph's nodes.
	 */
	@Override
	public Iterator<Node> nodeIterator() {
		
		return new Iterator<Node>() {
			
			long id = 0;

			@Override
			public boolean hasNext() {
				return id < getNumberOfNodes();
			}

			@Override
			public Node next() {
				return new Node(getExternalIdByInternalId(id++));
			}
			
			@Override
			public void remove() {
				Iterator.super.remove();
			}
		};
		
	}

	/**
	 * @return an iterator to graph's edges.
	 */
	@Override
	public Iterator<Edge> edgeIterator() {
		
		return new Iterator<Edge>() {
			
			long pos = 0;

			@Override
			public boolean hasNext() {
				return pos < getNumberOfEdges();
			}

			@Override
			public Edge next() {
				
				long edgeIndex = getEdgeIndex(pos);
				
				long from   = edgeAccess.getLong   ( edgeIndex      );
				long to     = edgeAccess.getLong   ( edgeIndex + 8  );
				double cost = edgeAccess.getDouble ( edgeIndex + 16 );
				
				pos++;
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
				
			}
			
			@Override
			public void remove() {
				Iterator.super.remove();
			}
		};
		
	}

	/**
	 * @return the number of graph's nodes.
	 */
	@Override
	public long getNumberOfNodes() {
		return nodeAccess.getLong(0);
	}

	/**
	 * @return the number of graph's edges.
	 */
	@Override
	public long getNumberOfEdges() {
		return edgeAccess.getLong(0);
	}
	
	/**
	 * @param id the node's id.
	 * @return the out edges of the node which has the given id.
	 */
	@Override
	public Iterator<Edge> getOutEdges(final long id) {
		
		return new Iterator<Edge>() {
			
			long pos = nodeAccess.getLong( getNodeIndex(idMapping.get(id)) + 8 );

			@Override
			public boolean hasNext() {
				return pos != -1l;
			}

			@Override
			public Edge next() {
				
				long edgeIndex = getEdgeIndex(pos);
				
				long from   = edgeAccess.getLong   ( edgeIndex      );
				long to     = edgeAccess.getLong   ( edgeIndex + 8  );
				double cost = edgeAccess.getDouble ( edgeIndex + 16 );
				
				pos = edgeAccess.getLong( edgeIndex + 24 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
				
			}
			
			@Override
			public void remove() {
				Iterator.super.remove();
			}
		};
		
	}

	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	@Override
	public Iterator<Edge> getInEdges(final long id) {
		
		return new Iterator<Edge>() {
			
			long pos = nodeAccess.getLong( getNodeIndex(idMapping.get(id)) + 16 );

			@Override
			public boolean hasNext() {
				return pos != -1l;
			}

			@Override
			public Edge next() {
				
				long edgeIndex = getEdgeIndex(pos);

				long from   = edgeAccess.getLong   ( edgeIndex      );
				long to     = edgeAccess.getLong   ( edgeIndex + 8  );
				double cost = edgeAccess.getDouble ( edgeIndex + 16 );
				
				pos = edgeAccess.getLong( edgeIndex + 32 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
				
			}
			
			@Override
			public void remove() {
				Iterator.super.remove();
			}
		};
		
	}

	@Override
	public Set<Class<? extends GraphComponent>> getAllComponentClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphComponent getComponent(Class<? extends GraphComponent> componentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addComponent(GraphComponent component) {
		// TODO Auto-generated method stub
		
	}


}
