package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.astar.AStarConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class AStarConstantWeightTest {
	
	private static Graph graphMonaco;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
	}

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

		assertEquals(1136643, shortestPath.getPathCost(), 0);

	}

	@Test
	public void ShortestPathMonacoTest3() {
		
		Long source = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long target = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		AbstractShortestPathService aStar = new AStarConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = aStar.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());
		
		assertEquals(3610712, shortestPath.getPathCost(), 0);

	}

	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}

}
