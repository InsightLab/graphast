package org.graphast.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.enums.TimeType;
import org.graphast.geometry.BBox;
import org.graphast.geometry.Point;
import org.graphast.graphgenerator.GraphGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.LongList;

public class GraphTest {

	//private static GraphImpl graphExample1;
	private static Graph graphExample2;
	private static GraphImpl graphExample3;
	private static GraphBoundsImpl graphExample4;	
	private static GraphImpl graphExample;
	private static GraphBounds graphMonaco;

	@BeforeClass
	public static void setup(){
		graphExample = (GraphImpl) new GraphGenerator().generateExample();
		//graphExample1 = (GraphImpl) new GraphGenerator().generateExample1();
		graphExample2 = new GraphGenerator().generateExample2();
		graphExample3 =  (GraphImpl) new GraphGenerator().generateExample3();
		graphExample4 =  (GraphBoundsImpl) new GraphGenerator().generateExample4();
		graphExample4.setTimeType(TimeType.MINUTE);
		graphMonaco = new GraphGenerator().generateMonaco();
	}

	@Test
	public void storeGetNode() {
		Node v = graphExample3.getNode(0);
		assertEquals(0, (long) v.getId());
		assertEquals(3, v.getExternalId());
		assertEquals(10d, v.getLatitude(), 0);
		assertEquals(10d, v.getLongitude(), 0);

		v = graphExample3.getNode(1);
		assertEquals(1, (long) v.getId());
		assertEquals(4, v.getExternalId());
		assertEquals(43.729467, v.getLatitude(), 0);
		assertEquals(7.413772, v.getLongitude(), 0);
	}

	@Test
	public void storeGetEdge() {
		EdgeImpl e = (EdgeImpl)graphExample3.getEdge(0);
		assertEquals(0, (long) e.getId());
		assertEquals(0, (long) e.getFromNode());
		assertEquals(1, (long) e.getToNode());
		assertEquals(10, e.getDistance());
		assertEquals(0, (long) e.getCostsIndex());
		assertEquals(0, (long) e.getGeometryIndex());

		e = (EdgeImpl)graphExample3.getEdge(1);
		assertEquals(1, (long) e.getId());
		assertEquals(1, (long) e.getFromNode());
		assertEquals(0, (long) e.getToNode());
		assertEquals(20, e.getDistance());
		assertEquals(5, (long) e.getCostsIndex());
		assertEquals(5, (long) e.getGeometryIndex());
	}
	
	@Test
	public void getEdgeSyntheticCost() {
//		Edge edge = graphExample1.getEdge(0);
		
//		assertEquals(147508, (int)graphExample1.getEdgeCost(edge, 3600000));
//		assertEquals(0, (int)graphExample1.getEdgeCost(edge, 7200000));
//		assertEquals(0, (int)graphExample1.getEdgeCost(edge, 36000000));
//		assertEquals(0, (int)graphExample1.getEdgeCost(edge, 61200000));
//		assertEquals(0, (int)graphExample1.getEdgeCost(edge, 75600000));
	}

	@Test
	public void getEdgeCostTest() {
		Edge edge = graphExample3.getEdge(0);
		assertEquals((Integer)1, graphExample3.getEdgeCost(edge, 3600000));
		assertEquals((Integer)1, graphExample3.getEdgeCost(edge, 7200000));
		assertEquals((Integer)2, graphExample3.getEdgeCost(edge, 36000000));
		assertEquals((Integer)3, graphExample3.getEdgeCost(edge, 61200000));
		assertEquals((Integer)4, graphExample3.getEdgeCost(edge, 75600000));

		edge = graphExample3.getEdge(1);
		assertEquals((Integer)2, graphExample3.getEdgeCost(edge, 3600000));
		assertEquals((Integer)2, graphExample3.getEdgeCost(edge, 7200000));
		assertEquals((Integer)6, graphExample3.getEdgeCost(edge, 36000000));
		assertEquals((Integer)8, graphExample3.getEdgeCost(edge, 61200000));
		assertEquals((Integer)10, graphExample3.getEdgeCost(edge, 75600000));

		// No costs test
		edge = graphExample3.getEdge(5);
		assertNull(graphExample3.getEdgeCost(edge, 3600000));
		assertNull(graphExample3.getEdgeCost(edge, 7200000));
		assertNull(graphExample3.getEdgeCost(edge, 36000000));
		assertNull(graphExample3.getEdgeCost(edge, 61200000));
		assertNull(graphExample3.getEdgeCost(edge, 75600000));
		
	}

