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
import org.junit.Before;
import org.junit.Test;

public class SpatialComponentTest {

	Graph g;
	private Node n0, n1, n2, n3;
	private Edge e0, e1, e2, e3;
	
	@Before
	public void setUp() throws Exception {
		g = new Graph();
		g.setComponent(new SpatialGraphComponent());
		n0 = new Node(0);
		n0.setComponent(new SpatialNodeComponent(new Point(12, 4)));
		n1 = new Node(1);
		n1.setComponent(new SpatialNodeComponent(new Point(13, 3)));
		n2 = new Node(2);
		n2.setComponent(new SpatialNodeComponent(new Point(14, 2)));
		n3 = new Node(3);
		n3.setComponent(new SpatialNodeComponent(new Point(15, 1)));
		
		e0 = new Edge(1, 2, true);
		e0.setComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 2))));
		e1 = new Edge(2, 3);
		e1.setComponent(new SpatialEdgeComponent(new Geometry(new Point(2, 3), new Point(4, 5))));
		e2 = new Edge(0, 1);
		e2.setComponent(new SpatialEdgeComponent(new Geometry(new Point(1, 3))));
		e3 = new Edge(3, 0, true);
		e3.setComponent(new SpatialEdgeComponent(new Geometry()));
	}

	@Test
	public void test() {
		assertEquals("N1 lat test", 13, n1.getComponent(SpatialNodeComponent.class).getLat());
		assertEquals("N3 lng test", 1, n3.getComponent(SpatialNodeComponent.class).getLng());
		System.out.println(e2.getComponent(SpatialEdgeComponent.class).getGeometry().getPoints().toArray());
		assertArrayEquals("E2 Geometry test", new Point[] {new Point(1, 3)}, e2.getComponent(SpatialEdgeComponent.class).getGeometry().getPoints().toArray());
	}

}
