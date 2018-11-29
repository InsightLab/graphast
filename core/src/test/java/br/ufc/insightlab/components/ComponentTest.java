package br.ufc.insightlab.components;

import br.ufc.insightlab.generators.GraphGenerator;
import br.ufc.insightlab.model.Edge;
import br.ufc.insightlab.model.Graph;
import br.ufc.insightlab.model.Node;
import br.ufc.insightlab.model.components.EdgeComponent;
import br.ufc.insightlab.model.components.cost_list_components.CostListEdgeComponent;
import br.ufc.insightlab.model.components.spatial_components.*;
import br.ufc.insightlab.query.cost_functions.CostFunctionFactory;
import br.ufc.insightlab.query.cost_functions.InterpolationMethod;
import br.ufc.insightlab.query.cost_functions.TimeDependentCostFunction;
import br.ufc.insightlab.query.shortestpath.DijkstraStrategy;
import br.ufc.insightlab.query.shortestpath.ShortestPathStrategy;
import br.ufc.insightlab.query.utils.DistanceVector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

//import Edge;

public class ComponentTest {

	private Graph g;
	private Graph graphExample4;
	private Node n0, n1, n2, n3;
	private Edge e0, e1, e2, e3;
	
	@Before
	public void setUp() throws Exception {
		g = new Graph();
		g.addComponent(new SpatialGraphComponent());
		n0 = new Node(0);
		n0.addComponent(new SpatialNodeComponent(12, 4));
		n1 = new Node(1);
		n1.addComponent(new SpatialNodeComponent(13, 3));
		n2 = new Node(2);
		n2.addComponent(new SpatialNodeComponent(14, 2));
		n3 = new Node(3);
		n3.addComponent(new SpatialNodeComponent(15, 1));
		
		e0 = new Edge(1, 2);
		e0.setBidirectional(true);
		e0.addComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 2))));
		e0.addComponent(new CostListEdgeComponent(2., 3., 1., 5., 6., 3., 1.));
		
		e1 = new Edge(2, 3);
		e1.addComponent(new SpatialEdgeComponent(new Geometry(new Point(2, 3), new Point(4, 5))));
		e1.addComponent(new CostListEdgeComponent(1., 2., 1., 4., 7., 1., 2.));
		
		e2 = new Edge(0, 1);
		e2.addComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 3))));
		e2.addComponent(new CostListEdgeComponent(3., 4., 2., 6., 6., 4., 3.));
		
		e3 = new Edge(3, 0);
		e3.setBidirectional(true);
		e3.addComponent(new SpatialEdgeComponent(new Geometry()));
		e3.addComponent(new CostListEdgeComponent(2., 1., 2., 4., 8., 4., 2.));
		
		g.addNodes(n0, n1, n2, n3);
		g.addEdges(e0, e1, e2, e3);
		
		graphExample4 = GraphGenerator.getInstance().generateExample4();
	}

	@Test
	public void spatialTest() {
		assertEquals("N0 lng test", 4, n0.getComponent(SpatialNodeComponent.class).getLng(), 0);
		assertEquals("N1 lat test", 13, n1.getComponent(SpatialNodeComponent.class).getLat(), 0);
		assertEquals("N2 lat test", 14, n2.getComponent(SpatialNodeComponent.class).getLat(), 0);
		assertEquals("N3 lng test", 1, n3.getComponent(SpatialNodeComponent.class).getLng(), 0);
		
		assertEquals("get node 0 lat test", 12, g.getNode(0).getComponent(SpatialNodeComponent.class).getLat(), 0);
		
		e3.getComponent(SpatialEdgeComponent.class).getGeometry();
		e3.getComponent(CostListEdgeComponent.class).getMeanCost();
		
		assertArrayEquals("E2 Geometry test", new Point[] {new Point(1, 3)}, e2.getComponent(SpatialEdgeComponent.class).getGeometry().getPoints().toArray());
	}
	
	@Test
	public void temporalTest() {
		assertEquals("E0 specific cost test", 5., e0.getComponent(CostListEdgeComponent.class).getSpecificCost(3), 0.);
		assertEquals("E2 specific cost test", 4., e2.getComponent(CostListEdgeComponent.class).getSpecificCost(5), 0.);
		assertEquals("E1 mean cost test", 2.571, e1.getComponent(CostListEdgeComponent.class).getMeanCost(), 0.001);
		assertEquals("E3 mean cost test", 3.286, e3.getComponent(CostListEdgeComponent.class).getMeanCost(), 0.001);
	}
	
	@Test
	public void linearTimeDependentDijkstraTest() {
		ShortestPathStrategy dijkstra = new DijkstraStrategy(graphExample4);
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
		ShortestPathStrategy dijkstra = new DijkstraStrategy(graphExample4);
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

	@Test
	public void getSubClassAndOnAttachTest() {
		Edge e = new Edge();
		EdgeComponent component1 = new EdgeComponentSubClass1();
		EdgeComponent component2 = new EdgeComponentSubClass2();
		e.addComponent(component1);
		e.addComponent(EdgeComponentSuperClass.class, component2);
		assertEquals("SubClass Equals", EdgeComponentSubClass1.class, e.getComponent(EdgeComponentSubClass1.class).getClass());
		assertEquals("SuperClass Equals", EdgeComponentSubClass2.class, e.getComponent(EdgeComponentSuperClass.class).getClass());
		assertNull("Trying to get component with wrong key", e.getComponent(EdgeComponentSubClass2.class));
	}

	private abstract class EdgeComponentSuperClass extends EdgeComponent {
		@Override
		public void onAttach() {
			assertNotNull(getEdge());
		}
	}

	private class EdgeComponentSubClass1 extends EdgeComponentSuperClass { }

	private class EdgeComponentSubClass2 extends EdgeComponentSuperClass { }
}