	@Test
	public void getGeometryTest() {
		List<Point> points = graphExample3.getGeometry(0);
		assertEquals((Double) 10.0,  (Double)points.get(0).getLatitude());
		assertEquals((Double) 10.0,  (Double)points.get(0).getLongitude());
		assertEquals((Double) 10.0,  (Double)points.get(1).getLatitude());
		assertEquals((Double) 20.0,  (Double)points.get(1).getLongitude());

		points=graphExample3.getEdge(0).getGeometry();
		assertEquals((Double) 10.0,  (Double)points.get(0).getLatitude());
		assertEquals((Double) 10.0,  (Double)points.get(0).getLongitude());


		points = graphExample3.getGeometry(1);
		assertEquals((Double) 10.0,  (Double)points.get(1).getLatitude());
		assertEquals((Double) 15.0,  (Double)points.get(1).getLongitude());
	}

	@Test
	public void getEdgeLabelTest() {
		assertEquals("rua1", graphExample3.getEdgeLabel(0));
		assertEquals("rua2", graphExample3.getEdgeLabel(1));
		// No label test
		assertNull(graphExample3.getEdgeLabel(5));
	}

	@Test
	public void getNodeLabelTest() {
		assertEquals("label node 0", graphExample3.getNodeLabel(0));
		assertEquals("Banco", graphExample3.getNodeLabel(5));
		// No label test
		assertNull(graphExample3.getNodeLabel(1));
	}

	@Test
	public void getOutNeighborsAndCostsTest() {
		LongList neig = graphExample3.getOutNeighborsAndCosts(0, 36000000);
		int pos = 0;

		assertEquals(1, (long) neig.get(pos++));
		assertEquals(2,  (long) neig.get(pos++));


		assertEquals(2,  (long) neig.get(pos++));
		assertEquals(2, (long) neig.get(pos++));

		assertEquals(3,  (long) neig.get(pos++));
		assertEquals(3, (long) neig.get(pos++));

		neig = graphExample3.getOutNeighbors(2);
		pos = 0;
		assertEquals(0,  (long) neig.get(pos++));
		assertEquals(4, (long) neig.get(pos++));

		neig = graphExample3.getOutNeighbors(3);
		pos = 0;
		assertEquals(0,  (long) neig.get(pos++));

		LongList neig4 = graphExample4.getOutNeighborsAndCosts(0, 600);
		int pos4 = 0;

		assertEquals(1, (long) neig4.get(pos4++));
		assertEquals(3,  (long) neig4.get(pos4++));

	}

	@Test
	public void saveLoadTest() throws IOException{
		graphExample3.save();
		GraphImpl graph2 = new GraphImpl(Configuration.USER_HOME + "/graphast/test/example3");
		graph2.load();
		assertEquals(graphExample3.getNodes(), graph2.getNodes());
		assertEquals(graphExample3.getEdges(), graph2.getEdges());
		assertEquals(graphExample3.getCosts(), graph2.getCosts());

		Node node = graphExample3.getNode(0);
		assertEquals(0, (long)graph2.getNodeId(node.getLatitude(), node.getLongitude()));
		assertEquals(node.getLatitude(), graph2.getNode(0).getLatitude(),0);
		assertEquals(node.getLongitude(), graph2.getNode(0).getLongitude(),0);


		assertEquals("label node 0", graph2.getNode(0).getLabel());
	}

	@Test
	public void getNode() throws IOException{
		Node n = graphExample3.getNode(3);

		assertEquals(3, (long)n.getId());
		assertEquals(6, n.getExternalId());
	}

	@Test
	public void getNode2(){
		long n = graphExample3.getNodeId(10d, 40d);
		assertEquals(3, n);
	}

	@Test
	public void getNumberOfNodesTest() {
		assertEquals(6, graphExample3.getNumberOfNodes());
	}

	@Test
	public void getNumberOfEdgesTest() {
		assertEquals(7, graphExample3.getNumberOfEdges());
	}

	@Test
	public void getOutEdgesTest() {
		LongList l = graphExample3.getOutEdges(0);
		assertEquals(0, (long) l.get(0));
		assertEquals(2, (long) l.get(1));
		assertEquals(4, (long) l.get(2));

		l = graphExample3.getOutEdges(3);
		assertEquals(6, (long)l.get(0));
	}

	@Test
	public void accessNeighborhoodTest() {
		Long2IntMap neighbors = graphExample3.accessNeighborhood(graphExample3.getNode(3));
		//number of neighbors
		assertEquals(1, neighbors.size());
		assertEquals(70 ,neighbors.get(0));

		neighbors = graphExample3.accessNeighborhood(graphExample3.getNode(0));
		//number of neighbors
		assertEquals(3, neighbors.size());
		assertEquals(30, neighbors.get(2));
		assertEquals(50, neighbors.get(3));
		assertEquals(10, neighbors.get(1));

		neighbors = graphExample.accessNeighborhood(graphExample.getNode(1));
		assertEquals(3, neighbors.size());
	}

