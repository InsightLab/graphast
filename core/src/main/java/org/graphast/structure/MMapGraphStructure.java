package org.graphast.structure;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

import hugedataaccess.DataAccess;
import hugedataaccess.MMapDataAccess;
import hugedataaccess.structures.MMapMap;
import hugedataaccess.structures.MMapTreeMap;

public class MMapGraphStructure implements GraphStructure {
	
	private static final int NODE_SIZE = 4*8;
	private static final int EDGE_SIZE = 4*16;
	
	private long nodePos = 0;
	private long edgePos = 0;
	
	private MMapMap idMapping;
	private String path;
	private String nodesFile = "nodes.mmap";
	private String edgesFile = "edges.mmap";
	
	private DataAccess nodeAccess;
	private DataAccess edgeAccess;
	
	public MMapGraphStructure(String path) {
		if (!path.endsWith("/")) path += "/";
		idMapping = new MMapTreeMap(path);
		this.path = path;
		File f = new File(this.path);
		boolean graphExists = f.exists();
		if (!graphExists) f.mkdirs();
		try {
			nodeAccess = new MMapDataAccess(path + nodesFile, 1024*1024*32l);
			edgeAccess = new MMapDataAccess(path + edgesFile, 1024*1024*32l);
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
	
	private long getNodeIndex(long id) {
		return 8 + id*NODE_SIZE;
	}
	
	private long getEdgeIndex(long id) {
		return 8 + id*EDGE_SIZE;
	}

	@Override
	public long getNumberOfNodes() {
		return nodeAccess.getLong(0);
	}

	@Override
	public long getNumberOfEdges() {
		return edgeAccess.getLong(0);
	}

	@Override
	public void addNode(Node n) {
		if (nodeAccess.getCapacity() < getNodeIndex(nodePos) + NODE_SIZE)
			nodeAccess.ensureCapacity(nodeAccess.getCapacity() + 1024*1024*32l);
		
		idMapping.put(n.getId(), nodePos);
		
		long nodeIndex = getNodeIndex(nodePos);
		
		nodeAccess.setLong( nodeIndex      , n.getId() );
		nodeAccess.setLong( nodeIndex + 8  ,    -1l    );
		nodeAccess.setLong( nodeIndex + 16 ,    -1l    );
		
		nodeAccess.setLong( 0 , ++nodePos );
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
			edgeAccess.ensureCapacity(edgeAccess.getCapacity() + 1024*1024*32l);
		
		long fromId = idMapping.get(e.getFromNodeId());
		long toId = idMapping.get(e.getToNodeId());
		
		long fromIndex = getNodeIndex(fromId);
		long toIndex = getNodeIndex(toId);
		
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
	
	private long getExternalIdByInternalId(long internalId) {
		return nodeAccess.getLong( getNodeIndex(internalId) );
	}

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
	public boolean containsNode(long id) {
		return idMapping.containsKey(id);
	}

}
