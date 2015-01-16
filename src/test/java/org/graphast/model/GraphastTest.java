package org.graphast.model;

import static org.junit.Assert.assertEquals;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.geometry.Point;
import org.junit.Before;
import org.junit.Test;

public class GraphastTest {
	
	private Graphast graph;
	
	private String dir = "/tmp/fastgraph";
	
	private long vId0, vId3, vId5;
	private long eId0, eId1;
	private String labelNode0 = "label node 0";
	
	@Before
	public void setup(){
		
		graph = new GraphastImpl(dir);
		
		GraphastNode v = new GraphastNode(3l, 10d, 10d, labelNode0);
		graph.addNode(v);
		vId0 = v.getId();
		
		v = new GraphastNode(4l, 10d, 20d);
		graph.addNode(v);
		
		v = new GraphastNode(2l, 10d, 30d);
		graph.addNode(v);
		
		v = new GraphastNode(6l, 10d, 40d);
		graph.addNode(v);
		vId3 = v.getId();
		
		v = new GraphastNode(7l, 11d, 32d);
		graph.addNode(v);
		
		v = new GraphastNode(7, 11, 32, "Banco");
		graph.addNode(v);
		vId5 = v.getId();
		
		short[] costs = {1,2,3,4};
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,20));
		GraphastEdge e = new GraphastEdge(0l, 1l, 10, costs, points, "rua1");
