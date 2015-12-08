package org.graphast.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.model.Node;
import org.graphast.model.NodeImpl;
import org.graphast.query.dao.postgis.GraphastDAO;
import org.graphast.util.ConnectionJDBC;
import org.graphast.util.DistanceUtils;
import org.graphast.util.GeoUtils;
import org.postgis.LineString;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OSMDBImporter implements Importer {

	private GraphastDAO dao;
	private GraphBounds graph;
	private String table;
	private final int FIELD_ID_LINESTRING = 1;
	private final int FIELD_LINESTRING = 2;
	private final int SIZE_INTERVAL = 96;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public OSMDBImporter(String table, String directory) {
		this.table = table;
		dao = new GraphastDAO();
		graph = new GraphBoundsImpl(directory);
	}
	
	@Override
	public GraphBounds execute() {
		
		try {
			plotNodes();
			graph.createBounds();
			
		} catch (SQLException | ClassNotFoundException | IOException e) {
			System.err.println("[ERRO] Ocorreu um erro na construção do grafo.");
		}
		return graph;
	}

	private void plotNodes() throws SQLException, ClassNotFoundException, IOException {
		Map<Integer, List<Node>> mapTaxi = dao.getPoiTaxiFortaleza();

		ResultSet result = dao.getPoints(table);
		int pointCount = 0;
		while (result.next()) {
			LineString lineString = new LineString(result.getString(FIELD_LINESTRING));
			Point[] arrayPoints = lineString.getPoints();
			LOGGER.info(String.format("registro: %s", result.getString(FIELD_LINESTRING)));
			
			
			int idRoad = result.getInt(FIELD_ID_LINESTRING);
			Node previousNode = null;

			for (Point point : arrayPoints) {
				pointCount++;
				LOGGER.info( String.format("Point [x,y]: %s,%s", point.getX(), point.getY()));
				Node node = new NodeImpl(point.getY(), point.getX());
				node.setLabel(Long.valueOf(idRoad).toString());
				Long nodeId = graph.getNodeId(GeoUtils.latLongToInt(node.getLatitude()), GeoUtils.latLongToInt(node.getLongitude()));

				if (nodeId != null) {
					LOGGER.info(String.format("point already exist in graph"));
					node = graph.getNode(nodeId);
				} else {
					graph.addNode(node);
					LOGGER.info(String.format("point inserted in graph with ID: %s", node.getId()));
				}
				
				

				if (previousNode != null && !previousNode.getId().equals(node.getId())) {
					LOGGER.info( String.format("Add edge from previous: %s to current: %s node", previousNode.getId(), node.getId()));
					Edge edge = new EdgeImpl(idRoad, previousNode.getId().longValue(), node.getId().longValue(), 0, String.valueOf(idRoad));
					addCost(edge);
					graph.addEdge(edge);
					if(mapTaxi.containsKey(idRoad)) {
						for(int i=0; i<mapTaxi.get(idRoad).size(); i++) {
							Node taxi = mapTaxi.get(idRoad).get(i);
							if (GeoUtils.isPointInEdgeLine(graph, taxi, edge)) {
								defineNodeWithTaxi(taxi, node);
								mapTaxi.get(idRoad).remove(i);
								i--;
							}
						}
					}
				}
				LOGGER.info(String.format("Graph now has %s nodes", graph.getNumberOfNodes()));
				LOGGER.info(String.format("Graph now has %s edges",graph.getNumberOfEdges()));


				previousNode = node;
			}
		}
		LOGGER.info( String.format("Total points parsed are: %s", pointCount));
		ConnectionJDBC.getConnection().close();
	}
	
	private void addCostZero(Edge edge) {
		
		int[] costs = new int[SIZE_INTERVAL];
		for (int i : costs) {
			costs[i] = 0;
		}
		edge.setCosts(costs);
	}

	private void defineNodeWithTaxi(Node taxi, Node node) {
		long externalId = taxi.getExternalId();
		graph.setNodeCategory(node.getId(), Long.valueOf(externalId).intValue());
	}
	
	@Deprecated
	private void connectTaxiToEdge(Node taxi, Node end) {
		graph.addNode(taxi);
		LOGGER.info(String.format("Taxi %s lies in edge and now taxi is node: %s", taxi.getCategory(), taxi.getId()));
		LOGGER.info(String.format("Connect Taxi %s with node: %s", taxi.getCategory(), end.getId()));
		Edge edge = new EdgeImpl(taxi.getId().longValue(), end.getId().longValue(), 0);
		addCostZero(edge);
		graph.addEdge(edge);
	}

	private void addCost(Edge edge) {

		Node nodeFrom = graph.getNode(edge.getFromNode());
		Node nodeTo = graph.getNode(edge.getToNode());

		double distanceBetweenLatLongHaversine = 
				DistanceUtils.distanceLatLong(nodeFrom.getLatitude(), nodeFrom.getLongitude(), 
				nodeTo.getLatitude(), nodeTo.getLongitude());
		
		double distanceBetweenLatLongHaversineInMill = distanceBetweenLatLongHaversine * 1000;
		
		int[] edgesCosts = CostGenerator.generateSyntheticEdgesCosts(Double.valueOf(distanceBetweenLatLongHaversineInMill).intValue());
		edge.setCosts(edgesCosts);
	}
}
