package org.insightlab.graphast.components;

import static org.junit.Assert.*;

import org.insightlab.graphast.model.Edge;
//import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.spatial_components.Geometry;
import org.insightlab.graphast.model.components.spatial_components.Point;
import org.insightlab.graphast.model.components.spatial_components.SpatialEdgeComponent;
import org.insightlab.graphast.model.components.spatial_components.SpatialGraphComponent;
import org.insightlab.graphast.model.components.spatial_components.SpatialNodeComponent;
import org.insightlab.graphast.model.components.temporal_components.TemporalEdgeComponent;
import org.junit.Before;
import org.junit.Test;

public class ComponentTest {

	Graph g;
	private Node n0, n1, n2, n3;
	private Edge e0, e1, e2, e3;
	
	@Before
	public void setUp() throws Exception {
		g = new Graph();
		g.addComponent(new SpatialGraphComponent());
		n0 = new Node(0);
		n0.addComponent(new SpatialNodeComponent(new Point(12, 4)));
		n1 = new Node(1);
		n1.addComponent(new SpatialNodeComponent(new Point(13, 3)));
		n2 = new Node(2);
		n2.addComponent(new SpatialNodeComponent(new Point(14, 2)));
		n3 = new Node(3);
		n3.addComponent(new SpatialNodeComponent(new Point(15, 1)));
		
		e0 = new Edge(1, 2, true);
		e0.addComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 2))));
		e0.addComponent(new TemporalEdgeComponent(2., 3., 1., 5., 6., 3., 1.));
		
		e1 = new Edge(2, 3);
		e1.addComponent(new SpatialEdgeComponent(new Geometry(new Point(2, 3), new Point(4, 5))));
		e1.addComponent(new TemporalEdgeComponent(1., 2., 1., 4., 7., 1., 2.));
		
		e2 = new Edge(0, 1);
		e2.addComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 3))));
		e2.addComponent(new TemporalEdgeComponent(3., 4., 2., 6., 6., 4., 3.));
		
		e3 = new Edge(3, 0, true);
		e3.addComponent(new SpatialEdgeComponent(new Geometry()));
		e3.addComponent(new TemporalEdgeComponent(2., 1., 2., 4., 8., 4., 2.));
	}

	@Test
	public void spatialTest() {
		assertEquals("N1 lat test", 13, n1.getComponent(SpatialNodeComponent.class).getLat());
		assertEquals("N3 lng test", 1, n3.getComponent(SpatialNodeComponent.class).getLng());
		System.out.println(e2.getComponent(SpatialEdgeComponent.class).getGeometry().getPoints().toArray());
		assertArrayEquals("E2 Geometry test", new Point[] {new Point(1, 3)}, e2.getComponent(SpatialEdgeComponent.class).getGeometry().getPoints().toArray());
	}
	
	@Test
	public void temporalTest() {
		assertEquals("E0 specific cost test", 5., e0.getComponent(TemporalEdgeComponent.class).getSpecificCost(3), 0.);
		assertEquals("E2 specific cost test", 4., e2.getComponent(TemporalEdgeComponent.class).getSpecificCost(5), 0.);
		assertEquals("E1 mean cost test", 2.571, e1.getComponent(TemporalEdgeComponent.class).getMeanCost(), 0.001);
		assertEquals("E3 mean cost test", 3.286, e3.getComponent(TemporalEdgeComponent.class).getMeanCost(), 0.001);
	}

}
