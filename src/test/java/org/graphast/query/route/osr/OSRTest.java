package org.graphast.query.route.osr;

import static org.junit.Assert.assertEquals;
import static org.graphast.util.NumberUtils.convertToInt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraVariableWeight;
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
		
		//Loads into graphBoundsPoI the graph related to the generateExamplePoI method (including upperBounds and 
		//lowerBounds for both edges and PoI's
		graphBoundsPoI = new GraphGenerator().generateExamplePoI();
		
		graphBoundsPoIReverse = new GraphGenerator().generateExamplePoI();
		
		graphBoundsPoIReverse.reverseGraph();
		
		//The variable 'bounds' represents a lowerBound shortestPath of starting in each vertex to all PoI.
		//For a better understanding, try to print the variable 'bounds'
		BoundsRoute bounds = new BoundsRoute(graphBoundsPoI, graphType);
		
		osr = new OSRSearch(graphBoundsPoI, bounds, graphBoundsPoIReverse);
		
//		for(int i=0; i<graphBoundsPoI.getNumberOfNodes();i++) {
//			System.out.println(graphBoundsPoI.getNode(i));
//		}
//		
//		for(int i=0; i<graphBoundsPoI.getNumberOfEdges();i++) {
//			System.out.println(graphBoundsPoI.getEdge(i));
//		}
		
	}
	
//	@Test
//	public void dijkstra() {
//		DijkstraVariableWeight dijkstra = new DijkstraVariableWeight(graphBoundsPoIReverse);
//		dijkstra.shortestPath(7);
//		System.out.println(dijkstra.shortestPath(7));
//	}
	
	
	@Test
	public void search() throws ParseException{
		
		ArrayList<Integer> categories = new ArrayList<Integer>();
		categories.add(2);
		categories.add(1);
		
    	Date date = DateUtils.parseDate(0, 550, 0);
    	
    	Graph graph = osr.getGraphAdapter();

    	
    	
//    	Sequence seq = osr.search(graph.getNode(3), graph.getNode(5), date, categories);

    	Sequence seq = osr.search(graph.getNode(1), graph.getNode(7), date, categories);
    	
    	System.out.println(seq);
//		assertEquals(convertToInt(graph.getNode(5).getId()), (long) seq.getPois().get(0).getId());
//		assertEquals(3120000, (int) seq.getPois().get(0).getDistance());
//		assertEquals(convertToInt(graph.getNode(2).getId()), (long) seq.getPois().get(1).getId());
//		assertEquals(7560000, (int) seq.getPois().get(1).getDistance());
//		
	}

}