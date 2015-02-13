package org.graphast.query.route.osr;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.junit.BeforeClass;
import org.junit.Test;

public class OSRTest {

	private static GraphBounds graphBoundsPoI;
	
	@BeforeClass
	public static void setup() {
		
		/*
		 * The type of graph the will be used to retrieve costs needed. 
		 * 		0 = Regular Costs;
		 * 		1 = Lower Bound Costs;
		 * 		3 = Upper Bound Costs.
		 */
		short graphType = 1;
		
		graphBoundsPoI = new GraphGenerator().generateExamplePoI();
		
		BoundsRoute bounds = new BoundsRoute(graphBoundsPoI, graphType);
		
		//System.out.println(bounds);
		
		
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

	}

}
