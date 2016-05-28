package org.graphast.piecewise;

public class GeneratorFunctionMatrix implements IGeneratorFunction {
	
	private IManipulatorEngine engine;
	
	public GeneratorFunctionMatrix(IManipulatorEngine engine) {
		this.engine = engine;
	}

	@Override
	public Function gerFuntionEdge(long idEdge, long timestamp) {
		engine.run(timestamp);
		return null;
	}
}
