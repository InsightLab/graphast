package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.astar.AStarConstantWeight;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Instruction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class AStarConstantWeightTest {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Graph graphMonaco;
	private static Graph graphExample;
	private static Graph graphExample2;
	private static Graph graphExample4;
	
	// TODO use the same tests made to dijkstra. they are missing now.
	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
		graphExample4 = new GraphGenerator().generateExample4();
	}

	//This test is just like its equivalent on the DijkstraConstantWeightTest
	@Test
	public void shortestPathMonacoTest() {
		
		Long source = graphMonaco.getNodeId(43.7294668047756,7.413772473047058);
		Long target = graphMonaco.getNodeId(43.73079058671274,7.415815422292399);

		AbstractShortestPathService aStar = new AStarConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = aStar.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(228910, shortestPath.getPathCost(), 0);
		
	}

	//This test is just like its equivalent on the DijkstraConstantWeightTest
	@Test
	public void shortestPathMonacoTest2() {
		
		Long source = graphMonaco.getNodeId(43.72842465479131, 7.414896579419745);
		Long target = graphMonaco.getNodeId(43.7354373276704, 7.4212202598427295);

		AbstractShortestPathService aStar = new AStarConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = aStar.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(1136643.0, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathExampleTest() {

		Long source = 0L; // External ID = 1
		Long target = 5L; // External ID = 4

		AbstractShortestPathService aStar = new AStarConstantWeight(graphExample);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = aStar.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(8100, shortestPath.getPathCost(), 0);

	}
	
	@Test
	public void shortestPathExample2Test() {

		Long source = 0L;
		Long target = 6L;

		AbstractShortestPathService aStar = new AStarConstantWeight(graphExample2);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = aStar.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExample2Test(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(12, shortestPath.getPathCost(), 0);

	}

	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}

}
