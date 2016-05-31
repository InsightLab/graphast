package org.graphast.piecewise;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.graphast.query.postgis.QueryPostgis;
import org.graphast.util.ConnectionJDBC;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTimeTravelToDB {

	private final String FILE_JSON_TIME_TRAVEL = "time_travel.json";
	
	private String FIELD_JSON_COORDINATES  = "coordinates";
	private String FIELD_JSON_TIME   		= "entry_time";
	private String FIELD_JSON_TOTAL_TRAVEL = "total_time";
	
	public void jsonToDB() throws PiecewiseException, SQLException, ClassNotFoundException, IOException {
		
		JSONObject jsonObject = new JSONObject(FILE_JSON_TIME_TRAVEL);
		
		JSONArray coordinates = jsonObject.getJSONArray(FIELD_JSON_COORDINATES);
		double timeDay = jsonObject.getDouble(FIELD_JSON_TIME);
		double totalTime = jsonObject.getDouble(FIELD_JSON_TOTAL_TRAVEL);
		
		Object latitude = coordinates.get(0);
		Object longitude = coordinates.get(1);
		
		Connection connectionJDBC = null;
		try {
			connectionJDBC = ConnectionJDBC.getConnection();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			throw new PiecewiseException("[ERRO] Um erro ocorreu quando estava sendo aberta a conexão com DB.");
		}
		
		PreparedStatement statement = connectionJDBC.prepareStatement(QueryPostgis.QUERY_CLOSER_LINESTRING);
		statement.setString(0, latitude.toString());
		statement.setString(0, longitude.toString());
		
		ResultSet resultSet = statement.executeQuery(QueryPostgis.QUERY_CLOSER_LINESTRING);
		
		while (resultSet.next()) {
			
			int idEdge = resultSet.getInt(0);
			persisteValue(idEdge, timeDay, totalTime);
		}
	}

	private void persisteValue(int idEdge, double timeDay, double totalTime) throws SQLException, ClassNotFoundException, IOException {
		
		String insertTableSQL = QueryPostgis.INSERT_PIECEWISE; 
				
		PreparedStatement preparedStatement = ConnectionJDBC.getConnection().prepareStatement(insertTableSQL);
		preparedStatement.setInt(1, idEdge);
		preparedStatement.setDouble(2, timeDay);
		preparedStatement.setDouble(3, totalTime);
		preparedStatement .executeUpdate();
		
	}
}
