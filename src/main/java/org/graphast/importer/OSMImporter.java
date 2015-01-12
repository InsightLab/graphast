package org.graphast.importer;

import it.unimi.dsi.fastutil.BigArrays;

import java.io.IOException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.model.Graphast;
import org.graphast.model.GraphastImpl;
import org.graphast.model.GraphastEdge;
import org.graphast.model.GraphastNode;
import org.graphast.query.route.shortestpath.DijkstraShortestPathWithConstantWeight;
import org.graphast.query.route.shortestpath.GraphastAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import com.graphhopper.GraphHopper;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.EdgeIterator;

import static org.graphast.util.GeoUtils.latLongToInt;
import static org.graphast.util.GeoUtils.latLongToDouble;

public class OSMImporter {
	public Logger logger = LoggerFactory.getLogger(this.getClass());

	public void execute() {
		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		String osmFile = Configuration.getProperty("berlin.osm.file");
		String graphHopperDir = Configuration.getProperty("berlin.graphhopper.dir");
		String graphastDir = Configuration.getProperty("berlin.graphast.dir");

		Graphast fg = new GraphastImpl(graphastDir);

		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
		GraphStorage gs = gh.getGraph();
		EdgeIterator edgeIterator = gs.getAllEdges();

		while(edgeIterator.next()) {
			
			int fromNodeId = edgeIterator.getBaseNode();
			int toNodeId = edgeIterator.getAdjNode();
			double distance = edgeIterator.getDistance();

			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(fromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(fromNodeId)));	

			double latitudeTo = gs.getNodeAccess().getLatitude(toNodeId);
			double longitudeTo = gs.getNodeAccess().getLongitude(toNodeId);			

			GraphastNode fromNode = new GraphastNode(latitudeFrom, longitudeFrom);
			GraphastNode toNode = new GraphastNode(latitudeTo, longitudeTo);

			fg.addNode(fromNode);
			fg.addNode(toNode);
			
		}
		logger.info("Number of Nodes: {}", fg.getNumberOfNodes());
		logger.info("Number of Edges: {}", fg.getNumberOfEdges());

		try {
			fg.save();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);

		Long source = fg.getNode(52.535926,13.192974);
		Long destination = fg.getNode(52.535926,13.192974);
		
//		GraphastAlgorithms dj = new DijkstraShortestPathWithConstantWeight(fg, source, destination);
//		dj.execute();

	}

	public static void main(String[] args) {
		new OSMImporter().execute();

	}
}
