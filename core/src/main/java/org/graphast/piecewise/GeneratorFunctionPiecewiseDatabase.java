package org.graphast.piecewise;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.graphast.query.postgis.QueryPostgis;
import org.graphast.util.ConnectionJDBC;

public class GeneratorFunctionPiecewiseDatabase implements IGeneratorFunctionPiecewise {

	private int FIELD_DATE_TIME = 1;
	private int FIELD_DURACAO   = 2;

	@Override
	public double[][] getData() throws PiecewiseException {
		
		Connection connectionJDBC = null;
		try {
			connectionJDBC = ConnectionJDBC.getConnection();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			throw new PiecewiseException("[ERRO] Um erro ocorreu quando estava sendo aberta a conexão com DB.");
		}
		
		ResultSet resultSet = null;
		double[][] matrixResult;
		try {
			Statement statement = connectionJDBC.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			resultSet = statement.executeQuery(QueryPostgis.QUERY_ALL_DATE_TIME_WITH_DURATE);
			
			resultSet.last();
			int size = resultSet.getRow();
			resultSet.beforeFirst();
			
			matrixResult = new double[size][2];
			
			int i = 0;
			while (resultSet.next()) {
			
				Double dateTime = resultSet.getDouble(FIELD_DATE_TIME);
				Double duracao = resultSet.getDouble(FIELD_DURACAO);
				double[] result = new double[2];
				result[0] = duracao;
				result[1] = dateTime;
				
				matrixResult[i] = result;
				i++;
			}
			
		} catch (SQLException e) {
			throw new PiecewiseException("[ERRO] Um erro ocorreu ao pegar as informações no DB.");
		}
		
		
		return matrixResult;
	}
}