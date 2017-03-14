package org.graphast.graphgenerator;

import java.io.IOException;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.graphast.query.route.shortestpath.dijkstrach.BidirectionalDijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphGeneratorTest {

	private static GraphBounds graphMonaco;
	// private static GraphBounds graphWashington;
	private static Graph graphExample;
	private static GraphBounds graphExample2;
	private static Graph graphExample3;
	private static Graph graphExamplePoI;
	private static Graph graphAndorra;
	private static CHGraph graphExampleCH;
	private static CHGraph graphExampleCHWithPoIs;

	private static CHGraph graphhopperExample1;
	private static CHGraph contractedGraphhopperExample1;

	@BeforeClass
	public static void setup() throws NumberFormatException, IOException {
		// graphMonaco = new GraphGenerator().generateMonaco();
		//// graphWashington = new GraphGenerator().generateWashington();
		// graphExample = new GraphGenerator().generateExample();
		// graphExample2 = new GraphGenerator().generateExample2();
		// graphExample3 = new GraphGenerator().generateExample3();
		// graphExamplePoI = new GraphGenerator().generateExamplePoI();
		// graphAndorra = new GraphGenerator().generateAndorra();
		// graphExampleCH = new GraphGenerator().generateGraphHopperExample();
		// graphExampleCHWithPoIs = new
		// GraphGenerator().generateGraphHopperExampleWithPoIs();
		// graphNativeGraphhopper = new
		// GraphGenerator().generateGraphhopperNativeExample();
		graphhopperExample1 = new GraphGenerator().generateGraphhopperExample1();
		contractedGraphhopperExample1 = new GraphGenerator().generateContractedGraphhopperExample1();
	}

	@Test
	public void compareGraphhopperExample1() {

		graphhopperExample1.prepareNodes();
		graphhopperExample1.contractNodes();

		System.out.println("Print the graph contracted by GRAPHAST!");
		for (int i = 0; i < graphhopperExample1.getNumberOfNodes(); i++) {
			CHNode testNode = graphhopperExample1.getNode(i);
			System.out.println("nodeID: " + testNode.getExternalId() + ", Latitude: " + testNode.getLatitude()
					+ ", Longitude: " + testNode.getLongitude() + ", Level: " + testNode.getLevel());
		}

		for (int i = 0; i < graphhopperExample1.getNumberOfEdges(); i++) {
			CHEdge testEdge = contractedGraphhopperExample1.getEdge(i);
			System.out.println("edgeID: " + testEdge.getExternalId() + ", From: "
					+ graphhopperExample1.getNode(testEdge.getFromNode()).getExternalId() + ", To: "
					+ graphhopperExample1.getNode(testEdge.getToNode()).getExternalId() + ", Distance: "
					+ testEdge.getDistance() + ", isShortcut: " + testEdge.isShortcut());
		}

		System.out.println("Print the graph contracted by GRAPHHOPPER!");
		for (int i = 0; i < contractedGraphhopperExample1.getNumberOfNodes(); i++) {
			CHNode testNode = contractedGraphhopperExample1.getNode(i);
			System.out.println("nodeID: " + testNode.getExternalId() + ", Latitude: " + testNode.getLatitude()
					+ ", Longitude: " + testNode.getLongitude() + ", isPoI: " + testNode.getCategory());
		}

		for (int i = 0; i < contractedGraphhopperExample1.getNumberOfEdges(); i++) {
			CHEdge testEdge = contractedGraphhopperExample1.getEdge(i);
			System.out.println("edgeID: " + testEdge.getExternalId() + ", From: "
					+ contractedGraphhopperExample1.getNode(testEdge.getFromNode()).getExternalId() + ", To: "
					+ contractedGraphhopperExample1.getNode(testEdge.getToNode()).getExternalId() + ", Distance: "
					+ testEdge.getDistance());
		}
		
		
		BidirectionalDijkstraCH bidirectionalDijkstra = new BidirectionalDijkstraCH(graphhopperExample1);
		
		Path bidirectionalDijkstraFinalPath = bidirectionalDijkstra.execute(graphhopperExample1.getNode(2), graphhopperExample1.getNode(5));
		
		
		System.out.println("Distancia: " + bidirectionalDijkstraFinalPath.getTotalCost());
		

	}

	// @Test
	// public void generateMonacoTest() {
	// assertEquals(751, graphMonaco.getNumberOfNodes());
	// assertEquals(1306, graphMonaco.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateWashingtonTest() {
	// assertEquals(636016, graphWashington.getNumberOfNodes());
	// assertEquals(1548054, graphWashington.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExampleTest() {
	// assertEquals(6, graphExample.getNumberOfNodes());
	// assertEquals(10, graphExample.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExample2Test() {
	// assertEquals(7, graphExample2.getNumberOfNodes());
	// assertEquals(8, graphExample2.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExample3Test() {
	// assertEquals(6, graphExample3.getNumberOfNodes());
	// assertEquals(7, graphExample3.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExamplePoITest() {
	// assertEquals(10, graphExamplePoI.getNumberOfNodes());
	// assertEquals(13, graphExamplePoI.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateAndorraTest() {
	// assertEquals(2621, graphAndorra.getNumberOfNodes());
	// assertEquals(5326, graphAndorra.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExampleCHTest() {
	// assertEquals(10, graphExampleCH.getNumberOfNodes());
	// assertEquals(19, graphExampleCH.getNumberOfEdges());
	// }
	//
	// @Test
	// public void generateExampleCHWithPoIsTest() {
	//
	// for(int i=0; i<graphExampleCHWithPoIs.getNumberOfNodes(); i++) {
	// CHNode testNode = graphExampleCHWithPoIs.getNode(i);
	// System.out.println("nodeID: " + testNode.getId() + ", Latitude: " +
	// testNode.getLatitude() +
	// ", Longitude: " + testNode.getLongitude() + ", isPoI: " +
	// testNode.getCategory());
	// }
	//
	// for(int i=0; i<graphExampleCHWithPoIs.getNumberOfEdges(); i++) {
	// CHEdge testEdge = graphExampleCHWithPoIs.getEdge(i);
	// System.out.println("edgeID: " + testEdge.getId() + ", From: " +
	// testEdge.getFromNode() +
	// ", To: " + testEdge.getToNode() + ", Distance: " +
	// testEdge.getDistance());
	// }
	//
	// assertEquals(13, graphExampleCHWithPoIs.getNumberOfNodes());
	// assertEquals(31, graphExampleCHWithPoIs.getNumberOfEdges());
	// }
	//
	// @Test
	// public void upperAndLowerBoundsTest() {
	//
	// graphExample2.createBounds();
	//
	// Map<Long, Integer> realValueUpper = new SimpleMap<Long, Integer>(2l,2,
	// 6l,11, 3l,11, 1l,10, 5l,13, 4l,10, 0l,4, 7l,15);
	// Map<Long, Integer> realValueLower = new SimpleMap<Long, Integer>(2l,1,
	// 6l,3, 3l,4, 1l,2, 5l,2, 4l,1, 0l,2, 7l,2);
	//
	// assertEquals(true, MapUtils.equalMaps(realValueUpper,
	// graphExample2.getEdgesUpperBound()));
	// assertEquals(true, MapUtils.equalMaps(realValueLower,
	// graphExample2.getEdgesLowerBound()));
	//
	// // Upper cost test
	// assertEquals(10, graphExample2.getEdgeUpperCost(1l));
	// assertEquals(2, graphExample2.getEdgeUpperCost(2l));
	// assertEquals(15, graphExample2.getEdgeUpperCost(7l));
	//
	// // Lower cost test
	// assertEquals(2, graphExample2.getEdgeLowerCost(1l));
	// assertEquals(1, graphExample2.getEdgeLowerCost(2l));
	// assertEquals(2, graphExample2.getEdgeLowerCost(7l));
	//
	// }

	@AfterClass
	public static void tearDown() {
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}