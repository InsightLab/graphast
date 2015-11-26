package org.graphast.piecewise;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import rcaller.RCaller;
import rcaller.RCode;

public class RCallerEnvironmentsTest {

	private static final String RSCRIPT = "/usr/bin/Rscript";

	@Test
	public void createRscriptTest() {

		RCaller rcaller = new RCaller();
		rcaller.setRscriptExecutable(RSCRIPT);
		Assert.assertNotNull(rcaller.getRCode());
	}

	@Test
	public void baseLinearOutXMLTest() throws IOException {

		RCaller caller = new RCaller();
		RCode code = caller.getRCode();

		code.addRCode("set.seed(123)");
		code.addRCode("x<-rnorm(10)");
		code.addRCode("y<-rnorm(10)");
		code.addRCode("ols<-lm(y~x)");

		caller.setRCode(code);
		caller.runAndReturnResult("ols");

		System.out.println(caller.getParser().getXMLFileAsString());
		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
	}


	@Test
	public void loessTest() throws IOException {

		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);

		code.addRCode("set.seed(123)");
		code.addRCode("x<-rnorm(10)");
		code.addRCode("y<-rnorm(10)");
		code.addRCode("dados.loess <- loess(y ~ x)");

		caller.setRCode(code);
		caller.runAndReturnResult("dados.loess");

		System.out.println(caller.getParser().getXMLFileAsString());
		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
	}

	@Test
	public void loessWithRandomDataTest() throws IOException {

		RCaller caller = new RCaller();
		RCode code = caller.getRCode();

		caller.setRscriptExecutable(RSCRIPT);

		code.addRCode("set.seed(123)");
		code.addRCode("x<-rnorm(10)");
		code.addRCode("y<-rnorm(10)");
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

		System.out.println(caller.getParser().getXMLFileAsString());
		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
	}

	@Test
	public void loessWithSynteticDataTest() throws IOException {

		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);

		code.addRCode("set.seed(123)");
		code.addRCode("x<-rnorm(10)");
		code.addRCode("y<-rnorm(10)");
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

		System.out.println(caller.getParser().getXMLFileAsString());
		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
	}
	
	@Test
	public void rcallerSetParameterTest() throws IOException, PiecewiseException {
		
		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);

		IGeneratorFunctionPiecewise generatorFunctionPiecewise = new GeneratorFunctionPiecewiseDatabase();
		double[][] d = generatorFunctionPiecewise.getData();
		
		code.addDoubleMatrix("d", d);
		code.addRCode("dados.frame <- data.frame(d)");
		code.addRCode("dados.loess <- loess(dados.frame)");
		code.addRCode("xl <- with(dados.loess, seq(min(x), max(x), (max(x) - min(x))/1000))");
		code.addRCode("y.predict <- predict(dados.loess, xl)");
		code.addRCode("infl <- c(FALSE, diff(diff(y.predict)>0)!=0)");
		code.addRCode("objMin <- cbind(xl, y.predict)[xl == min(xl),]");
		code.addRCode("obj <- cbind(xl, y.predict)[infl,]");
		code.addRCode("objMax <- cbind(xl, y.predict)[xl == max(xl),]");
		code.addRCode("allObjects<-list(resultMin=objMin, resultMax=objMax, resultObj=obj)");
		
		caller.setRCode(code);
		caller.runAndReturnResult("dados.loess");

		System.out.println(caller.getParser().getXMLFileAsString());
		Assert.assertNotNull(caller.getParser().getXMLFileAsString());
		
	}
	



}
