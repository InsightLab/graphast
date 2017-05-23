package org.graphast.importer;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.geometry.Point;
import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphImpl;
import org.graphast.model.NodeImpl;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHGraphImpl;
import org.graphast.model.contraction.CHNodeImpl;
import org.graphast.util.FileUtils;
import org.graphast.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.GraphHopper;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

public class OSMImporterImpl implements Importer {

	public Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String osmFile, graphHopperDir, graphastDir, graphastTmpDir;
	
	public OSMImporterImpl(String osmFile, String graphHopperDir, String graphastDir) {
		graphastTmpDir = Configuration.USER_HOME + "/graphast/tmp/osmimporter";
		this.osmFile = osmFile;
		if (graphHopperDir == null) {
			this.graphHopperDir = graphastTmpDir;
			FileUtils.deleteDir(graphastTmpDir);
		} else {
			this.graphHopperDir = graphHopperDir;
		}
		this.graphastDir = graphastDir;
	}
	
	public OSMImporterImpl(String osmFile, String graphastDir) {
		this(osmFile, null, graphastDir);
	}
	
	public OSMImporterImpl() {
		
	}
	
	@Override
	public CHGraph executeNative() {
		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		CHGraph graph = new CHGraphImpl();
		GraphHopper gh = new GraphHopper().forDesktop();
		
		
		GraphStorage gs = gh.getGraph();
		
		gs.edge(0, 1, 1, true);
        gs.edge(0, 2, 1.4, false);
        gs.edge(2, 5, 1.4, false);
        gs.edge(1, 2, 1, true);
        gs.edge(1, 3, 3, true);
        gs.edge(2, 3, 1, true);
        gs.edge(4, 3, 1, true);
        gs.edge(2, 5, 1.4, true);
        gs.edge(3, 5, 1, true);
        gs.edge(5, 6, 1, true);
        gs.edge(4, 6, 1, true);
        gs.edge(6, 7, 1, true);
        
		EdgeIterator edgeIterator = gs.getAllEdges();

		
		
		
		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		int countInvalidDirection = 0;
		int countBidirectional = 0;
		int countOneWay = 0;
		int countOneWayInverse = 0;
		while(edgeIterator.next()) {
			count++;

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			int distance = (int)NumberUtils.round(edgeIterator.getDistance() * 1000, 0); // Convert distance from meters to millimeters
			String label = edgeIterator.getName();
			
			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalFromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalFromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalToNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalToNodeId)));			

			CHNodeImpl fromNode, toNode;

			long fromNodeId, toNodeId;

			if(!hashExternalIdToId.containsKey(externalFromNodeId)){

				fromNode = new CHNodeImpl(externalFromNodeId, latitudeFrom, longitudeFrom);
				graph.addNode(fromNode);
				fromNodeId = (long)fromNode.getId();
				hashExternalIdToId.put(externalFromNodeId, fromNodeId);
			} else {
				fromNodeId = hashExternalIdToId.get(externalFromNodeId);
			}

			if(!hashExternalIdToId.containsKey(externalToNodeId)){
				toNode = new CHNodeImpl(externalToNodeId, latitudeTo, longitudeTo);
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
			
			//geometry
			PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
			List<Point> geometry = new ArrayList<Point>();
			for(int i =0; i < pl.size(); i++) {
				Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
				geometry.add(p);
			}
			
			if(direction == 0) {          // Bidirectional
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countBidirectional++;
				
			} else if(direction == 1) {   // One direction: base -> adj
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWay++;
			} else if(direction == -1) {  // One direction: adj -> base
				CHEdge edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWayInverse++;
			} else {
				logger.info("Edge not created. Invalid direction: {}", direction);
			}
		}

		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
		logger.info("Count: {}", count);
		logger.info("Number of invalid direction in original edges: {}", countInvalidDirection);
		logger.info("Number of Bidirectional edges: {}", countBidirectional);
		logger.info("Number of OneWay edges: {}", countOneWay);
		logger.info("Number of OneWayInverse edges: {}", countOneWayInverse);

		graph.save();

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);
		
		if (graphHopperDir.equals(graphastTmpDir)) {
			FileUtils.deleteDir(graphastTmpDir);
		}
		
		return graph;
	}
	
	/* (non-Javadoc)
	 * @see org.graphast.importer.OSMImporter#execute()
	 */
	@Override
	public GraphBounds execute() {
		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
		return execute(gh);
	}

	public GraphBounds execute(GraphHopper gh) {
		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		GraphBounds graph = new GraphImpl(graphastDir);


		GraphStorage gs = gh.getGraph();
		EdgeIterator edgeIterator = gs.getAllEdges();

		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		int countInvalidDirection = 0;
		int countBidirectional = 0;
		int countOneWay = 0;
		int countOneWayInverse = 0;
		while(edgeIterator.next()) {
			count++;

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			int distance = (int)NumberUtils.round(edgeIterator.getDistance() * 1000, 0); // Convert distance from meters to millimeters
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
			
			//geometry
			PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
			List<Point> geometry = new ArrayList<Point>();
			for(int i =0; i < pl.size(); i++) {
				Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
				geometry.add(p);
			}
			
			if(direction == 0) {          // Bidirectional
				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countBidirectional++;
				
			} else if(direction == 1) {   // One direction: base -> adj
				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWay++;
			} else if(direction == -1) {  // One direction: adj -> base
				Edge edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWayInverse++;
			} else {
				logger.info("Edge not created. Invalid direction: {}", direction);
			}
		}

		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
		logger.info("Count: {}", count);
		logger.info("Number of invalid direction in original edges: {}", countInvalidDirection);
		logger.info("Number of Bidirectional edges: {}", countBidirectional);
		logger.info("Number of OneWay edges: {}", countOneWay);
		logger.info("Number of OneWayInverse edges: {}", countOneWayInverse);

		graph.save();

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);
		
		if (graphHopperDir.equals(graphastTmpDir)) {
			FileUtils.deleteDir(graphastTmpDir);
		}
		
		return graph;
	}
	
	@Override
	public CHGraph executeCH() {
		
		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
		return executeCH(gh);
		
	}
	
	public CHGraph executeCH(GraphHopper gh) {

		logger.info("Initial date: {}", new Date());
		double initialTime = System.currentTimeMillis();

		CHGraph graph = new CHGraphImpl(graphastDir);

		GraphStorage gs = gh.getGraph();
		EdgeIterator edgeIterator = gs.getAllEdges();
		
		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
		int count = 0;
		int count2= 0;
		int countInvalidDirection = 0;
		int countBidirectional = 0;
		int countOneWay = 0;
		int countOneWayInverse = 0;
		while(edgeIterator.next()) {
			count++;

			int externalFromNodeId = edgeIterator.getBaseNode();
			int externalToNodeId = edgeIterator.getAdjNode();
			int externalEdgeId = edgeIterator.getEdge();
			int distance = (int)NumberUtils.round(edgeIterator.getDistance() * 1000, 0); // Convert distance from meters to millimeters
			String label = edgeIterator.getName();
			
			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalFromNodeId)));
			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalFromNodeId)));	

			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalToNodeId)));
			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalToNodeId)));			

			CHNodeImpl fromNode, toNode;

			long fromNodeId, toNodeId;

			if(!hashExternalIdToId.containsKey(externalFromNodeId)){

				fromNode = new CHNodeImpl(externalFromNodeId, latitudeFrom, longitudeFrom);
				graph.addNode(fromNode);
				fromNodeId = (long)fromNode.getId();
				hashExternalIdToId.put(externalFromNodeId, fromNodeId);
			} else {
				fromNodeId = hashExternalIdToId.get(externalFromNodeId);
			}

			if(!hashExternalIdToId.containsKey(externalToNodeId)){
				toNode = new CHNodeImpl(externalToNodeId, latitudeTo, longitudeTo);
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
				
				PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
				List<Point> geometry = new ArrayList<Point>();
				for(int i =0; i < pl.size(); i++) {
					Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
					geometry.add(p);
				}
				
				count2++;
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				
				continue;
			}
			
			
