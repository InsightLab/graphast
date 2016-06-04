package org.graphast.knn;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGeneratorGridTester;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.rnn.RNNBacktrackingSearch;
import org.graphast.query.rnn.RNNBreadthFirstSearch;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class RNNComparatorTest {
	
	private Integer idCustomer = null;
	private Date endServiceTime = null;
	private Date startServiceTime = null;
	private Double percentagemPoi = null;
	private String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	@Before
	public void setUp() throws ParseException, IOException {
		
		//Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(0);
		
		//Tempo para atendiemento - 23h 59m 59s
		endServiceTime = DateUtils.parseDate(0, 20, 00);
		
		//Hora que ele realiza a chamada do serviço
		startServiceTime = DateUtils.parseDate(00, 00, 00);
		
		percentagemPoi = Double.valueOf(1);
	}

	// 1k (1024 pontos)
	@Test
	public void taxiSearchSytenticGraph1k() throws IOException, ParseException {
		
		for(int i=0; i<100; i++) {
			
			int comprimento = 32;
			int altura = 32;
			
			GraphGeneratorGridTester graphSynthetic = new GraphGeneratorGridTester(PATH_GRAPH, comprimento,altura, percentagemPoi);
			graphSynthetic.generateGraph();
			GraphBounds graph = graphSynthetic.getGraph();
			
			//==== SOLUÇÃO I ====
			long startSolution1 = System.currentTimeMillis();   
			RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graph);
			NearestNeighbor solution1 = taxiSearch.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution1 = System.currentTimeMillis();
			long timeSolution1 = endSolution1-startSolution1;
			
			graph.reverseGraph();
			
			//==== SOLUÇÃO III ====
			long startSolution3 = System.currentTimeMillis();   
			RNNBreadthFirstSearch taxiSearchRestritionsOSR = new RNNBreadthFirstSearch(graph);
			NearestNeighbor solution3 = taxiSearchRestritionsOSR.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution3 = System.currentTimeMillis();
			long timeSolution3 = endSolution3-startSolution3;
			
			System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", timeSolution1, timeSolution3, solution1.getId(), solution3.getId(), solution1.getDistance(), 
					solution3.getDistance(), solution1.getPath().size(), solution3.getPath().size(), solution1.getPath(), solution3.getPath()));
		}
	}
	
	// 10k (10000 pontos)
	@Test
	public void taxiSearchSytenticGraph10k() throws IOException, ParseException {
		
		for(int i=0; i<100; i++) {
			
			int comprimento = 100;
			int altura = 100;
			
			GraphGeneratorGridTester graphSynthetic = new GraphGeneratorGridTester(PATH_GRAPH, comprimento,altura, percentagemPoi);
			graphSynthetic.generateGraph();
			GraphBounds graph = graphSynthetic.getGraph();
			
			//==== SOLUÇÃO I ====
			long startSolution1 = System.currentTimeMillis();   
			RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graph);
			NearestNeighbor solution1 = taxiSearch.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution1 = System.currentTimeMillis();
			long timeSolution1 = endSolution1-startSolution1;
			
			graph.reverseGraph();
			
			//==== SOLUÇÃO III ====
			long startSolution3 = System.currentTimeMillis();   
			RNNBreadthFirstSearch taxiSearchRestritionsOSR = new RNNBreadthFirstSearch(graph);
			NearestNeighbor solution3 = taxiSearchRestritionsOSR.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution3 = System.currentTimeMillis();
			long timeSolution3 = endSolution3-startSolution3;
			
			System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", timeSolution1, timeSolution3, solution1.getId(), solution3.getId(), solution1.getDistance(), 
					solution3.getDistance(), solution1.getPath().size(), solution3.getPath().size(), solution1.getPath(), solution3.getPath()));
		}
	}
	
	// 100k (99856 pontos)
	@Test
	public void taxiSearchSytenticGraph100k() throws IOException, ParseException {
		
		for(int i=0; i<100; i++) {
			
			int comprimento = 316;
			int altura = 316;
			
			GraphGeneratorGridTester graphSynthetic = new GraphGeneratorGridTester(PATH_GRAPH, comprimento,altura, percentagemPoi);
			graphSynthetic.generateGraph();
			GraphBounds graph = graphSynthetic.getGraph();
			
			GraphBounds reverseGraph = graphSynthetic.getGraph();
			graph = reverseGraph;
			reverseGraph.reverseGraph();
			
			//==== SOLUÇÃO I ====
			long startSolution1 = System.currentTimeMillis();   
			RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graph);
			NearestNeighbor solution1 = taxiSearch.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution1 = System.currentTimeMillis();
			long timeSolution1 = endSolution1-startSolution1;
			
			
			//==== SOLUÇÃO III ====
			long startSolution3 = System.currentTimeMillis();   
			RNNBreadthFirstSearch taxiSearchRestritionsOSR = new RNNBreadthFirstSearch(reverseGraph);
			NearestNeighbor solution3 = taxiSearchRestritionsOSR.search(reverseGraph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution3 = System.currentTimeMillis();
			long timeSolution3 = endSolution3-startSolution3;
			
			System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", timeSolution1, timeSolution3, solution1.getId(), solution3.getId(), solution1.getDistance(), 
					solution3.getDistance(), solution1.getPath().size(), solution3.getPath().size(), solution1.getPath(), solution3.getPath()));
		}
	}
	
	// 1G (1000000 pontos)
	@Test
	public void taxiSearchSytenticGraph1000k() throws IOException, ParseException {
		
		for(int i=0; i<10; i++) {
			
			int comprimento = 1000;
			int altura = 1000;
			
			GraphGeneratorGridTester graphSynthetic = new GraphGeneratorGridTester(PATH_GRAPH, comprimento,altura, percentagemPoi);
			graphSynthetic.generateGraph();
			GraphBounds graph = graphSynthetic.getGraph();
			
			GraphBounds reverseGraph = graphSynthetic.getGraph();
			graph = reverseGraph;
			reverseGraph.reverseGraph();
			
			//==== SOLUÇÃO I ====
			long startSolution1 = System.currentTimeMillis();   
			RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graph);
			NearestNeighbor solution1 = taxiSearch.search(graph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution1 = System.currentTimeMillis();
			long timeSolution1 = endSolution1-startSolution1;
			
			
			//==== SOLUÇÃO III ====
			long startSolution3 = System.currentTimeMillis();   
			RNNBreadthFirstSearch taxiSearchRestritionsOSR = new RNNBreadthFirstSearch(reverseGraph);
			NearestNeighbor solution3 = taxiSearchRestritionsOSR.search(reverseGraph.getNode(idCustomer), endServiceTime, startServiceTime);
			long endSolution3 = System.currentTimeMillis();
			long timeSolution3 = endSolution3-startSolution3;
			
			System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", timeSolution1, timeSolution3, solution1.getId(), solution3.getId(), solution1.getDistance(), 
					solution3.getDistance(), solution1.getPath().size(), solution3.getPath().size(), solution1.getPath(), solution3.getPath()));
		}
	}
		

	@AfterClass
	public static void tearDown() {

		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}
