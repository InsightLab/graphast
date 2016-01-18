package org.graphast.piecewise;


public class GeneratorFunctionLoess implements IGeneratorFunction {
	
	private IManipulatorEngine engine;
	
	public GeneratorFunctionLoess() {
	}
	
	public GeneratorFunctionLoess(IManipulatorEngine engine) {
		this.engine = engine;
	}

	public double getValue(long timestamp) throws PiecewiseException {
		return engine.run().getValue(timestamp);
	}
	
	@Override
	public Function gerFuntionEdge(long idEdge, long timestamp) {
		return engine.run();
	}
}
