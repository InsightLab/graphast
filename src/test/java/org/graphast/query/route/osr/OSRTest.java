package org.graphast.query.route.osr;

import static org.junit.Assert.assertEquals;
import static org.graphast.util.NumberUtils.convertToInt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.util.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;


public class OSRTest {

	private static OSRSearch osr;
	private static GraphBounds graphBoundsPoI, graphBoundsPoIReverse;
	
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
		
		graphBoundsPoIReverse = new GraphGenerator().generateExamplePoI();
		
		graphBoundsPoIReverse.reverseGraph();
		
		BoundsRoute bounds = new BoundsRoute(graphBoundsPoI, graphType);
		
		System.out.println(bounds);
		
		
//		graphAdapter.load(getResourcePath("/tdg.json"), FileType.GRAPHSON);
//		graphAdapter.setInfo();
//		lbgAdapter.load(getResourcePath("/lbg.json"), FileType.GRAPHSON);
//		lbgRev = (RoadGraphAdapter) graphAdapter.reverseGraph(lbgRev);
//	
//		//calculate or load bounds
//		BoundsRoute bounds = new BoundsRoute(lbgAdapter, "localhost", 5);

		osr = new OSRSearch(graphBoundsPoI, bounds, graphBoundsPoIReverse);
		
	}
	
	@Test
	public void search() throws ParseException{
		ArrayList<Integer> categories = new ArrayList<Integer>();
		categories.add(2);
		categories.add(1);
		
    	Date date = DateUtils.parseDate(0, 550, 0);
    	
    	Graph graph = osr.getGraphAdapter();

//    	Sequence seq = osr.search(graph.getNode(3), graph.getNode(5), date, categories);

    	Sequence seq = osr.search(graph.getNode(1), graph.getNode(7), date, categories);
//		assertEquals(convertToInt(graph.getNode(5).getId()), (long) seq.getPois().get(0).getId());
//		assertEquals(3120000, (int) seq.getPois().get(0).getDistance());
//		assertEquals(convertToInt(graph.getNode(2).getId()), (long) seq.getPois().get(1).getId());
//		assertEquals(7560000, (int) seq.getPois().get(1).getDistance());
//		
	}

}