	@Test
	public void getEdgeLabel() {
		Edge e =  graphExample.getEdge(0);
		assertEquals("Named Street 1", graphExample.getEdgeLabel(0));
		assertEquals("Named Street 1", e.getLabel());

		// No label test
		e = graphExample3.getEdge(5);
		assertNull(graphExample3.getEdgeLabel(5));
	}

	@Test
	public void getEdgeCostsTest() {

		assertEquals(4, graphExample3.getEdgeCosts(0).length);
		assertEquals(5, graphExample3.getEdgeCosts(1).length);
		assertEquals(1, graphExample3.getEdgeCosts(2).length);
		assertEquals(5, graphExample3.getEdgeCosts(3).length);
		assertEquals(1, graphExample3.getEdgeCosts(4).length);
		//No costs test
		assertNull(graphExample3.getEdgeCosts(5));
		assertNull(graphExample3.getEdgeCosts(6));
	}

	@Test
	public void getNodeCostsTest() {

		assertEquals(4, graphExample3.getNodeCosts(2).length);
		// No costs test
		assertNull(graphExample3.getNodeCosts(0));
		assertNull(graphExample3.getNodeCosts(1));


	}

	@Test
	public void getNodeCategory() {
		assertArrayEquals ( new int[]{0, 2, 4, 1}, graphExample2.getCategories().toIntArray());
		assertEquals(4, graphExample2.getCategories().size());

		// no category
		assertEquals(0, graphExample.getCategories().size());
	}

	@Test
	public void setEdgeCostsTest() {

		int[] costs = {3,2,3,4};
		graphExample.setEdgeCosts(1, costs);
		int[] costs2 = {3,2};
		graphExample.setEdgeCosts(1, costs2);

		assertArrayEquals ( new int[]{-4, 3, 2, 3, 4, 2, 3, 2}, graphExample.getCosts().toIntArray());

	}

	@Test
	public void setNodeCostsTest() {

		int[] costs = {9,8,7,6};
		graphExample.setNodeCosts(1, costs);
		int[] costs2 = {5,4};
		graphExample.setNodeCosts(1, costs2);

		assertArrayEquals ( new int[]{-4, 9, 8, 7, 6, 2, 5, 4}, graphExample.getNodesCosts().toIntArray());

	}

	@Test
	public void getNearestNodeTest() {
		assertEquals(2, (long) graphExample3.getNearestNode(10d, 31d).getId());
		assertEquals(380, (long) graphMonaco.getNearestNode(43.738331, 7.421239).getId());
		assertEquals(253, (long) graphMonaco.getNearestNode(43.7294668047756, 7.413772473047058).getId());
		assertEquals(252, (long) graphMonaco.getNearestNode(43.73079058671274, 7.415815422292399).getId());
	}
	
//	@Test
//	public void importPoIs() throws NumberFormatException, IOException {
//		System.out.println(graphMonaco.getNearestNode(43.7307036,7.4170275).getLabel());
//		graphMonaco.importPoIList();
//		System.out.println(graphMonaco.getNearestNode(43.7307036,7.4170275).getLabel());
//	}

	@Test
	public void getReverseGraph() {

		graphExample.reverseGraph();

		assertEquals(1, graphExample.getEdge(0).getFromNode());
		assertEquals(0, graphExample.getEdge(0).getToNode());
		assertEquals(1, graphExample.getEdge(0).getFromNodeNextEdge());
		assertEquals(1, graphExample.getEdge(0).getToNodeNextEdge());

		assertEquals(0, graphExample.getEdge(1).getFromNode());
		assertEquals(1, graphExample.getEdge(1).getToNode());
		assertEquals(-1, graphExample.getEdge(1).getFromNodeNextEdge());
		assertEquals(2, graphExample.getEdge(1).getToNodeNextEdge());

		assertEquals(2, graphExample.getEdge(2).getFromNode());
		assertEquals(1, graphExample.getEdge(2).getToNode());
		assertEquals(3, graphExample.getEdge(2).getFromNodeNextEdge());
		assertEquals(9, graphExample.getEdge(2).getToNodeNextEdge());

		assertEquals(3, graphExample.getEdge(3).getFromNode());
		assertEquals(2, graphExample.getEdge(3).getToNode());
		assertEquals(4, graphExample.getEdge(3).getFromNodeNextEdge());
		assertEquals(4, graphExample.getEdge(3).getToNodeNextEdge());

		assertEquals(2, graphExample.getEdge(4).getFromNode());
		assertEquals(3, graphExample.getEdge(4).getToNode());
		assertEquals(-1, graphExample.getEdge(4).getFromNodeNextEdge());
		assertEquals(5, graphExample.getEdge(4).getToNodeNextEdge());

		assertEquals(4, graphExample.getEdge(5).getFromNode());
		assertEquals(3, graphExample.getEdge(5).getToNode());
		assertEquals(6, graphExample.getEdge(5).getFromNodeNextEdge());
		assertEquals(6, graphExample.getEdge(5).getToNodeNextEdge());

		assertEquals(3, graphExample.getEdge(6).getFromNode());
		assertEquals(4, graphExample.getEdge(6).getToNode());
		assertEquals(-1, graphExample.getEdge(6).getFromNodeNextEdge());
		assertEquals(7, graphExample.getEdge(6).getToNodeNextEdge());

		assertEquals(5, graphExample.getEdge(7).getFromNode());
		assertEquals(4, graphExample.getEdge(7).getToNode());
		assertEquals(8, graphExample.getEdge(7).getFromNodeNextEdge());
		assertEquals(8, graphExample.getEdge(7).getToNodeNextEdge());

		assertEquals(4, graphExample.getEdge(8).getFromNode());
		assertEquals(5, graphExample.getEdge(8).getToNode());
		assertEquals(-1, graphExample.getEdge(8).getFromNodeNextEdge());
		assertEquals(9, graphExample.getEdge(8).getToNodeNextEdge());

		assertEquals(5, graphExample.getEdge(9).getFromNode());
		assertEquals(1, graphExample.getEdge(9).getToNode());
		assertEquals(-1, graphExample.getEdge(9).getFromNodeNextEdge());
		assertEquals(-1, graphExample.getEdge(9).getToNodeNextEdge());

	}
	
