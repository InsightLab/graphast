package org.graphast.query.dao.postgis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphast.model.Node;
import org.graphast.model.NodeImpl;
import org.graphast.util.ConnectionJDBC;
import org.postgis.Point;

public class GraphastDAO {
	
	private static final int FIELD_ID_TAXI = 1;
	private final static int FIELD_POINT_TAXI = 2;
	private final static int FIELD_ID_ROAD = 3;

	public ResultSet getPoints(String table) throws ClassNotFoundException, SQLException, IOException {

		String finalQuery = String.format(QueryPostgis.QUERY_POINT_ROAD.replace("TABLE_NAME", "%s"), table);
		Statement statement = ConnectionJDBC.getConnection().createStatement();

		return statement.executeQuery(finalQuery);
	}
	private void addNodeInMap( Map<Integer, List<Node>> map, int idRoad, Node node) {
		if (!map.containsKey(idRoad)) {
			map.put(idRoad, new ArrayList<Node>());
		}
		map.get(idRoad).add(node);
	}
	public Map<Integer, List<Node>> getPoiTaxiFortaleza() throws ClassNotFoundException, SQLException, IOException {
		
		Map<Integer, List<Node>> mapIdRoads = new HashMap<Integer, List<Node>>();
		
		String finalQuery = String.format(QueryPostgis.QUERY_POINT_TAXI);
		Statement statement = ConnectionJDBC.getConnection().createStatement();
		ResultSet result = statement.executeQuery(finalQuery);
		
		while (result.next()) {
			
			String strPointTaxi = result.getString(FIELD_POINT_TAXI);
			Point point = new Point(strPointTaxi);
			int idTaxi = result.getInt(FIELD_ID_TAXI);
			int idRoad = result.getInt(FIELD_ID_ROAD);
			Node node = new NodeImpl(idTaxi, point.getY(), point.getX());
			node.setCategory(idTaxi);
			node.setLabel(Integer.valueOf(idRoad).toString());
			addNodeInMap(mapIdRoads, idRoad, node);
		}

		return mapIdRoads;
	}
}
