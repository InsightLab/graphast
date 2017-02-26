package org.graphast.knn;

import java.util.HashMap;
import java.util.Map;

import org.graphast.geometry.PoI;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHGraph;
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

		// Calculate the distance from all PoIs to the source node.
		Long source = graphMonacoWithPoI.getNodeId(43.72842465479131, 7.414896579419745);

		// for (int i = 0; i < graphHopperExample4WithPoIs.getNumberOfNodes();
		// i++) {
		// System.out.println("Nó: " + i + ", Level: " +
		// graphHopperExample4WithPoIs.getNode(i).getLevel());
		// }

		KNNCHSearch knn = new KNNCHSearch(graphMonacoWithPoI);

		knn.search(graphMonacoWithPoI.getNode(source), 4);

	}

	@Test
	public void graphHopperExample4WithPoIsTest() {

		Long source = 1l;

		KNNCHSearch knn = new KNNCHSearch(graphHopperExample4WithPoIs);

		knn.search(graphHopperExample4WithPoIs.getNode(source), 4);

	}
//
//	@Test
//	public void graphHopperExample3WithPoIsTest() {
//
//		Long source = 2l;
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample3WithPoIs);
//
//		knn.search(graphHopperExample3WithPoIs.getNode(source), 3);
//
//	}
//
//	//
//	@Test
//	public void graphHopperExample2WithPoIsTest() {
//
//		Long source = 3l;
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExample2WithPoIs);
//
//		knn.search(graphHopperExample2WithPoIs.getNode(source), 2);
//
//	}
//
//	//
//	@Test
//	public void graphHopperExampleWithPoIsTest() {
//
//		Long source = 3l;
//
//		KNNCHSearch knn = new KNNCHSearch(graphHopperExampleWithPoIs);
//
//		knn.search(graphHopperExampleWithPoIs.getNode(source), 2);
//
//	}

}
