package org.graphast.structure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graphast.model.Edge;
import org.graphast.model.Node;

import hugedataaccess.DataAccess;
import hugedataaccess.MMapDataAccess;

public class MMapGraphStructure implements GraphStructure {
	
	private static final int NODE_SIZE = 4*4;
	private static final int EDGE_SIZE = 4*8;
	
	private int nodePos = 0;
	private int edgePos = 0;
	
	private Map<Long, Integer> idMapping = new HashMap<>();
	private String path;
	private String nodesFile = "nodes.mmap";
	private String edgesFile = "edges.mmap";
	
	private DataAccess nodeAccess;
	private DataAccess edgeAccess;
	
	public MMapGraphStructure(String path) {
		if (!path.endsWith("/")) path += "/";
		this.path = path;
		File f = new File(this.path);
		boolean graphExists = f.exists();
		if (!graphExists) f.mkdirs();
		try {
			nodeAccess = new MMapDataAccess(path + nodesFile, 1024*1024l);
			edgeAccess = new MMapDataAccess(path + edgesFile, 1024*1024l);
			if (graphExists) {
				nodePos = getNumberOfNodes();
				edgePos = getNumberOfEdges();
				for (int i = 0; i < nodePos; i++) {
					idMapping.put(nodeAccess.getLong(getNodeIndex(i)), i);
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private long getNodeIndex(int id) {
		return 4 + id*NODE_SIZE;
	}
	
	private long getEdgeIndex(int id) {
		return 4 + id*EDGE_SIZE;
	}

	@Override
	public int getNumberOfNodes() {
		return nodeAccess.getInt(0);
	}

	@Override
	public int getNumberOfEdges() {
		return edgeAccess.getInt(0);
	}

	@Override
	public void addNode(Node n) {
		if (nodeAccess.getCapacity() < getNodeIndex(nodePos) + NODE_SIZE)
			nodeAccess.ensureCapacity(nodeAccess.getCapacity() + 1024*1024l);
		
		idMapping.put(n.getId(), nodePos);
		
		long nodeIndex = getNodeIndex(nodePos);
		
		nodeAccess.setLong( nodeIndex      , n.getId() );
		nodeAccess.setInt ( nodeIndex + 8  ,    -1     );
		nodeAccess.setInt ( nodeIndex + 12 ,    -1     );
		
		nodeAccess.setInt( 0 , ++nodePos );
	}

	@Override
	public void addEdge(Edge e) {
		addDirectionalEdge(e);
		if (e.isBidirectional()) {
			e = new Edge(e.getToNodeId(), e.getFromNodeId(), e.getCost());
			addDirectionalEdge(e);
		}
	}
	
	private void addDirectionalEdge(Edge e) {
		if (edgeAccess.getCapacity() < getEdgeIndex(edgePos) + EDGE_SIZE)
			edgeAccess.ensureCapacity(edgeAccess.getCapacity() + 1024*1024l);
		
		int fromId = idMapping.get(e.getFromNodeId());
		int toId = idMapping.get(e.getToNodeId());
		
		long fromIndex = getNodeIndex(fromId);
		long toIndex = getNodeIndex(toId);
		
		long edgeIndex = getEdgeIndex(edgePos);
		
		edgeAccess.setInt    ( edgeIndex      , fromId );
		edgeAccess.setInt    ( edgeIndex + 4  , toId );
		edgeAccess.setDouble ( edgeIndex + 8  , e.getCost() );
		edgeAccess.setInt    ( edgeIndex + 16 , nodeAccess.getInt(fromIndex + 8) );
		edgeAccess.setInt    ( edgeIndex + 20 , nodeAccess.getInt(toIndex + 12) );
		
		nodeAccess.setInt    ( fromIndex + 8 , edgePos);
		nodeAccess.setInt    ( toIndex   + 12 , edgePos);
		
		edgeAccess.setInt( 0 , ++edgePos );
	}
	
	private long getExternalIdByInternalId(int internalId) {
		return nodeAccess.getLong( getNodeIndex(internalId) );
	}

	@Override
	public Iterator<Node> nodeIterator() {
		return new Iterator<Node>() {
			
			int id = 0;

			@Override
			public boolean hasNext() {
				return id < getNumberOfNodes();
			}

			@Override
			public Node next() {
				return new Node(getExternalIdByInternalId(id++));
			}
		};
	}

	@Override
	public Iterator<Edge> edgeIterator() {
		return new Iterator<Edge>() {
			
			int pos = 0;

			@Override
			public boolean hasNext() {
				return pos < getNumberOfEdges();
			}

			@Override
			public Edge next() {
				long edgeIndex = getEdgeIndex(pos);
				
				int from    = edgeAccess.getInt    ( edgeIndex     );
				int to      = edgeAccess.getInt    ( edgeIndex + 4 );
				double cost = edgeAccess.getDouble ( edgeIndex + 8 );
				
				pos++;
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

	@Override
	public Iterator<Edge> getOutEdges(final long id) {
		return new Iterator<Edge>() {
			
			int pos = nodeAccess.getInt( getNodeIndex(idMapping.get(id)) + 8 );

			@Override
			public boolean hasNext() {
				return pos != -1;
			}

			@Override
			public Edge next() {
				long edgeIndex = getEdgeIndex(pos);
				
				int from    = edgeAccess.getInt    ( edgeIndex     );
				int to      = edgeAccess.getInt    ( edgeIndex + 4 );
				double cost = edgeAccess.getDouble ( edgeIndex + 8 );
				
				pos = edgeAccess.getInt( edgeIndex + 16 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

	@Override
	public Iterator<Edge> getInEdges(final long id) {
		return new Iterator<Edge>() {
			
			int pos = nodeAccess.getInt( getNodeIndex(idMapping.get(id)) + 12 );

			@Override
			public boolean hasNext() {
				return pos != -1;
			}

			@Override
			public Edge next() {
				long edgeIndex = getEdgeIndex(pos);

				int from    = edgeAccess.getInt    ( edgeIndex     );
				int to      = edgeAccess.getInt    ( edgeIndex + 4 );
				double cost = edgeAccess.getDouble ( edgeIndex + 8 );
				
				pos = edgeAccess.getInt( edgeIndex + 20 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

}
