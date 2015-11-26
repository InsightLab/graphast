package org.graphast.piecewise;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import rcaller.RCaller;
import rcaller.RCode;

public class GeneratorFunctionPiecewise {
	
	private static final String RSCRIPT = "/usr/bin/Rscript";

	public double getValue(Date timeDay) {
		
		IGeneratorFunctionPiecewise generatorFunctionPiecewise = new GeneratorFunctionPiecewiseDatabase();
		
		double[][] data = null;
		try {
			data = generatorFunctionPiecewise.getData();
		} catch (PiecewiseException e) {
			e.printStackTrace();
		}
		
//		List<Object> executeLossFuntion = executeLossFuntion(data);
		
		return 0;
	}

	private List<Object> executeLossFuntion(double[][] data ) throws PiecewiseException {
		
		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);
		
		code.addDoubleMatrix("dados.frame", data);
		code.addRCode("dados.loess <- loess(y ~ x)");
		code.addRCode("xl <- seq(min(x), max(x), (max(x) - min(x))/1000)");
		code.addRCode("y.predict <- predict(dados.loess, xl)");
		code.addRCode("infl <- c(FALSE, diff(diff(y.predict)>0)!=0)");
		code.addRCode("objMin <- cbind(xl, y.predict)[xl == min(xl),]");
		code.addRCode("obj <- cbind(xl, y.predict)[infl,]");
		code.addRCode("objMax <- cbind(xl, y.predict)[xl == max(xl),]");
		code.addRCode("allObjects<-list(resultMin=objMin, resultMax=objMax, resultObj=obj)");

		caller.setRCode(code);
		caller.runAndReturnResult("allObjects");

		try {
			System.out.println(caller.getParser().getXMLFileAsString());
		} catch (IOException e) {
			throw new PiecewiseException("[ERRO] Um erro quando estava sendo executada a função LOESS do R.");
		}
		
		return null;
	}
}
