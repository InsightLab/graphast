package org.graphast.query.route.shortestpath;

import static org.graphast.util.DistanceUtils.distanceLatLong;
import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graphast;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.graphhopper.util.StopWatch;

public class AStarShortestPathConstantWeightTest {

		private static Graphast graphMonaco;
		
		@BeforeClass
		public static void setup() {
			graphMonaco = new GraphGenerator().generateMonaco();
		}
		
		@Test
		public void shortestPathMonacoTest() {
			
			// To see the error, try this coordinates
			//		from: 43.729825,7.414146
			//		to: 43.730577,7.415487
			Long source = graphMonaco.getNode(43.7294668047756,7.413772473047058);
			Long target = graphMonaco.getNode(43.73079058671274,7.415815422292399);
			
			AbstractShortestPathService aStar = new AStarShortestPathConstantWeight(graphMonaco);
			
			//TODO Improve this distance function
			double diffFrom = distanceLatLong(43.7294668047756,7.413772473047058, 43.7294668047756,7.413772473047058);
			double diffTo = distanceLatLong(43.73079058671274,7.415815422292399, 43.73079058671274,7.415815422292399);
			StopWatch sw = new StopWatch();
	        sw.start();
			double shortestPath = aStar.shortestPath(source, target);
			sw.stop();
			
			System.out.println("execution time:" + sw.getTime());
			int realDistance = (int)(((shortestPath/1000.0) - diffFrom - diffTo)*1000);

			assertEquals(228910, realDistance);

		}
		
		@Test
		public void shortestPathMonacoTest2() {
			Long source = graphMonaco.getNode(43.72842465479131, 7.414896579419745);
			Long target = graphMonaco.getNode(43.7354373276704, 7.4212202598427295);
			
			AbstractShortestPathService aStar = new AStarShortestPathConstantWeight(graphMonaco);
		
			// 1117.9563590469443m = 1117956mm (GraphHooper result)
	        assertEquals(1136643, aStar.shortestPath(source, target));

		}
		
		@Test
		public void ShortestPathMonacoTest3() {
			Long source = graphMonaco.getNode(43.72636792197156, 7.417292499928754);
			Long target = graphMonaco.getNode(43.74766484829034,7.430716770083832);
			
			AbstractShortestPathService aStar = new AStarShortestPathConstantWeight(graphMonaco);
	        assertEquals(3610710, aStar.shortestPath(source, target));

		}
		

		
		@AfterClass
		public static void tearDown() {
			FileUtils.deleteDir("/tmp/graphhopper/test");
			FileUtils.deleteDir("/tmp/graphast/test");
		}
	}
