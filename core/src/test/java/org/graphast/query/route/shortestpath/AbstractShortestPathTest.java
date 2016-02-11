package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public abstract class AbstractShortestPathTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static Graph graphMonaco;
//	protected static Graph graphSeattle;
	protected static Graph graphExample;
	protected static Graph graphExample2;
	
	
	protected static AbstractShortestPathService serviceMonaco;
//	protected static AbstractShortestPathService serviceSeattle;
	protected static AbstractShortestPathService serviceExample;
	protected static AbstractShortestPathService serviceExample2;
	
	
	
	@BeforeClass
	public static void setup() throws NumberFormatException, IOException {
		graphMonaco = new GraphGenerator().generateMonaco();
//		graphSeattle = new GraphGenerator().generateSeattle();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
	}

	@Test
	public void shortestPathMonacoTest() {
		
		Long source = graphMonaco.getNodeId(43.740174,7.424376);
		Long target = graphMonaco.getNodeId(43.735554,7.416147);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());
		
//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println("(" + point.getLatitude() + "," + point.getLongitude()+")");
//		}
		
//		assertEquals(228910, shortestPath.getTotalCost(), 0);
		// TODO fix this assertion
		// assertEquals(76, shortestPath.getGeometry().size()); // Works with A*, but not with Dijkstra
		//assertEquals(78, shortestPath.getGeometry().size()); // Works with Dijsktra, but not with A*
		
		assertEquals(63155, shortestPath.getTotalCost(), 0);
	}

	@Test
	public void shortestPathMonacoTest2() {
		
		Long source = graphMonaco.getNodeId(43.72842465479131, 7.414896579419745);
		Long target = graphMonaco.getNodeId(43.7354373276704, 7.4212202598427295);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest2(): {}ms", sw.getTime());
		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());

//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println("(" + point.getLatitude() + "," + point.getLongitude()+")");
//		}
		
//		assertEquals(75, shortestPath.getGeometry().size());

		assertEquals(66851.0, shortestPath.getTotalCost(), 0);

	}
	
//	@Test
//	public void shortestPathSeattleTest() {
//		
//		Long source = graphSeattle.getNodeId(47.650698,-122.393716);
//		Long target = graphSeattle.getNodeId(47.555501,-122.283506);
//
//		StopWatch sw = new StopWatch();
//
//		sw.start();
//		Path shortestPath = serviceSeattle.shortestPath(source, target);
//		sw.stop();
//
//		logger.debug(shortestPath.toString());
//		logger.debug("Execution Time of shortestPathWashintonTest(): {}ms", sw.getTime());
//		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
//		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());
//
//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println(point.getLatitude() + "," + point.getLongitude());
//		}
//
////		assertEquals(228910, shortestPath.getPathCost(), 0);
//		
//	}

	@Test
	public void shortestPathExampleTest() {

		Long source = 0L; // External ID = 1
		Long target = 5L; // External ID = 4

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceExample.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());

//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println("(" + point.getLatitude() + "," + point.getLongitude()+")");
//		}

//		assertEquals(6, shortestPath.getGeometry().size());
		assertEquals(475, shortestPath.getTotalCost(), 0);

	}
	
//	@Test
//	public void shortestPathExample2Test() {
//
//		Long source = 0L;
//		Long target = 6L;
//
//		AbstractShortestPathService aStar = new AStarConstantWeight(graphExample2);
//
//		StopWatch sw = new StopWatch();
//
//		sw.start();
//		Path shortestPath = aStar.shortestPath(source, target);
//		sw.stop();
//
//		logger.debug(shortestPath.toString());
//		logger.debug("Execution Time of shortestPathExample2Test(): {}ms", sw.getTime());
//		logger.debug("Path Cost: {}", shortestPath.getPathCost());
//
//		assertEquals(12, shortestPath.getPathCost(), 0);
//
//	}
	
	@Test
	public void shortestPathMonacoTest3() {

		Long source = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long target = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());

//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println("(" + point.getLatitude() + "," + point.getLongitude()+")");
//		}

		// TODO fix this assertion
		// assertEquals(239, shortestPath.getGeometry().size()); // Works with A*, but not with Dijkstra
		//assertEquals(246, shortestPath.getGeometry().size());
		assertEquals(212364.0, shortestPath.getTotalCost(), 0);

	}
	
	@Test
	public void shortestPathGraphExampleReverseTest2() {

		Long target = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long source = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		graphMonaco.reverseGraph();

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Total Distance: {}", shortestPath.getTotalDistance());
		logger.debug("Path Total Cost: {}", shortestPath.getTotalCost());

//		for(Point point : shortestPath.getGeometry()) {
//			System.out.println("(" + point.getLatitude() + "," + point.getLongitude()+")");
//		}
		// TODO fix this assertion
		//assertEquals(236, shortestPath.getGeometry().size()); // Works with A*, but not with Dijkstra
		// assertEquals(240, shortestPath.getGeometry().size());

		assertEquals(212364.0, shortestPath.getTotalCost(), 0);
	}
	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}

}