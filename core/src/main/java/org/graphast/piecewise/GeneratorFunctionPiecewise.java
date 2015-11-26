package org.graphast.piecewise;

import rcaller.RCaller;
import rcaller.RCode;

public class GeneratorFunctionPiecewise {
	
	private static final String RSCRIPT = "/usr/bin/Rscript";

	public double getValue(double timestamp) throws PiecewiseException {
		
		IGeneratorFunctionPiecewise generatorFunctionPiecewise = new GeneratorFunctionPiecewiseDatabase();
		double[][] coleta = generatorFunctionPiecewise.getData();
		return getFuntionEdge(coleta, timestamp);
	}

	private double getFuntionEdge(double[][] data, double timestamp) throws PiecewiseException {
		
		RCaller caller = new RCaller();
		RCode code = caller.getRCode();
		caller.setRscriptExecutable(RSCRIPT);
		
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

		double[] pontoInicial = caller.getParser().getAsDoubleArray("pontoInicial");
		double[] pontosInflexao = caller.getParser().getAsDoubleArray("pontosInflexao");
		
		PontoGeometrico pontoInicialGeo = new PontoGeometrico(pontoInicial[0], pontoInicial[1]);
		PontoGeometrico pontoFinalGeo = new PontoGeometrico(pontosInflexao[0], pontosInflexao[pontosInflexao.length/2]);
		
		double coeficienteAngular = getCoeficienteAngular(pontoInicialGeo, pontoFinalGeo);
		double coeficienteLinear = getCoeficienteLinear(pontoInicialGeo, coeficienteAngular);
		double y = coeficienteAngular *  timestamp + coeficienteLinear;
		
		return y;
	}
	
	private double getCoeficienteLinear(PontoGeometrico pontoInicialGeo, double coeficienteAngular) {
		double gama = pontoInicialGeo.getY() - coeficienteAngular * pontoInicialGeo.getX();  
		return gama;
	}

	private double getCoeficienteAngular(PontoGeometrico pontoInicialGeo, PontoGeometrico pontoFinalGeo) {
		double alfa = (pontoFinalGeo.getY() - pontoInicialGeo.getY()) / (pontoFinalGeo.getX() - pontoInicialGeo.getX());
		return alfa;
	}

	class PontoGeometrico {
		
		double x;
		double y;
		
		public PontoGeometrico(double x, double y) {
			this.x = x;
			this.y= y;
		}
		
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
	}
}
