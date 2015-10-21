package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
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
	protected static AbstractShortestPathService serviceExample4Bounds;
	protected static Graph graphExample4;
	protected static GraphBounds graphBounds;

	@BeforeClass
	public static void setup2() {
		graphExample4 = new GraphGenerator().generateExample4();
		graphBounds = new GraphGenerator().generateExamplePoI();
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

		assertEquals(14, shortestPath.getTotalCost(), 0);

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

		assertEquals(12, shortestPath.getTotalCost(), 0);

	}
	
	@Test
	public void shortestPathGraphExample4DayWithBounds() throws ParseException {
		Long source = 4L; 
		Long target = 8L; 
		Date time = DateUtils.parseDate(0, 0, 0);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceExample4Bounds.shortestPath(source, target, time);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());

		assertEquals(12, shortestPath.getTotalCost()/1000/60, 0);

	}
	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}
}
