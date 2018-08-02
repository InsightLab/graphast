package org.insightlab.graphast.serialization;

import org.insightlab.graphast.generators.GraphGenerator;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.query.cost_functions.CostFunctionFactory;
import org.insightlab.graphast.query.cost_functions.InterpolationMethod;
import org.insightlab.graphast.query.cost_functions.TimeDependentCostFunction;
import org.insightlab.graphast.query.shortestpath.DijkstraStrategy;
import org.insightlab.graphast.query.shortestpath.ShortestPathStrategy;
import org.insightlab.graphast.query.utils.DistanceVector;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class SerializationTest {

	protected static GraphSerializer serializer;
	private static Graph original, reloaded;
	private static final String PATH = "serialized_graphs/";
	
	@Before
	public void setUp() {
		if (reloaded == null) {
			serializer = GraphSerializer.getInstance();
			original = GraphGenerator.getInstance().generateExample4();
			serializer.save(PATH, original);
			
			try {
				reloaded = serializer.load(PATH);	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("File Not Found!");
			}
		}
	}
	
	@Test
	public void testNumberOfNodes() {
		assertEquals("Number of nodes", original.getNumberOfNodes(), reloaded.getNumberOfNodes());
	}
	
	@Test
	public void testNumberOfEdges() {
		assertEquals("Number of edges", original.getNumberOfEdges(), reloaded.getNumberOfEdges());
	}

	@Test
	public void linearTimeDependentDijkstraTest() {
		ShortestPathStrategy dijkstra = new DijkstraStrategy(reloaded);
		TimeDependentCostFunction costFunction = CostFunctionFactory.getTimeDependentCostFunction(InterpolationMethod.LINEAR);
		
		dijkstra.setCostFunction(costFunction);
		
		DistanceVector result;

		costFunction.setTime(6, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 6:00", 14, result.getDistance(6), 0);
		
		costFunction.setTime(12, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 12:00", 12, result.getDistance(6), 0);
		
		costFunction.setTime(15, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 15:00", 13, result.getDistance(6), 0); 
		
		costFunction.setTime(20, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 20:00", 14, result.getDistance(6), 0); 
		
	}
	
	@Test
	public void stepTimeDependentDijkstraTest() {
		ShortestPathStrategy dijkstra = new DijkstraStrategy(reloaded);
		TimeDependentCostFunction costFunction = CostFunctionFactory.getTimeDependentCostFunction(InterpolationMethod.STEP);
		dijkstra.setCostFunction(costFunction);
		DistanceVector result;

		costFunction.setTime(6, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 6:00", 14, result.getDistance(6), 0);
		
		costFunction.setTime(18, 0);
		result = dijkstra.run(0, 6);
		assertEquals("Temporal dijkstra example4 18:00", 12, result.getDistance(6), 0);

	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		SerializationUtils.deleteSerializedGraph(PATH);
	}

}
