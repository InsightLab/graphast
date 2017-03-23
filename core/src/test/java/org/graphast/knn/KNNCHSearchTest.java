package org.graphast.knn;

import java.util.HashMap;
import java.util.Map;

import org.graphast.geometry.PoI;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.knnch.baseline.KNNCHSearchBaseline;
import org.graphast.query.knnch.lowerbounds.KNNCHSearch;
import org.graphast.util.DistanceUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KNNCHSearchTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static CHGraph graphMonacoWithPoI;
	private static CHGraph graphHopperExampleWithPoIs;
	private static CHGraph graphHopperExample2WithPoIs;
	private static CHGraph graphHopperExample3WithPoIs;
	private static CHGraph graphHopperExample4WithPoIs;

	@BeforeClass
	public static void setup() {

		graphMonacoWithPoI = new GraphGenerator().generateMonacoCHWithPoI();
		graphMonacoWithPoI.prepareNodes();
		graphMonacoWithPoI.contractNodes();

		graphHopperExampleWithPoIs = new GraphGenerator().generateGraphHopperExampleWithPoIs();
		graphHopperExampleWithPoIs.prepareNodes();
		graphHopperExampleWithPoIs.contractNodes();

		graphHopperExample2WithPoIs = new GraphGenerator().generateGraphHopperExample2WithPoIs();
		graphHopperExample2WithPoIs.prepareNodes();
		graphHopperExample2WithPoIs.contractNodes();

		graphHopperExample3WithPoIs = new GraphGenerator().generateGraphHopperExample3WithPoIs();
		graphHopperExample3WithPoIs.prepareNodes();
		graphHopperExample3WithPoIs.contractNodes();

		graphHopperExample4WithPoIs = new GraphGenerator().generateGraphHopperExample4WithPoIs();
		graphHopperExample4WithPoIs.prepareNodes();
		graphHopperExample4WithPoIs.contractNodes();

	}

	@Test
	public void graphMonacoWithPoITest() {

		CHGraph testGraph = graphMonacoWithPoI;
		Long source = graphMonacoWithPoI.getNodeId(43.72842465479131, 7.414896579419745);
//		Long source = 0l;
		int k = 128;
		
		// Calculate the distance from all PoIs to the source node.
		logger.info("Starting to run the Naive kNN Baseline.");
		KNNCHSearchBaseline knnBaseline = new KNNCHSearchBaseline(testGraph);
		knnBaseline.search(testGraph.getNode(source), k);

//		logger.info("Starting to run the first prunning method for kNN with CH.");
//		KNNCHSearch knn = new KNNCHSearch(testGraph);
//		knn.search(testGraph.getNode(source), k);

	}

//	@Test
//	public void graphHopperExample4WithPoIsTest() {
//
//		Long source = 1l;
//		
//		KNNCHSearchBaseline knnBaseline = new KNNCHSearchBaseline(graphHopperExample4WithPoIs);
//		knnBaseline.search(graphHopperExample4WithPoIs.getNode(source), 2);
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample4WithPoIs);
//		knn.search(graphHopperExample4WithPoIs.getNode(source), 2);
//
//	}

//	@Test
//	public void graphHopperExample3WithPoIsTest() {
//
//		Long source = 2l;
//		
//		KNNCHSearchBaseline knnBaseline = new KNNCHSearchBaseline(graphHopperExample3WithPoIs);
//		knnBaseline.search(graphHopperExample3WithPoIs.getNode(source), 3);
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample3WithPoIs);
//		knn.search(graphHopperExample3WithPoIs.getNode(source), 3);
//
//	}
	
//	@Test
//	public void graphHopperExample2WithPoIsTest() {
//
//		Long source = 3l;
//
//		KNNCHSearchBaseline knnBaseline = new KNNCHSearchBaseline(graphHopperExample2WithPoIs);
//		knnBaseline.search(graphHopperExample2WithPoIs.getNode(source), 2);
//		
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample2WithPoIs);
//		knn.search(graphHopperExample2WithPoIs.getNode(source), 2);
//
//	}

//	@Test
//	public void graphHopperExampleWithPoIsTest() {
//
//		Long source = 3l;
//		
//		KNNCHSearchBaseline knnBaseline = new KNNCHSearchBaseline(graphHopperExampleWithPoIs);
//		knnBaseline.search(graphHopperExampleWithPoIs.getNode(source), 2);
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExampleWithPoIs);
//		knn.search(graphHopperExampleWithPoIs.getNode(source), 2);
//
//	}

}