//			if(fromNodeId == toNodeId) {
//				count2++;
//				logger.info("Edge not created, because fromNodeId({}) == toNodeId({})", fromNodeId, toNodeId);
//				continue;
//			}
			
			//geometry
			PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
			List<Point> geometry = new ArrayList<Point>();
			for(int i =0; i < pl.size(); i++) {
				Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
				geometry.add(p);
			}
			
			if(direction == 0) {          // Bidirectional
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countBidirectional++;
				
			} else if(direction == 1) {   // One direction: base -> adj
				CHEdge edge = new CHEdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWay++;
			} else if(direction == -1) {  // One direction: adj -> base
				CHEdge edge = new CHEdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
				graph.addEdge(edge);
				countOneWayInverse++;
			} else {
				logger.info("Edge not created. Invalid direction: {}", direction);
			}
		}

		logger.info("Number of Nodes: {}", graph.getNumberOfNodes()); 
		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
		logger.info("Count: {}", count);
		logger.info("Number of invalid direction in original edges: {}", countInvalidDirection);
		logger.info("Number of Bidirectional edges: {}", countBidirectional);
		logger.info("Number of OneWay edges: {}", countOneWay);
		logger.info("Number of OneWayInverse edges: {}", countOneWayInverse);

		graph.save();

		double finalTime = System.currentTimeMillis();
		double total = finalTime - initialTime;
		logger.info("Final date: {}", new Date());
		logger.info("Total time: {}", total);
		
		if (graphHopperDir.equals(graphastTmpDir)) {
			FileUtils.deleteDir(graphastTmpDir);
		}
		
		return graph;
	}
	
