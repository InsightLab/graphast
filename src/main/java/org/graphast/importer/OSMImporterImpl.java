package org.graphast.importer;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

import java.io.IOException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.model.Edge;
import org.graphast.model.Graph;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphImpl;
import org.graphast.model.NodeImpl;
import org.graphast.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.GraphHopper;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.EdgeIterator;

public class OSMImporterImpl implements Importer {

	public Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String osmFile, graphHopperDir, graphastDir, graphastTmpDir;
	
	public OSMImporterImpl(String osmFile, String graphHopperDir, String graphastDir) {
		graphastTmpDir = Configuration.USER_HOME + "/graphast/tmp/osmimporter";
		this.osmFile = osmFile;
		if (graphHopperDir == null) {
			this.graphHopperDir = graphastTmpDir;
		} else {
			this.graphHopperDir = graphHopperDir;
		}
	}
	
	public OSMImporterImpl(String osmFile, String graphastDir) {
		this(osmFile, null, graphastDir);
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.importer.OSMImporter#execute()
	 */
	@Override
	public Graph execute() {

		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		Graph graph = new GraphImpl(graphastDir);

		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
		GraphStorage gs = gh.getGraph();
		EdgeIterator edgeIterator = gs.getAllEdges();

		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		int countInvalidDirection = 0;
		while(edgeIterator.next()) {
			count++;

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			int distance = (int)(edgeIterator.getDistance() * 1000); // Convert distance from meters to millimeters
			String label = edgeIterator.getName();
			
			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalFromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalFromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalToNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalToNodeId)));			

			NodeImpl fromNode, toNode;

			long fromNodeId, toNodeId;

			if(!hashExternalIdToId.containsKey(externalFromNodeId)){

				fromNode = new NodeImpl(externalFromNodeId, latitudeFrom, longitudeFrom);
				graph.addNode(fromNode);
				fromNodeId = (long)fromNode.getId();
				hashExternalIdToId.put(externalFromNodeId, fromNodeId);
			} else {
				fromNodeId = hashExternalIdToId.get(externalFromNodeId);
			}

			if(!hashExternalIdToId.containsKey(externalToNodeId)){
				toNode = new NodeImpl(externalToNodeId, latitudeTo, longitudeTo);
				graph.addNode(toNode);
				toNodeId = (long)toNode.getId();
				hashExternalIdToId.put(externalToNodeId, toNodeId);
			} else {
				toNodeId = hashExternalIdToId.get(externalToNodeId);
			}

			int direction = 9999;
			try {
				direction = getDirection(edgeIterator.getFlags());
			} catch (Exception e) {
				countInvalidDirection++;
			}
			
			if(fromNodeId == toNodeId) {
				logger.info("Edge not created, because fromNodeId({}) == toNodeId({})", fromNodeId, toNodeId);
				continue;
			}
			
			if(direction == 0) {          // Bidirectional
				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label);
				graph.addEdge(edge);
				edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label);
				graph.addEdge(edge);
			} else if(direction == 1) {   // One direction: base -> adj
				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label);
				graph.addEdge(edge);
			} else if(direction == -1) {  // One direction: adj -> base
				Edge edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label);
				graph.addEdge(edge);
			} else {
				logger.info("Edge not created. Invalid direction: {}", direction);
			}
		}

		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
		logger.info("Count: {}", count);
		logger.info("Number of invalid direction in original edges: {}", countInvalidDirection);

		try {
			graph.save();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);
		
		if (graphHopperDir.equals(graphastTmpDir)) {
			FileUtils.deleteDir(graphastTmpDir);
		}
		
		return graph;
	}

	public int getDirection(long flags) {
		long direction = (flags & 3);

		if(direction ==  1) {
			return 1;   // One direction: From --> To 
		} else if(direction ==  2) {
			return -1;  // One direction: To --> From
		} else if(direction == 3) {
			return 0;   // Bidirectional: To <--> From
		}
		else {
			throw new IllegalArgumentException("Invalid flag: " + direction);
		}
	}

	public String getOsmFile() {
		return osmFile;
	}

	public void setOsmFile(String osmFile) {
		this.osmFile = osmFile;
	}


	public String getGraphHopperDir() {
		return graphHopperDir;
	}

	public void setGraphHopperDir(String graphHopperDir) {
		this.graphHopperDir = graphHopperDir;
	}

	public String getGraphastDir() {
		return graphastDir;
	}

	public void setGraphastDir(String graphastDir) {
		this.graphastDir = graphastDir;
	}
	
}
