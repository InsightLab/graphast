package org.graphast.importer;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

import java.io.IOException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastNode;
import org.graphast.query.route.shortestpath.AbstractShortestPathService;
import org.graphast.query.route.shortestpath.DijkstraShortestPathConstantWeight;
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
		
		//TODO Check if the edge is bidirectional or not
		while(edgeIterator.next()) {
			
			int fromNodeId = edgeIterator.getBaseNode();
			int toNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			double distance = edgeIterator.getDistance();
			

			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(fromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(fromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(toNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(toNodeId)));			

			GraphastNode fromNode, toNode;
			
			
			Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
			
			if(!hashExternalIdToId.containsKey(fromNodeId)){
				
				fromNode = new GraphastNode(fromNodeId, latitudeFrom, longitudeFrom);
				graph.addNode(fromNode);
				hashExternalIdToId.put(fromNodeId, (long)fromNode.getId());
			
			} else {
				
				hashExternalIdToId.get(fromNodeId);
				
				long existingIdFrom = graph.getNodeId(latLongToInt(gs.getNodeAccess().getLatitude(fromNodeId)), latLongToInt(gs.getNodeAccess().getLongitude(fromNodeId)));
				//TODO Must be changed.
				fromNode = graph.getNode(hashExternalIdToId.get(fromNodeId));
			
			}
			
			if(graph.getNode(gs.getNodeAccess().getLatitude(toNodeId), gs.getNodeAccess().getLongitude(toNodeId))==null){
				
				toNode = new GraphastNode(toNodeId, latitudeTo, longitudeTo);
				graph.addNode(toNode);
			
			} else {
				
				long existingIdTo = graph.getNodeId(latLongToInt(gs.getNodeAccess().getLatitude(toNodeId)), latLongToInt(gs.getNodeAccess().getLongitude(toNodeId)));
				toNode = graph.getNode(existingIdTo);
				
			}
			
			GraphastEdge edge = new GraphastEdge(externalEdgeId, fromNode.getId(), toNode.getId(), (int)distance);
			graph.addEdge(edge);
			
			long conditional = edge.getId();
			System.out.println("edgeId: " + edge.getId());
			System.out.println("edgeFrom: " + edge.getFromNode());
			System.out.println("edgeTo: " + edge.getToNode());
			System.out.println("\n");
			
		}
		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());

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
}
