package org.graphast.importer;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.model.NodeImpl;
import org.graphast.query.dao.postgis.QueryPostgis;
import org.graphast.util.ConnectionJDBC;
import org.postgis.Point;

public class PoiTaxiFortalezaImporter {

	private GraphBounds graph;
	
	private static final int FIELD_PARAMETER_ID_LINESTRING = 1;
	private static final int FIELD_ID_TAXI = 1;
	private final static int FIELD_POINT_TAXI = 2;
	private final int SIZE_INTERVAL = 96;
	
	public PoiTaxiFortalezaImporter(GraphBounds graph) {
		this.graph = graph;
	}
	
	public GraphBounds getGraph() {
		
		for(int i=0; i<graph.getNumberOfEdges(); i++) {
			Edge edge = graph.getEdge(i);
			
			try {
				setTaxiInGraph(edge);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		
		return graph;
	}
	
	private void setTaxiInGraph(Edge edge) throws ClassNotFoundException, IOException {
		
		long externalId = edge.getId();
		List<Node> taxiNodes = getCloseTaxi(externalId);
		Node nodeTo = graph.getNode(edge.getToNode());
		
		for (Node node: taxiNodes) {
			
			if (!graph.getCategories().contains(node.getCategory())) {
				graph.addNode(node);
				Edge edgeToNeighbor = new EdgeImpl(node.getId(), nodeTo.getId(), 1);
				addCostZero(edgeToNeighbor);
				graph.addEdge(edgeToNeighbor);
			}
		}
	}
	
	private void addCostZero(Edge edge) {
		
		int[] costs = new int[SIZE_INTERVAL];
		for (int i : costs) {
			costs[i] = 2;
		}
		edge.setCosts(costs);
	}
	
	private List<Node> getCloseTaxi(long idLineString) throws ClassNotFoundException, IOException {

		List<Node> taxiNodes = new ArrayList<Node>();
		try {
			PreparedStatement taxiStatement = ConnectionJDBC.getConnection().prepareStatement(QueryPostgis.QUERY_POINT_TAXI);
			taxiStatement.setLong(FIELD_PARAMETER_ID_LINESTRING, idLineString);
			ResultSet resultSet = taxiStatement.executeQuery();

			while (resultSet.next()) {
				
				String string = resultSet.getString(FIELD_POINT_TAXI);
				Point point = new Point(string);
				Node node = new NodeImpl(resultSet.getInt(FIELD_ID_TAXI), point.getY(), point.getX());
				node.setCategory(resultSet.getInt(FIELD_ID_TAXI));
				node.setExternalId(resultSet.getInt(FIELD_ID_TAXI));
				taxiNodes.add(node);
			}
			
			ConnectionJDBC.getConnection().close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return taxiNodes;
	}

}
