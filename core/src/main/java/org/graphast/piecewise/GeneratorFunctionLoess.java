package org.graphast.piecewise;

import rcaller.RCaller;
import rcaller.RCode;

public class GeneratorFunctionLoess implements IGeneratorFunctionPiecewise {
	
	private IManipulatorEngine engine;
	
	public GeneratorFunctionLoess() {
	}
	
	public GeneratorFunctionLoess(IManipulatorEngine engine) {
		this.engine = engine;
	}
	
	private static final String RSCRIPT = "/usr/bin/Rscript";

	public double getValue(double timestamp) throws PiecewiseException {
		
		IBuilderDatasource generatorFunctionPiecewise = new BuilderDatasourceDB();
		double[][] coleta = generatorFunctionPiecewise.getData();
		return getFuntionEdge(coleta, timestamp);
	}
	
	@Override
	public Function gerFuntionEdge(long idEdge, long timestamp) {
		
		engine.run();
		return null;
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
		PontoGeometrico pontoFinalGeo0 = new PontoGeometrico(pontosInflexao[0], pontosInflexao[pontosInflexao.length/2]);
		
		double coeficienteAngularAnterior = getCoeficienteAngular(pontoInicialGeo, pontoFinalGeo0);
		double coeficienteLinearAnterior = getCoeficienteLinear(pontoInicialGeo, coeficienteAngularAnterior);
					
		
		double yFinal = 0;
		for (int i = 0; i < (pontosInflexao.length/2) - 1; i++) {
			
			PontoGeometrico pontoGeo1 = new PontoGeometrico(pontosInflexao[i], pontosInflexao[pontosInflexao.length/2+i]);
			PontoGeometrico pontoGeo2 = new PontoGeometrico(pontosInflexao[i+1], pontosInflexao[pontosInflexao.length/2+(i+1)]);
			double coeficienteAngularCurrent = getCoeficienteAngular(pontoGeo1, pontoGeo2);
			double coeficienteLinearCurrent = getCoeficienteLinear(pontoGeo1, coeficienteAngularCurrent);
			
			yFinal = yFinal + (coeficienteAngularAnterior + pontoGeo1.getX() * (coeficienteLinearAnterior - coeficienteLinearCurrent));
			
			coeficienteAngularAnterior = coeficienteAngularCurrent;
			coeficienteLinearAnterior = coeficienteLinearCurrent; 
		}
		
		double[] pontoFinal = caller.getParser().getAsDoubleArray("pontoFinal");
		
		PontoGeometrico ponto1FinalGeo = new PontoGeometrico(pontosInflexao[(pontosInflexao.length/2)-1],
				pontosInflexao[pontosInflexao.length - 1]);
		PontoGeometrico ponto2FinalGeo = new PontoGeometrico(pontoFinal[0], pontoFinal[1]);
		
		double coeficienteAngularFinal = getCoeficienteAngular(ponto1FinalGeo, ponto2FinalGeo);
		double coeficienteLinearFinal = getCoeficienteLinear(ponto1FinalGeo, coeficienteAngularFinal);
		
		yFinal = yFinal + (coeficienteAngularFinal * timestamp + coeficienteLinearFinal);
				
		return yFinal;
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
