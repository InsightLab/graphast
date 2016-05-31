package org.graphast.piecewise;


public class GeneratorFunctionLoess implements IGeneratorFunction {
	
	private IManipulatorEngine engine;
	
	public GeneratorFunctionLoess() {
	}
	
	public GeneratorFunctionLoess(IManipulatorEngine engine) {
		this.engine = engine;
	}

	@Override
	public Function gerFuntionEdge(long idEdge, long timestamp) {
		return engine.run(timestamp);
	}
}
