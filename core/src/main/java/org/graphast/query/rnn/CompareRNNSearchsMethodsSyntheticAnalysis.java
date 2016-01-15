package org.graphast.query.rnn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.graphast.config.Configuration;
import org.graphast.exception.PathNotFoundException;
import org.graphast.importer.GraphGeneratorGrid;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DateUtils;
import org.graphast.util.NumberUtils;

public class CompareRNNSearchsMethodsSyntheticAnalysis {
	
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	protected static final Logger LOGGER = Logger.getGlobal();
	
	public static void main(String[] args) throws IOException {
		
		int side = 0;
		switch (args[0]) {
		case "1k":
			side = 32;
			break;
		case "10k":
			side = 100;
			break;
		case "100k":
			side = 316;
			break;
		case "1000k":
			side = 1000;
			break;
		default:
			break;
		}
		
		runAnalysis(args[0], side, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	}

	public static void runAnalysis(String experimentName, int side, int percentagemPoi, int testTimes) throws IOException {
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(PATH_GRAPH+experimentName, side, percentagemPoi);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		GraphGeneratorGrid graphSyntheticReverse = new GraphGeneratorGrid(PATH_GRAPH+experimentName, side, percentagemPoi);
		graphSyntheticReverse.generateGraph();
		GraphBounds graphReverse = graphSynthetic.getGraph();
		
		
		RNNBacktrackingSearch rnnDFS = new RNNBacktrackingSearch(graph);
		RNNBreadthFirstSearch rnnBFS = new RNNBreadthFirstSearch(graphReverse);
		
		Date timeout = DateUtils.parseDate(00, 50, 00);
		Date timestamp = DateUtils.parseDate(00, 00, 00);
		
		FileWriter rnnDFSFileCsv = new FileWriter(experimentName+"_"+percentagemPoi+"_rnn_dfs_baseline.csv");
		FileWriter rnnBFSFileCsv = new FileWriter(experimentName+"_"+percentagemPoi+"_rnn_bfs_solution.csv");
		
		for (int i = 0; i < testTimes; i++) {
			Node customer = getRandomCustomerInGraph(graph);
			runSearchAndWrite(graph, rnnDFS, customer, timeout, timestamp, rnnDFSFileCsv);
			runSearchAndWrite(graph, rnnBFS, customer, timeout, timestamp, rnnBFSFileCsv);
		}
		
		rnnBFSFileCsv.close();
		rnnDFSFileCsv.close();
	}

	private static void runSearchAndWrite(GraphBounds graph, IRNNTimeDependent rnn,
			Node customer, Date timeout, Date timestamp, FileWriter fileCsv) throws IOException {
		try {
			long startTime = System.nanoTime();
			NearestNeighbor solution = rnn.search(customer, timeout, timestamp);
			long endTime = System.nanoTime();
			
			long time = endTime - startTime;
			
			Long solutionId = null;
			Double travelTime = null;
			Integer nodesSize = null;
			ArrayList<Long> path = null;
			int numberVisitedNodes = 0;
			if(solution != null && solution.getPath()!=null) {
				solutionId = solution.getId();
				travelTime = solution.getTravelTime();
				nodesSize = solution.getPath().size();
				path = solution.getPath();
				numberVisitedNodes = solution.getNumberVisitedNodes();
				
				String coordinatesCustomer = customer.getLongitude() + "," + customer.getLatitude();
				
				Node nodePoi = graph.getNode(solutionId);
				String poiCoordinate = nodePoi.getLongitude() + "," + nodePoi.getLatitude();
				String gidPoi = nodePoi.getLabel();
				
				String coordinateNodeVisited = "";
				for (Long visited : path) {
					Node nodeVisited = graph.getNode(visited);
					coordinateNodeVisited = coordinateNodeVisited + "(" + nodeVisited.getLongitude() + "," + nodeVisited.getLatitude() + ")";
				}
				
				String currentLine = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", coordinatesCustomer, 
						poiCoordinate, time, solutionId, travelTime, nodesSize, path, coordinateNodeVisited, gidPoi, numberVisitedNodes) + "\n";
				
				
				System.out.println(currentLine);
				
				fileCsv.write(currentLine);
			}
		} catch(PathNotFoundException e) {
			System.err.println(String.format("Customer %s (%s, %s) has no POI in subgraph", customer.getId(), customer.getLatitude(), customer.getLongitude()));
		}
	}

	
	private static Node getRandomCustomerInGraph(GraphBounds graph) {
		Node node;
 		do {
			long id = Double.valueOf(NumberUtils.generatePdseurandom(0, Long.valueOf(graph.getNumberOfNodes()-1).intValue())).longValue();
			node = graph.getNode(id);
		} while(node.getCategory()!=-1);
		return node;
	}
}
