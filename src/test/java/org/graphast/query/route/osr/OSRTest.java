package org.graphast.query.route.osr;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.junit.BeforeClass;
import org.junit.Test;

public class OSRTest {

	private static GraphBounds graphBoundsPoI;
	
	@BeforeClass
	public static void setup() {
		
		graphBoundsPoI = new GraphGenerator().generateExamplePoI();
		BoundsRoute bounds = new BoundsRoute(graphBoundsPoI);
		
		System.out.println(bounds);
		
		
//		graphAdapter.load(getResourcePath("/tdg.json"), FileType.GRAPHSON);
//		graphAdapter.setInfo();
//		lbgAdapter.load(getResourcePath("/lbg.json"), FileType.GRAPHSON);
//		lbgRev = (RoadGraphAdapter) graphAdapter.reverseGraph(lbgRev);
//	
//		//calculate or load bounds
//		BoundsRoute bounds = new BoundsRoute(lbgAdapter, "localhost", 5);
//		
//		osr = new OSRSearch(graphAdapter, bounds, lbgRev);
	}
	
	@Test
	public void test() {
		//fail("Not yet implemented");
	}

}