//		public GraphastEdge(long fromNode, long toNode, int distance,
//				short[] costs, List<Point> geometry, String label)
		graph.addEdge(e);
		eId0 = e.getId();
		
		costs = new short[]{2,4,6,8,10};
		points = new ArrayList<Point>();
		points.add(new Point(10,20));
		points.add(new Point(10,15));
		points.add(new Point(10,10));
		e = new GraphastEdge(1l, 0l, 20, costs, points, "rua2");
		graph.addEdge(e);
		eId1 = e.getId();
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,30));
		e = new GraphastEdge(0l, 2l, 20, costs, points, "rua3");
		graph.addEdge(e);
		
		costs = new short[]{2};
		points = new ArrayList<Point>();
		points.add(new Point(10,30));
		points.add(new Point(10,10));
		e = new GraphastEdge(2l, 0l, 20, costs, points, "rua4");
		graph.addEdge(e);
		
		costs = new short[]{3};
		points = new ArrayList<Point>();
		points.add(new Point(10,10));
		points.add(new Point(10,40));
		e = new GraphastEdge(0l, 3l, 20, costs, points, "");
		graph.addEdge(e);
		
		e = new GraphastEdge(2l, 4l, 20);
		graph.addEdge(e);
		
		e = new GraphastEdge(3l, 0l, 50);
		graph.addEdge(e);
	}
	
	@Test
	public void storeGetNode(){
		GraphastNode v = graph.getNode(0);
		assertEquals(0, (long) v.getId());
		assertEquals(3, (long) BigArrays.index(v.getExternalIdSegment(), v.getExternalIdOffset()));
		assertEquals(0, (long) BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset()));
		assertEquals(10d, v.getLatitude(), 0);
		assertEquals(10d, v.getLongitude(), 0);
		
		v = graph.getNode(1);
		assertEquals(1, (long) v.getId());
		assertEquals(4, (long) BigArrays.index(v.getExternalIdSegment(), v.getExternalIdOffset()));
		assertEquals(0, (long) BigArrays.index(v.getFirstEdgeSegment(), v.getFirstEdgeOffset()));
		assertEquals(10d, v.getLatitude(), 0);
		assertEquals(20d, v.getLongitude(), 0);
	}
	
	@Test
	public void storeGetEdge() {
		GraphastEdge e = graph.getEdge(eId0);
		assertEquals(0, (long) e.getId());
		assertEquals(0, (long) e.getFromNode());
		assertEquals(1, (long) e.getToNode());
		assertEquals(10, e.getDistance());
		assertEquals(0, (long) e.getCostsIndex());
		assertEquals(0, (long) e.getGeometryIndex());
		
		e = graph.getEdge(eId1);
		assertEquals(1, (long) e.getId());
		assertEquals(1, (long) e.getFromNode());
		assertEquals(0, (long) e.getToNode());
		assertEquals(20, e.getDistance());
		assertEquals(5, (long) e.getCostsIndex());
		assertEquals(5, (long) e.getGeometryIndex());
	}
	
	@Test
	public void getCostsTest(){
		short[] costs = graph.getEdgeCosts(0);
		assertEquals(1, costs[0]);
		assertEquals(2, costs[1]);
		assertEquals(3, costs[2]);
		assertEquals(4, costs[3]);
		
		costs = graph.getEdgeCosts(1);
		assertEquals(2, costs[0]);
		assertEquals(4, costs[1]);
		assertEquals(6, costs[2]);
		assertEquals(8, costs[3]);
		assertEquals(10, costs[4]);
	}
	
	@Test
	public void getEdgeCostTest(){
		GraphastEdge edge = graph.getEdge(0);
		assertEquals(1, graph.getEdgeCost(edge, 3600));
		assertEquals(1, graph.getEdgeCost(edge, 7200));
		assertEquals(2, graph.getEdgeCost(edge, 36000));
		assertEquals(3, graph.getEdgeCost(edge, 61200));
		assertEquals(4, graph.getEdgeCost(edge, 75600));
		
		edge = graph.getEdge(1);
		assertEquals(2, graph.getEdgeCost(edge, 3600));
		assertEquals(2, graph.getEdgeCost(edge, 7200));
		assertEquals(6, graph.getEdgeCost(edge, 36000));
		assertEquals(8, graph.getEdgeCost(edge, 61200));
		assertEquals(10, graph.getEdgeCost(edge, 75600));
	}
	
	@Test
	public void getEdgePointsTest(){
		List<Point> points = graph.getEdgePoints(0);
		assertEquals((Double) 10.0,  (Double)points.get(0).getLatitude());
		assertEquals((Double) 10.0,  (Double)points.get(0).getLongitude());
		
		points = graph.getEdgePoints(1);
		int size = points.size() - 1;
		assertEquals((Double) 10.0,  (Double)points.get(size).getLatitude());
		assertEquals((Double) 10.0,  (Double)points.get(size).getLongitude());
	}
	
	@Test
	public void getEdgeLabelTest(){
		assertEquals("rua1", graph.getEdgeLabel(0));
		assertEquals("rua2", graph.getEdgeLabel(1));
	}
	
	@Test
	public void getNodeLabelTest(){
		assertEquals("Banco", graph.getNodeLabel(vId5));
	}
	
	@Test
	public void getOutNeighborsAndCostsTest(){
		LongList neig = graph.getOutNeighborsAndCosts(0, 36000);
		int pos = 0;
		
		assertEquals(1, (long) neig.get(pos++));
		assertEquals(2,  (long) neig.get(pos++));
		

		assertEquals(2,  (long) neig.get(pos++));
		assertEquals(2, (long) neig.get(pos++));
		
		assertEquals(3,  (long) neig.get(pos++));
		assertEquals(3, (long) neig.get(pos++));
		
		neig = graph.getOutNeighbors(2);
		pos = 0;
		assertEquals(0,  (long) neig.get(pos++));
		assertEquals(4, (long) neig.get(pos++));
		
		neig = graph.getOutNeighbors(3);
		pos = 0;
		assertEquals(0,  (long) neig.get(pos++));
	}
	
	@Test
	public void saveLoadTest() throws IOException{
		graph.save();
		Graphast graph2 = new GraphastImpl(dir);
		graph2.load();
		assertEquals(graph.getNodes(), graph2.getNodes());
		assertEquals(graph.getEdges(), graph2.getEdges());
		assertEquals(graph.getCosts(), graph2.getCosts());

		GraphastNode node = graph.getNode(vId0);
		assertEquals(vId0, (long)graph2.getNode(node.getLatitude(), node.getLongitude()));
		assertEquals(labelNode0, graph2.getNode(vId0).getLabel());
	}
	
	@Test
	public void getNode() throws IOException{
		GraphastNode n = graph.getNode(3);
		
		assertEquals(3, (long)n.getId());
		assertEquals(6, BigArrays.index(n.getExternalIdSegment(), n.getExternalIdOffset()));
	}
	
	@Test
	public void getNode2(){
		
		long n = graph.getNode(10, 40);
		assertEquals(vId3, n);
		
	}
	
	@Test
	public void getNumberOfNodesTest() {
		
		assertEquals(6, graph.getNumberOfNodes());
		
	}
	
	@Test
	public void getNumberOfEdgesTest() {
		
		assertEquals(7, graph.getNumberOfEdges());
		
	}
	
	@Test
	public void getOutEdgesTest() {
		LongList l = graph.getOutEdges(vId0);
		assertEquals(0, (long) l.get(0));
		assertEquals(2, (long) l.get(1));
		assertEquals(4, (long) l.get(2));
		
		l = graph.getOutEdges(vId3);
		assertEquals(6, (long)l.get(0));
	}

}
