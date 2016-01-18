package org.graphast.piecewise;

import java.util.Calendar;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.junit.Test;

public class GeneratorFunctionRefactore {

	// Com um graph já existente, basta setar a função.
	@Test
	public void definedFuntionEdgeTest() {
		
		//Hora do dia: Dia: 16/01/2016 Hora: 11:30:00
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, 01, 16, 11, 30, 00);
		long timestamp = calendar.getTimeInMillis(); 
		
		GraphGenerator graphGenerator = new GraphGenerator();
		Graph graph = graphGenerator.generateExample();
		
		IManipulatorEngine engineR = new ManipulatorR();
		GeneratorFunctionLoess generatorFunction = new GeneratorFunctionLoess(engineR);
		Function function = generatorFunction.gerFuntionEdge(1l, timestamp);
		
		graph.setFuntionEdge(1l, function);
	}
	
	//Criar o graph com as funções dos edges
	@Test
	public void createGraphWithFuntionEdgeTest() {
	}
}
