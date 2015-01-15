package org.graphast.importer;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

import java.io.IOException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.GraphHopper;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.EdgeIterator;

public class OSMImporter {

	public Logger logger = LoggerFactory.getLogger(this.getClass());

	public Graphast execute(String osmFile, String graphHopperDir, String graphastDir) {

		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		Graphast graph = new GraphastImpl(graphastDir);

		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
		GraphStorage gs = gh.getGraph();
		EdgeIterator edgeIterator = gs.getAllEdges();

		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		//TODO Check if the edge is bidirectional or not
		while(edgeIterator.next()) {
			count++;
			System.out.println("edgeIteratorId: " + edgeIterator.getEdge());
			System.out.println("edgeIteratorFrom: " + edgeIterator.getBaseNode());
			System.out.println("edgeIteratorTo: " + edgeIterator.getAdjNode());
			System.out.println("egdeCost: " + edgeIterator.getDistance());
			System.out.println("edgeDirection: " + getDirection(edgeIterator.getFlags()));
			System.out.println("\n");

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			double distance = edgeIterator.getDistance();

			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalFromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalFromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalToNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalToNodeId)));			

			GraphastNode fromNode, toNode;

			long fromNodeId, toNodeId;


			if(!hashExternalIdToId.containsKey(externalFromNodeId)){

				fromNode = new GraphastNode(externalFromNodeId, latitudeFrom, longitudeFrom);
				graph.addNode(fromNode);
				fromNodeId = (long)fromNode.getId();
				hashExternalIdToId.put(externalFromNodeId, fromNodeId);

			} else {

				fromNodeId = hashExternalIdToId.get(externalFromNodeId);

			}

			if(!hashExternalIdToId.containsKey(externalToNodeId)){

				toNode = new GraphastNode(externalToNodeId, latitudeTo, longitudeTo);
				graph.addNode(toNode);
				toNodeId = (long)toNode.getId();
				hashExternalIdToId.put(externalToNodeId, toNodeId);

			} else {

				toNodeId = hashExternalIdToId.get(externalToNodeId);

			}

			int direction = getDirection(edgeIterator.getFlags());
			if(direction == 0) {
				if(fromNodeId != toNodeId) {
					GraphastEdge edge = new GraphastEdge(externalEdgeId, fromNodeId, toNodeId, (int)distance);
					graph.addEdge(edge);
					edge = new GraphastEdge(externalEdgeId, toNodeId, fromNodeId, (int)distance);
					graph.addEdge(edge);
				}
			}else if(direction == 1) {
				GraphastEdge edge = new GraphastEdge(externalEdgeId, fromNodeId, toNodeId, (int)distance);
				graph.addEdge(edge);
			}else {
				GraphastEdge edge = new GraphastEdge(externalEdgeId, toNodeId, fromNodeId, (int)distance);
				graph.addEdge(edge);
			}
		}


		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
		logger.info("Count: {}", count);

		try {
			graph.save();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);

		return graph;
	}

	public static void main(String[] args) {
		String osmFile = Configuration.getProperty("berlin.osm.file");
		String graphHopperDir = Configuration.getProperty("berlin.graphhopper.dir");
		String graphastDir = Configuration.getProperty("berlin.graphast.dir");
		new OSMImporter().execute(osmFile, graphHopperDir, graphastDir);

	}

	public int getDirection(long flags) {
		long direction = (flags & 3);

		if(direction ==  1) {
			return 1; 
		} else if(direction ==  2) {
			return -1;
		} else if(direction == 3) {
			return 0;
		}
		else {
			throw new IllegalArgumentException("Invalid flag");
		}
	}
}
