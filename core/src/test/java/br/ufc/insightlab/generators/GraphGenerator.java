package br.ufc.insightlab.generators;

import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.model.Node;
import br.ufc.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;
import br.ufc.insightlab.graphast.model.components.cost_list_components.CostListNodeComponent;
import br.ufc.insightlab.graphast.model.components.spatial_components.Geometry;
import br.ufc.insightlab.graphast.model.components.spatial_components.Point;
import br.ufc.insightlab.graphast.model.components.spatial_components.SpatialEdgeComponent;
import br.ufc.insightlab.graphast.model.components.spatial_components.SpatialNodeComponent;

public class GraphGenerator {
	
	private static GraphGenerator instance = null;
	
	public static GraphGenerator getInstance() {
		if (instance == null)
			instance = new GraphGenerator();
		return instance;
	}
	
	private GraphGenerator() {}
	
	public Graph generateExample1() {

		Graph graph = new Graph();

		Node n1 = new Node(0l);
		n1.addComponent(new SpatialNodeComponent(10., 10.));

		Node n2 = new Node(1l);
		n2.addComponent(new SpatialNodeComponent(43.7294668047756, 7.413772473047058));

		Node n3 = new Node(2l);
		n3.addComponent(new SpatialNodeComponent(10., 30.));
		n3.addComponent(new CostListNodeComponent(1., 2., 3., 4.));

		Node n4 = new Node(3l);
		n4.addComponent(new SpatialNodeComponent(10., 40.));

		Node n5 = new Node(4l);
		n5.addComponent(new SpatialNodeComponent(11., 32.));

		graph.addNodes(n1, n2, n3, n4, n5);

		
		CostsGenerator costsGenerator = CostsGenerator.getInstance();
		
		Edge e1 = new Edge(0l, 1l, 10);
		e1.addComponent(new SpatialEdgeComponent(new Geometry(new Point(10.,10.), new Point(10.,20.))));
		e1.addComponent(new CostListEdgeComponent(costsGenerator.generateCosts(10)));
		
		Edge e2 = new Edge(1l, 0l, 20);
		e2.addComponent(new SpatialEdgeComponent(new Geometry(new Point(10.,20.), new Point(10.,15.), new Point(10., 10.))));
		e2.addComponent(new CostListEdgeComponent(costsGenerator.generateCosts(20)));
		
		Edge e3 = new Edge(0l, 2l, 30);
		e3.addComponent(new SpatialEdgeComponent(new Geometry(new Point(10.,10.), new Point(10.,30.))));
		e3.addComponent(new CostListEdgeComponent(costsGenerator.generateCosts(30)));

		Edge e4 = new Edge(2l, 0l, 40);
		e4.addComponent(new SpatialEdgeComponent(new Geometry(new Point(10.,30.), new Point(10.,10.))));
		e4.addComponent(new CostListEdgeComponent(costsGenerator.generateCosts(40)));

		Edge e5 = new Edge(0l, 3l, 50);
		e5.addComponent(new SpatialEdgeComponent(new Geometry(new Point(10.,10.), new Point(10.,40.))));
		e5.addComponent(new CostListEdgeComponent(costsGenerator.generateCosts(50)));

		Edge e6 = new Edge(2l, 4l, 60);

		Edge e7 = new Edge(3l, 0l, 70);
		
		graph.addEdges(e1, e2, e3, e4, e5, e6, e7);

		return graph;
	}
	
	public Graph generateExample2() {

		Graph graph = new Graph();

		Node n0 = new Node(0l);
		n0.addComponent(new SpatialNodeComponent(0., 10.));
		Node n1 = new Node(1l);
		n1.addComponent(new SpatialNodeComponent(10., 0.));
		Node n2 = new Node(2l);
		n2.addComponent(new SpatialNodeComponent(30., 20.));
		Node n3 = new Node(3l);
		n3.addComponent(new SpatialNodeComponent(40., 20.));
		Node n4 = new Node(4l);
		n4.addComponent(new SpatialNodeComponent(50., 30.));
		Node n5 = new Node(5l);
		n5.addComponent(new SpatialNodeComponent(60., 20.));
		Node n6 = new Node(6l);
		n6.addComponent(new SpatialNodeComponent(60., 0.));
		
		graph.addNodes(n0, n1, n2, n3, n4, n5, n6);

		Edge e0 = new Edge(0l, 1l, 1);
		e0.addComponent(new CostListEdgeComponent(3., 2., 3., 4.));
		Edge e1 = new Edge(0l, 2l, 5);
		e1.addComponent(new CostListEdgeComponent(4., 2., 6., 8., 10.));
		Edge e2 = new Edge(1l, 2l, 3);
		e2.addComponent(new CostListEdgeComponent(1., 2.));
		Edge e3 = new Edge(2l, 3l, 3);
		e3.addComponent(new CostListEdgeComponent(4., 4., 7., 6., 11.));
		Edge e4 = new Edge(3l, 4l, 3);
		e4.addComponent(new CostListEdgeComponent(1., 10.));
		Edge e5 = new Edge(3l, 5l, 4);
		e5.addComponent(new CostListEdgeComponent(2., 12., 13.));
		Edge e6 = new Edge(4l, 5l, 2);
		e6.addComponent(new CostListEdgeComponent(3., 9., 10., 11.));
		Edge e7 = new Edge(5l, 6l, 1);
		e7.addComponent(new CostListEdgeComponent(5., 2., 4., 6., 8., 15.));

		graph.addEdges(e0, e1, e2, e3, e4, e5, e6, e7);

		return graph;

	}
	
