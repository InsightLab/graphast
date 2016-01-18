package org.graphast.piecewise;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.graphast.util.ConnectionJDBC;
import org.junit.Assert;
import org.junit.Test;

import rcaller.RCaller;
import rcaller.RCode;

public class GeneratorFunctionPiecewiseDatabaseTest {
	
	private static final String RSCRIPT = "/usr/bin/Rscript";

	@Test
	public void getConnectionTest() throws ClassNotFoundException, SQLException, IOException {
		
		Connection connection = ConnectionJDBC.getConnection();
		Assert.assertNotNull(connection);
	}
	
	@Test
	public void getDataTest() throws PiecewiseException {
		
		IBuilderDatasource generatorFunctionPiecewise = new BuilderDatasourceDB();
		double[][] data = generatorFunctionPiecewise.getData();
		int length = data.length;
		
		Assert.assertEquals(28, length);
	}
	
	@Test
	public void verifyPointsTest() throws IOException, PiecewiseException {
		
		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);

		IBuilderDatasource generatorFunctionPiecewise = new BuilderDatasourceDB();
		double[][] data = generatorFunctionPiecewise.getData();
		
		code.addDoubleMatrix("matrix", data);
		code.addRCode("dados.frame <- data.frame(matrix)");
		code.addRCode("dados.loess <- loess(dados.frame)");
		code.addRCode("xl <- with(dados.loess, seq(min(x), max(x), (max(x) - min(x))/1000))");
		code.addRCode("y.predict <- predict(dados.loess, xl)");
		code.addRCode("infl <- c(FALSE, diff(diff(y.predict)>0)!=0)");
		code.addRCode("pontoInicial <- cbind(xl, y.predict)[xl == min(xl),]");
		code.addRCode("pontosInflexao <- cbind(xl, y.predict)[infl,]");
		code.addRCode("pontoFinal <- cbind(xl, y.predict)[xl == max(xl),]");
		code.addRCode("allObjects <- list(pontoInicial=pontoInicial, pontoFinal=pontoFinal, pontosInflexao=pontosInflexao)");
		
		caller.setRCode(code);
		caller.runAndReturnResult("allObjects");

		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
		System.out.println(caller.getParser().getXMLFileAsString().toString());
		
		double[] pontoInicial = caller.getParser().getAsDoubleArray("pontoInicial");
		Assert.assertEquals(Double.valueOf("1201977368000"), Double.valueOf(pontoInicial[0]));
		Assert.assertEquals(Double.valueOf("295522763826171"), Double.valueOf(pontoInicial[1]));
		
		double[] pontosInflexao = caller.getParser().getAsDoubleArray("pontosInflexao");
		int length = pontosInflexao.length;
		Assert.assertEquals(6, length);
		
		Assert.assertEquals(Double.valueOf("1202063763696"), Double.valueOf(pontosInflexao[0]));
		Assert.assertEquals(Double.valueOf("55328186.6718641"), Double.valueOf(pontosInflexao[length/2]));
	}
	
	@Test
	public void generationFunctionEdgeTest() throws NumberFormatException, PiecewiseException {
		
		GeneratorFunctionLoess generatorFunctionPiecewise = new GeneratorFunctionLoess();
		double value = generatorFunctionPiecewise.getValue(Long.valueOf("1201977368000"));
		
		System.out.println(value);
		Assert.assertNotNull(value);
	}
}
