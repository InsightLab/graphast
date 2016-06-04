package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.exception.PathNotFoundException;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.graphgenerator.GraphGeneratorGridTester;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.rnn.RNNBreadthFirstSearch;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class RNNBreadthFirstSearchTest {

	private static GraphBounds graphBounds;
	private static GraphBounds graphBoundsReverse;

	private Integer idCustomer;
	private Date maxTravelTime;
	private Date hourServiceTime;
	private String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	public void setUpNoRandomGraph() throws ParseException, IOException {

		// Tempo para atendiemento
		maxTravelTime = DateUtils.parseDate(23, 59, 59);

		// Hora que ele realiza a chamada do serviço meia-noite e vinte minutos
		hourServiceTime = DateUtils.parseDate(00, 00, 00);

		graphBounds = new GraphGenerator().generateExampleTAXI();
//		graphBoundsReverse = new GraphGenerator().generateExampleTAXI();
//		graphBoundsReverse.reverseGraph();
	}
	
	public void setUpRandomGraph(int qtdX, int qtdY, int percentPois) throws ParseException {
		
		// Tempo para atendiemento
		maxTravelTime = DateUtils.parseDate(23, 59, 59);

		// Hora que ele realiza a chamada do serviço meia-noite e vinte minutos
		hourServiceTime = DateUtils.parseDate(00, 00, 00);

		GraphGeneratorGridTester graphSynthetic = new GraphGeneratorGridTester(PATH_GRAPH, qtdX, qtdY, percentPois);
		graphSynthetic.generateGraph();
		
		graphBoundsReverse = graphSynthetic.getGraph();
		graphBounds = graphBoundsReverse;
		graphBoundsReverse.reverseGraph();
	}
	
	// TESTE 1: (EXCEPTION) Verificamos se o cliente que chama o táxi está no mesmo ponto 
	// de um determinado taxita.
	@Test
	public void taxiSearchExpected_I() throws ParseException, IOException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(4);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		Assert.assertNotNull(nearestNeighbor);
		assertEquals("Deve retornar o vid esperado.", 4, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 0, nearestNeighbor.getDistance()/1000/60);
		
		ArrayList<Long> path = new ArrayList<>();
		path.add(4l);
		assertEquals("Deve retornar o caminho esperado.", path.get(0), nearestNeighbor.getPath().get(0));
	}
	
	// TESTE 1.1: (EXCEPTION) Nenhum taxista é encontrado na malha, para a quantidade de tempo
	// superior necessario para atendimento (11 minutos e 59 segundos)
	@Test(expected = PathNotFoundException.class)  
	public void taxiSearchExpected_II() throws ParseException, IOException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(8);
		
		// Tempo para atendiemento
		maxTravelTime = DateUtils.parseDate(0, 11, 59);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBounds);
		taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
	}
	
	// TESTE 2: O cliente está em um vértice vizinho ao taxista. CLIENTE -> TAXISTA (NÃO REVERSO)
	@Test
	public void taxiSearchNeighbor() throws IOException, ParseException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(7);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBounds);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(4l);
		path_result.add(3l);
		path_result.add(2l);
		path_result.add(7l);
		
		assertEquals("Deve retornar o vid esperado.", 4l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 37, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	// TESTE 2.1: O cliente está em um vértice vizinho ao taxista. CLIENTE -> TAXISTA (REVERSO)
	@Test
	public void taxiSearchNeighborReverso() throws IOException, ParseException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(7);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBounds.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 12, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	// Tem três taxista na malha, custumer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolution() throws IOException, ParseException {
		
		//Cliente que realiza a chamada do serviço
		Integer idCustomer = Integer.valueOf(5);
				
		//Tempo para atendiemento - 39 minutos
		Date serviceTime = DateUtils.parseDate(0, 39, 0);

		//Hora que ele realiza a chamada do serviço - meia-noite e vinte segundos
		Date hourServiceTime = DateUtils.parseDate(00, 00, 00);
		
		graphBounds = new GraphGenerator().generateExampleTAXI();
		graphBoundsReverse = new GraphGenerator().generateExampleTAXI();
		graphBoundsReverse.reverseGraph();
		
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), serviceTime, hourServiceTime);
		
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 39, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	
	// TESTE 3: O cliente está em um vértice vizinho a dois taxistas. Mas com 
	// pesos das aresta para chegar ao cliente diferente. Cliente:8; Taxista 1: 4, 12 minutos; Taxista 1: 9, 15 minutos.
	@Test
	public void taxiSearchNeighborWithDifferentWeight() throws IOException, ParseException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(8);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(4l);
		path_result.add(8l);
		
		assertEquals("Deve retornar o vid esperado.", 4l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 12, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	// TESTE 4: O cliente está em um vértice vizinho a dois taxistas. Mas com 
	// pesos das arestas para chegar ao cliente iguais
	@Test
	public void taxiSearchNeighborWithEqualsWeight() throws IOException, ParseException {
		
		setUpNoRandomGraph();
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(8);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(4l);
		path_result.add(8l);
		
		//Retorna o que possui o menor tempo para a travessia, a Queue realiza o compare para a ordenação. 
		assertEquals("Deve retornar o vid esperado.", 4l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 12, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	// Tem três taxista na malha, customer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolutionTimeForTheCallMidnight() throws IOException, ParseException {
		
		GraphBounds graphBoundsReverse = new GraphGenerator().generateExampleTaxi15to15minutes();
		graphBoundsReverse.reverseGraph();
		
		//Tempo para atendiemento - 40 minutos
		maxTravelTime = DateUtils.parseDate(00, 38, 00);
				
		//Hora que ele realiza a chamada do serviço - meia-noite
		hourServiceTime = DateUtils.parseDate(00, 00, 00);
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(5);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		System.out.println(nearestNeighbor);
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 28, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	// Tem três taxista na malha, customer = 5, cab 1 = 1, cab 2 = 4 e cab 3 = 9
	@Test
	public void returnBetterSolutionTimeForTheCallHourServiceInit() throws IOException, ParseException {
		
		GraphBounds graphBoundsReverse = new GraphGenerator().generateExampleTaxi15to15minutes();
		graphBoundsReverse.reverseGraph();
		
		//Tempo para atendiemento - 40 minutos
		maxTravelTime = DateUtils.parseDate(2, 52, 00);
				
		//Hora que ele realiza a chamada do serviço - meia-noite e quinze minutos
		hourServiceTime = DateUtils.parseDate(00, 15, 00);
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(5);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		System.out.println(nearestNeighbor);
		Assert.assertNotNull(nearestNeighbor);
		
		ArrayList<Long> path_result = new ArrayList<>();
		path_result.add(1l);
		path_result.add(7l);
		path_result.add(6l);
		path_result.add(5l);
		
		assertEquals("Deve retornar o vid esperado.", 1l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 52, nearestNeighbor.getDistance()/1000/60);
		assertEquals("Deve retornar o caminho esperado.", path_result , nearestNeighbor.getPath());
	}
	
	@Test  
	public void taxiBetterWaytRandomGraphOneByOne() throws ParseException {

		setUpRandomGraph(1,1,1);
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(0);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		assertEquals("Deve retornar o vid esperado.", 0l, nearestNeighbor.getId());
		assertEquals("Deve retornar o custo esperado.", 0, nearestNeighbor.getDistance()/1000/60);
		
		ArrayList<Long> path = new ArrayList<>();
		path.add(0l);
		assertEquals("Deve retornar o caminho esperado.", path , nearestNeighbor.getPath());
	}
	
	@Test
	public void taxiBetterWaytRandomGraphTwoByTwo() throws ParseException {

		setUpRandomGraph(2, 2, 1);
		
		// Cliente que realiza a chamada do serviço
		idCustomer = Integer.valueOf(0);
		
		RNNBreadthFirstSearch taxiSearch = new RNNBreadthFirstSearch(graphBoundsReverse);
		NearestNeighbor nearestNeighbor = taxiSearch.search(graphBoundsReverse.getNode(idCustomer), maxTravelTime, hourServiceTime);
		
		Assert.assertNotNull(nearestNeighbor);
	}
	
	@AfterClass
	public static void tearDown() {

		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	}
}