	public Graph generateExample4() {
		Graph graph = new Graph();

		Node n0 = new Node(0l);
		n0.addComponent(new SpatialNodeComponent(-3.74077, -38.55735));
		Node n1 = new Node(1l);
		n1.addComponent(new SpatialNodeComponent(-3.74003, -38.55693));
		Node n2 = new Node(2l);
		n2.addComponent(new SpatialNodeComponent(-3.74049, -38.5563));
		Node n3 = new Node(3l);
		n3.addComponent(new SpatialNodeComponent(-3.74035, -38.55526));
		Node n4 = new Node(4l);
		n4.addComponent(new SpatialNodeComponent(-3.73958, -38.55479));
		Node n5 = new Node(5l);
		n5.addComponent(new SpatialNodeComponent(-3.74001, -38.55415));
		Node n6 = new Node(6l);
		n6.addComponent(new SpatialNodeComponent(-3.7412, -38.55388));
		
		graph.addNodes(n0, n1, n2, n3, n4, n5, n6);

		Edge e0 = new Edge(0l, 1l, 1);
		e0.addComponent(new CostListEdgeComponent(3., 1.));
		Edge e1 = new Edge(0l, 2l, 1);
		e1.addComponent(new CostListEdgeComponent(5.));
		Edge e2 = new Edge(1l, 2l, 1);
		e2.addComponent(new CostListEdgeComponent(3.));
		Edge e3 = new Edge(2l, 3l, 1);
		e3.addComponent(new CostListEdgeComponent(3.));
		Edge e4 = new Edge(3l, 4l, 1);
		e4.addComponent(new CostListEdgeComponent(3.));
		Edge e5 = new Edge(3l, 5l, 1);
		e5.addComponent(new CostListEdgeComponent(6., 4.));
		Edge e6 = new Edge(4l, 5l, 1);
		e6.addComponent(new CostListEdgeComponent(2.));
		Edge e7 = new Edge(5l, 6l, 1);
		e7.addComponent(new CostListEdgeComponent(1.));

		graph.addEdges(e0, e1, e2, e3, e4, e5, e6, e7);

		return graph;
	}
	
	public Graph generateExample6() {
		Graph graph = new Graph();
		Node n0 = new Node(60054477);
		n0.addComponent(new SpatialNodeComponent(52.3524, 20.9440));
		Node n1 = new Node(60054510);	
		n1.addComponent(new SpatialNodeComponent(52.3515, 20.9447));
		Node n2 = new Node(249476123);
		n2.addComponent(new SpatialNodeComponent(52.3499, 20.9461));
		Node n3 = new Node(252417127);
		n3.addComponent(new SpatialNodeComponent(52.3509, 20.9452));
		Node n4 = new Node(252417130);
		n4.addComponent(new SpatialNodeComponent(52.3514, 20.9449));
		
		graph.addNodes(n0, n1, n2, n3, n4);

		graph.addEdge(new Edge(n4.getId(), n0.getId(), 121)); // 252417130 60054477 0.121
		graph.addEdge(new Edge(n0.getId(), n1.getId(), 112)); // 60054477 60054510 0.112
		graph.addEdge(new Edge(n3.getId(), n2.getId(), 123)); // 252417127 249476123 0.123
		graph.addEdge(new Edge(n2.getId(), n2.getId(), 0));   
		graph.addEdge(new Edge(n1.getId(), n2.getId(), 195)); // 60054510 249476123 0.195
		
		return graph;
	}

}