	@Test
	public void equalsTest() {
		// case "true"
		assertTrue(graphExample.equals(graphExample));
		assertTrue(graphExample2.equals(graphExample2));
		assertTrue(graphExample3.equals(graphExample3));
		assertTrue(graphExample4.equals(graphExample4));
		assertTrue(graphMonaco.equals(graphMonaco));
		
		//case "false"
		assertFalse(graphExample.equals(graphExample2));
		assertFalse(graphExample2.equals(graphExample3));
		assertFalse(graphExample3.equals(graphExample4));
		assertFalse(graphExample4.equals(graphMonaco));
		assertFalse(graphMonaco.equals(graphExample));
	}

	@Test 
	public void addPoiTest() {
		Graph graph = new GraphImpl(Configuration.USER_HOME + "/graphhopper/test/poiTest");
		graph.addPoi(0, 1, 2, 1);
		assertEquals(0, graph.getPoi(0).getExternalId());
		assertEquals(1, (int)graph.getPoi(0).getLatitude());
		assertEquals(2, (int)graph.getPoi(0).getLongitude());
		assertEquals(1, graph.getPoi(0).getCategory());
	}
	
	@Test 
	public void getEdgeTest() {
		
		assertEquals(Long.valueOf(1), graphExample2.getEdge(0, 2).getId());
		
	}
		
	@Test
	public void setGeometryTest() {
		Graph graphTest = graphExample;
		List<Point> geometry = new ArrayList<Point>();
		Point p = new Point(1,2);
		geometry.add(p);
		graphTest.setEdgeGeometry(0, geometry);
		
		assertEquals(1, (int)graphTest.getEdge(0).getGeometry().get(0).getLatitude());
		assertEquals(2, (int)graphTest.getEdge(0).getGeometry().get(0).getLongitude());
	}
	
	@Test
	public void getBBoxTest() {
		BBox bBox = graphMonaco.getBBox();
		assertEquals(43.723389, bBox.getMinLatitude(), 0);
		assertEquals(7.407121, bBox.getMinLongitude(), 0);
		assertEquals(43.751963, bBox.getMaxLatitude(), 0);
		assertEquals(7.439278, bBox.getMaxLongitude(), 0);
	}
	
	@Test
	public void linearFunctionArrayToCostIntArrayTest() {
		GraphBoundsImpl graph = (GraphBoundsImpl)graphExample4;
		LinearFunction linearFunction = new LinearFunction(0,10,graph.getMaxTime(),10);
		int[] costArray = graph.linearFunctionArrayToCostIntArray(new LinearFunction[]{linearFunction});
		assertEquals(10,costArray[0]);
		assertEquals(1, costArray.length);
		
		linearFunction = new LinearFunction(0,0,graph.getMaxTime(),10);
		costArray = graph.linearFunctionArrayToCostIntArray(new LinearFunction[]{linearFunction});
		assertEquals(5,costArray[0]);
		assertEquals(1, costArray.length);
	}
	
	@Test
	public void convertToLinearFunctionTest() {
		GraphBoundsImpl graph = (GraphBoundsImpl)graphExample4;
		LinearFunction linearFunction = new LinearFunction(0,10,graph.getMaxTime(),10);
		LinearFunction[] lfArray = graph.convertToLinearFunction(new int[]{10});
		assertEquals(linearFunction,lfArray[0]);
		assertEquals(1, lfArray.length);
	}

}