package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public abstract class AbstractShortestPathLinearFunctionTest {
	
	private static Logger logger = LoggerFactory.getLogger(DijkstraLinearFunctionTest.class);
	protected static AbstractShortestPathService serviceExample4;
	protected static Graph graphExample4;

	@BeforeClass
	public static void setup2() {
		graphExample4 = new GraphGenerator().generateExample4();
	}
	
	@Test
	public void shortestPathGraphExample4Day() throws ParseException {
		Long source = 0L; 
		Long target = 6L; 
		Date time = DateUtils.parseDate(6, 0, 0);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceExample4.shortestPath(source, target, time);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(14, shortestPath.getPathCost(), 0);

	}
	
	@Test
	public void shortestPathGraphExample4Night() throws ParseException {
		Long source = 0L; 
		Long target = 6L; 
		Date time = DateUtils.parseDate(18, 0, 0);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceExample4.shortestPath(source, target, time);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(12, shortestPath.getPathCost(), 0);

	}
	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}
}
