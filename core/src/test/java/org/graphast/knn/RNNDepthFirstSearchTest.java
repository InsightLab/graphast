package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.exception.PathNotFoundException;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.importer.OSMDBImporter;
import org.graphast.model.Edge;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.rnn.RNNBacktrackingSearch;
import org.graphast.query.route.shortestpath.dijkstra.Dijkstra;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraLinearFunction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.graphast.util.NumberUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RNNDepthFirstSearchTest {
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	private Integer idCustomer;
	private Date maxTravelTime;
	private Date hourServiceTime;
	
	@Before
	public void setUp() throws ParseException, IOException {
		
		//Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(5);
				
		//Hora que ele realiza a chamada do serviço - meia-noite e vinte segundo
		hourServiceTime = DateUtils.parseDate(00, 00, 00);
	}
	
	@Test
	public void reverteGraphTest() throws IOException {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTAXI();
		GraphBounds graphBoundsReverse = new GraphGenerator().generateExampleTAXI();
		graphBoundsReverse.reverseGraph();
		
		//checar quantidade de vértices que foram invertidos
		assertEquals("A quantidade de Vértices esperado não corresponde com a quantidade retornada.",
				10, graphBounds.getNumberOfNodes());
		
		//checar quantidade de edge que foram invertidos
		assertEquals("A quantidade de Edges esperados não corresponde com a quantidade retornada.",
				13, graphBounds.getNumberOfEdges());
		
		List<Double> latitudesTo = new ArrayList<Double>();
		List<Double> longitudeTo = new ArrayList<Double>();
		List<Double> idTo = new ArrayList<Double>();
		
		for (int i = 0; i < graphBounds.getNumberOfEdges(); i++) {
			Edge edge = graphBounds.getEdge(i);
			double latitude = graphBounds.getNode(edge.getToNode()).getLatitude();
			double longitude = graphBounds.getNode(edge.getToNode()).getLongitude();
			double id = graphBounds.getNode(edge.getToNode()).getId();
			
			latitudesTo.add(latitude);
			longitudeTo.add(longitude);
			idTo.add(id);
		}
		
		//Reverse Graph
		graphBounds.reverseGraph();
		
		List<Double> latitudesFromReverse = new ArrayList<Double>();
		List<Double> longitudeFromReverse = new ArrayList<Double>();
		List<Double> idFromReverse = new ArrayList<Double>();
		
		for (int i = 0; i < graphBounds.getNumberOfEdges(); i++) {
			Edge edge = graphBounds.getEdge(i);
			double latitude = graphBounds.getNode(edge.getFromNode()).getLatitude();
			double longitude = graphBounds.getNode(edge.getFromNode()).getLongitude();
			double id = graphBounds.getNode(edge.getFromNode()).getId();
			
			latitudesFromReverse.add(latitude);
			longitudeFromReverse.add(longitude);
			idFromReverse.add(id);
		}
		
		//checar quantidade de vértices que foram invertidos
		assertEquals("A quantidade de Vértices esperado não corresponde com a quantidade retornada após o reverso do grafo.",
				10, graphBounds.getNumberOfNodes());
		
		//checar quantidade de edge que foram invertidos
		assertEquals("A quantidade de Edges esperados não corresponde com a quantidade retornada após o reverso do grafo.",
				13, graphBounds.getNumberOfEdges());
		
		assertEquals(latitudesTo, latitudesFromReverse);
		assertEquals(longitudeTo, longitudeFromReverse);
		assertEquals(idTo, idFromReverse);
		
	}
	
	// Tem três taxista na malha, customer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolution() {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTAXI();
		
		//Tempo para atendiemento - 39 minutos
		maxTravelTime = DateUtils.parseDate(0, 39, 00);
		
		RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		System.out.println(nearestNeighbor);
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 39, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
		Assert.assertNotNull(nearestNeighbor.getNumberVisitedNodes());
	}
	
	// Tem três taxista na malha, custumer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	public void returnNullSolution() throws PathNotFoundException {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTAXI();
		
		//Tempo para atendiemento - 38 minutos
		maxTravelTime = DateUtils.parseDate(0, 38, 00);
		
		RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		Assert.assertNull(nearestNeighbor);
	}
	
	// Tem três taxista na malha, customer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolutionTimeForTheCallMidnight() throws PathNotFoundException {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTaxi15to15minutes();
		
		//Tempo para atendiemento - 40 minutos
		maxTravelTime = DateUtils.parseDate(0, 40, 00);
				
		//Hora que ele realiza a chamada do serviço - meia-noite
		hourServiceTime = DateUtils.parseDate(00, 00, 00);
		
		RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		System.out.println(nearestNeighbor);
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 40, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
		Assert.assertNotNull(nearestNeighbor.getNumberVisitedNodes());
	}
	
	// Tem três taxista na malha, customer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolutionTimeForTheCallHourServiceInit() throws PathNotFoundException {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTaxi15to15minutes();
		
		//Tempo para atendiemento - 39 minutos
		maxTravelTime = DateUtils.parseDate(0, 40, 00);
				
		//Hora que ele realiza a chamada do serviço - meia-noite e vinte segundo
		hourServiceTime = DateUtils.parseDate(00, 15, 00);
		
		RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		System.out.println(nearestNeighbor);
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 29, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
		Assert.assertNotNull(nearestNeighbor.getNumberVisitedNodes());
	}
	
	@Test
	public void baseTest() throws IOException, ParseException {
		
		GraphBounds graphBounds = new GraphGenerator().generateExampleTAXI();
		
		Dijkstra dijkstraShortestPathLinearFunction = new DijkstraLinearFunction(graphBounds);
		Path shortestPath = dijkstraShortestPathLinearFunction.shortestPath(1l, 5l);
		Assert.assertEquals(39, shortestPath.getTotalCost()/1000/60, 0);
		
	}
	
	@Ignore
	@Test
	public void dbTest() {
		
		OSMDBImporter importer = new OSMDBImporter("view_exp_1k", PATH_GRAPH);
		GraphBounds graphBounds = importer.execute();
		
		RNNBacktrackingSearch taxiSearch = new RNNBacktrackingSearch(graphBounds);
//		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		//Tempo para atendiemento - 39 minutos
		maxTravelTime = DateUtils.parseDate(00, 59, 00);
		//Hora que ele realiza a chamada do serviço - meia-noite e vinte segundo
		hourServiceTime = DateUtils.parseDate(00, 00, 00);
		
		for (int i = 0; i < 100; i++) {
			
			long numberOfNodes = graphBounds.getNumberOfNodes();
			long customer = Double.valueOf(NumberUtils.generatePdseurandom(0, Long.valueOf(numberOfNodes).intValue())).longValue();
			NearestNeighbor solution2 = taxiSearch.search(graphBounds.getNode(customer), maxTravelTime, hourServiceTime);
			
			Long idSolution2 = null;
			Integer distance2 = null;
			Integer size2 = null;
			ArrayList<Long> path2 = null;
			Long externalId2 = null;
			if(solution2 != null) {
				idSolution2 = solution2.getId();
				distance2 = solution2.getDistance();
				size2 = solution2.getPath().size();
				path2 = solution2.getPath();
				externalId2 = graphBounds.getNode(solution2.getId()).getExternalId();
				
			}
			
			String.format("%s;%s;%s;%s;%s", idSolution2, externalId2, distance2, size2, path2);
		}
	}
	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}
