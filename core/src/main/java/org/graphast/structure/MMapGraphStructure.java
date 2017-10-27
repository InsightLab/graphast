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
	
	private static final int NODE_SIZE = 4*3;
	private static final int EDGE_SIZE = 2*4 + 8 + 2*4;
	
	private int nodePos = 0;
	private int edgePos = 0;
	
	private Map<Integer, Integer> idMapping = new HashMap<>();
	private String path;
	private String nodesFile = "nodes.mmap";
	private String edgesFile = "edges.mmap";
	
	private DataAccess nodeAccess;
	private DataAccess edgeAccess;
	
	public MMapGraphStructure(String graphName) {
		this.path = graphName + "/";
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
					idMapping.put(nodeAccess.getInt(getNodeIndex(i)), i);
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
		if (nodeAccess.getCapacity() < (nodePos+1)*NODE_SIZE)
			nodeAccess.ensureCapacity(nodeAccess.getCapacity() + 1024*1024l);
		
		idMapping.put(n.getId(), nodePos);
		
		long nodeIndex = getNodeIndex(nodePos);
		
		nodeAccess.setInt( nodeIndex     , n.getId() );
		nodeAccess.setInt( nodeIndex + 4 ,    -1     );
		nodeAccess.setInt( nodeIndex + 8 ,    -1     );
		
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
		if (edgeAccess.getCapacity() < (edgePos+1)*EDGE_SIZE)
			edgeAccess.ensureCapacity(edgeAccess.getCapacity() + 1024*1024l);
		
		int fromId = idMapping.get(e.getFromNodeId());
		int toId = idMapping.get(e.getToNodeId());
		long fromIndex = getNodeIndex(fromId);
		long toIndex = getNodeIndex(toId);
		
		long edgeIndex = getEdgeIndex(edgePos);
		
		edgeAccess.setInt    ( edgeIndex      , fromId );
		edgeAccess.setInt    ( edgeIndex + 4  , toId );
		edgeAccess.setDouble ( edgeIndex + 8  , e.getCost() );
		edgeAccess.setInt    ( edgeIndex + 16 , nodeAccess.getInt(fromIndex + 4) );
		edgeAccess.setInt    ( edgeIndex + 20 , nodeAccess.getInt(toIndex + 8) );
		
		nodeAccess.setInt    ( fromIndex + 4 , edgePos);
		nodeAccess.setInt    ( toIndex   + 8 , edgePos);
		
		edgeAccess.setInt( 0 , ++edgePos );
	}
	
	private int getExternalIdByInternalId(int internalId) {
		return nodeAccess.getInt( getNodeIndex(internalId) );
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
				int from    = edgeAccess.getInt    ( getEdgeIndex(pos)     );
				int to      = edgeAccess.getInt    ( getEdgeIndex(pos) + 4 );
				double cost = edgeAccess.getDouble ( getEdgeIndex(pos) + 8 );
				
				pos++;
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

	@Override
	public Iterator<Edge> getOutEdges(final int id) {
		return new Iterator<Edge>() {
			
			int pos = nodeAccess.getInt( getNodeIndex(id) + 4 );

			@Override
			public boolean hasNext() {
				return pos != -1;
			}

			@Override
			public Edge next() {
				int from    = edgeAccess.getInt    ( getEdgeIndex(pos)     );
				int to      = edgeAccess.getInt    ( getEdgeIndex(pos) + 4 );
				double cost = edgeAccess.getDouble ( getEdgeIndex(pos) + 8 );
				
				pos = edgeAccess.getInt( getEdgeIndex(pos) + 16 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

	@Override
	public Iterator<Edge> getInEdges(final int id) {
		return new Iterator<Edge>() {
			
			int pos = nodeAccess.getInt( getNodeIndex(id) + 8 );

			@Override
			public boolean hasNext() {
				return pos != -1;
			}

			@Override
			public Edge next() {
				int from    = edgeAccess.getInt    ( getEdgeIndex(pos)     );
				int to      = edgeAccess.getInt    ( getEdgeIndex(pos) + 4 );
				double cost = edgeAccess.getDouble ( getEdgeIndex(pos) + 8 );
				
				pos = edgeAccess.getInt( getEdgeIndex(pos) + 20 );
				
				return new Edge(getExternalIdByInternalId(from), getExternalIdByInternalId(to), cost);
			}
		};
	}

}