//	/* (non-Javadoc)
//	 * @see org.graphast.importer.OSMImporter#execute()
//	 */
//	@Override
//	public GraphBounds execute() {
//
//		logger.info("Initial date: {}", new Date());
//		double initialTime = System.currentTimeMillis();
//
//		GraphBounds graph = new GraphImpl(graphastDir);
//
//		GraphHopper gh = OSMToGraphHopperReader.createGraph(osmFile, graphHopperDir, false, false);
//		GraphStorage gs = gh.getGraph();
//		EdgeIterator edgeIterator = gs.getAllEdges();
//
//		Int2LongOpenHashMap hashExternalIdToId = new Int2LongOpenHashMap();
//		int count = 0;
//		int countInvalidDirection = 0;
//		int countBidirectional = 0;
//		int countOneWay = 0;
//		int countOneWayInverse = 0;
//		while(edgeIterator.next()) {
//			count++;
//
//			int externalFromNodeId = edgeIterator.getBaseNode();
//			int externalToNodeId = edgeIterator.getAdjNode();
//			int externalEdgeId = edgeIterator.getEdge();
//			int distance = (int)NumberUtils.round(edgeIterator.getDistance() * 1000, 0); // Convert distance from meters to millimeters
//			String label = edgeIterator.getName();
//			
//			double latitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalFromNodeId)));
//			double longitudeFrom = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalFromNodeId)));	
//
//			double latitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLatitude(externalToNodeId)));
//			double longitudeTo = latLongToDouble(latLongToInt(gs.getNodeAccess().getLongitude(externalToNodeId)));			
//
//			NodeImpl fromNode, toNode;
//
//			long fromNodeId, toNodeId;
//
//			if(!hashExternalIdToId.containsKey(externalFromNodeId)){
//
//				fromNode = new NodeImpl(externalFromNodeId, latitudeFrom, longitudeFrom);
//				graph.addNode(fromNode);
//				fromNodeId = (long)fromNode.getId();
//				hashExternalIdToId.put(externalFromNodeId, fromNodeId);
//			} else {
//				fromNodeId = hashExternalIdToId.get(externalFromNodeId);
//			}
//
//			if(!hashExternalIdToId.containsKey(externalToNodeId)){
//				toNode = new NodeImpl(externalToNodeId, latitudeTo, longitudeTo);
//				graph.addNode(toNode);
//				toNodeId = (long)toNode.getId();
//				hashExternalIdToId.put(externalToNodeId, toNodeId);
//			} else {
//				toNodeId = hashExternalIdToId.get(externalToNodeId);
//			}
//
//			int direction = 9999;
//			try {
//				direction = getDirection(edgeIterator.getFlags());
//			} catch (Exception e) {
//				countInvalidDirection++;
//			}
//			
//			if(fromNodeId == toNodeId) {
//				logger.info("Edge not created, because fromNodeId({}) == toNodeId({})", fromNodeId, toNodeId);
//				continue;
//			}
//			
//			//geometry
//			PointList pl = edgeIterator.fetchWayGeometry(3); //3 gets all point including from node and to node
//			List<Point> geometry = new ArrayList<Point>();
//			for(int i =0; i < pl.size(); i++) {
//				Point p = new Point(pl.getLatitude(i),pl.getLongitude(i));
//				geometry.add(p);
//			}
//			
//			if(direction == 0) {          // Bidirectional
//				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
//				graph.addEdge(edge);
//				edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
//				graph.addEdge(edge);
//				countBidirectional++;
//				
//			} else if(direction == 1) {   // One direction: base -> adj
//				Edge edge = new EdgeImpl(externalEdgeId, fromNodeId, toNodeId, distance, label, geometry);
//				graph.addEdge(edge);
//				countOneWay++;
//			} else if(direction == -1) {  // One direction: adj -> base
//				Edge edge = new EdgeImpl(externalEdgeId, toNodeId, fromNodeId, distance, label, geometry);
//				graph.addEdge(edge);
//				countOneWayInverse++;
//			} else {
//				logger.info("Edge not created. Invalid direction: {}", direction);
//			}
//		}
//
//		logger.info("Number of Nodes: {}", graph.getNumberOfNodes());
//		logger.info("Number of Edges: {}", graph.getNumberOfEdges());
//		logger.info("Count: {}", count);
//		logger.info("Number of invalid direction in original edges: {}", countInvalidDirection);
//		logger.info("Number of Bidirectional edges: {}", countBidirectional);
//		logger.info("Number of OneWay edges: {}", countOneWay);
//		logger.info("Number of OneWayInverse edges: {}", countOneWayInverse);
//
//		graph.save();
//
//		double finalTime = System.currentTimeMillis();
//		double total = finalTime - initialTime;
//		logger.info("Final date: {}", new Date());
//		logger.info("Total time: {}", total);
//		
//		if (graphHopperDir.equals(graphastTmpDir)) {
//			FileUtils.deleteDir(graphastTmpDir);
//		}
//		
//		return graph;
//	}

	public static void main(String[] args) {
		String osmFile = args[0];
		String baseOsmFileName = osmFile.substring(0, osmFile.indexOf('.'));
		String graphastDir = args[1] != null ? args[1] : System.getProperty("user.dir") + "/" + baseOsmFileName;
		new OSMImporterImpl(osmFile, graphastDir).execute();
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