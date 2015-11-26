package org.graphast.piecewise;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.graphast.util.ConnectionJDBC;
import org.junit.Assert;
import org.junit.Test;

public class GeneratorFunctionPiecewiseDatabaseTest {

	@Test
	public void getConnectionTest() throws ClassNotFoundException, SQLException, IOException {
		
		Connection connection = ConnectionJDBC.getConnection();
		Assert.assertNotNull(connection);
	}
	
	@Test
	public void getDataTest() throws PiecewiseException {
		
		IGeneratorFunctionPiecewise generatorFunctionPiecewise = new GeneratorFunctionPiecewiseDatabase();
		double[][] data = generatorFunctionPiecewise.getData();
		int length = data.length;
		
		Assert.assertEquals(28, length);
	}
}
