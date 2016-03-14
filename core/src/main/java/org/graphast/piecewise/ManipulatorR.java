package org.graphast.piecewise;

import com.github.rcaller.rStuff.RCaller;
import com.github.rcaller.rStuff.RCode;

public class ManipulatorR implements IManipulatorEngine {
	
	private static final String RSCRIPT = "/usr/bin/Rscript";
	private double[][] data;

	public ManipulatorR(double[][] coleta) {
		this.data = coleta;
	}
	
	@Override
	public Function run(long x) {

		
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
		PontoGeometrico pontoGeo1 = null;

		yFinal = coeficienteAngularAnterior;
		
		for (int i = 0; i < (pontosInflexao.length/2) - 1; i++) {
			
			pontoGeo1 = new PontoGeometrico(pontosInflexao[i], pontosInflexao[pontosInflexao.length/2+i]);
			PontoGeometrico pontoGeo2 = new PontoGeometrico(pontosInflexao[i+1], pontosInflexao[pontosInflexao.length/2+(i+1)]);
			double coeficienteAngularCurrent = getCoeficienteAngular(pontoGeo1, pontoGeo2);
			double coeficienteLinearCurrent = getCoeficienteLinear(pontoGeo1, coeficienteAngularCurrent);
			
			yFinal = yFinal + (pontoGeo1.getX() * (coeficienteLinearAnterior - coeficienteLinearCurrent));
			
			coeficienteAngularAnterior = coeficienteAngularCurrent;
			coeficienteLinearAnterior = coeficienteLinearCurrent; 
		}
		
		double[] pontoFinal = caller.getParser().getAsDoubleArray("pontoFinal");
		
		PontoGeometrico ponto1FinalGeo = new PontoGeometrico(pontosInflexao[(pontosInflexao.length/2)-1],
				pontosInflexao[pontosInflexao.length - 1]);
		PontoGeometrico ponto2FinalGeo = new PontoGeometrico(pontoFinal[0], pontoFinal[1]);
		
		double coeficienteAngularFinal = getCoeficienteAngular(ponto1FinalGeo, ponto2FinalGeo);
		double coeficienteLinearFinal = getCoeficienteLinear(ponto1FinalGeo, coeficienteAngularFinal);
		
		yFinal = yFinal + (pontoGeo1.getX() * (coeficienteLinearAnterior - coeficienteLinearFinal)) + coeficienteAngularFinal * x;
		
		return null;
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
