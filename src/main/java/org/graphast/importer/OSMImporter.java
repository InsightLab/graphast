package org.graphast.importer;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;

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

		while(edgeIterator.next()) {
			
			int fromNodeId = edgeIterator.getBaseNode();
			int toNodeId = edgeIterator.getAdjNode();
			double distance = edgeIterator.getDistance();

			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(fromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(fromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(toNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(toNodeId)));			

			GraphastNode fromNode = new GraphastNode(latitudeFrom, longitudeFrom);
			GraphastNode toNode = new GraphastNode(latitudeTo, longitudeTo);
			

			
			graph.addNode(fromNode);
			graph.addNode(toNode);

			if(longitudeTo == 7.414896 || longitudeFrom == 7.421220) {
				System.out.println("fromNode: " + fromNode);
				System.out.println("LatitudeFrom: " + latitudeFrom);
				System.out.println("LongitudeFrom: " + longitudeFrom);
				System.out.println("");
				System.out.println("toNode: " + toNode);
				System.out.println("LatitudeTo: " + latitudeTo);
				System.out.println("LongitudeTo: " + longitudeTo);
			}
			
			GraphastEdge edge = new GraphastEdge(fromNode.getId(), toNode.getId(), (int)distance);
			graph.addEdge(edge);
			
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
