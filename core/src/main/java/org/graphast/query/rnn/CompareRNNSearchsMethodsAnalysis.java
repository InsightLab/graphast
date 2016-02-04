package org.graphast.query.rnn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.graphast.config.Configuration;
import org.graphast.exception.PathNotFoundException;
import org.graphast.importer.OSMDBImporter;
import org.graphast.model.GraphBounds;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.BenchmarkMemory;
import org.graphast.util.DateUtils;
import org.graphast.util.NumberUtils;

public class CompareRNNSearchsMethodsAnalysis {
	private static final String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	protected static final Logger LOGGER = Logger.getGlobal();
	
	public static void main(String[] args) throws IOException {
		
		runAnalysis("view_exp_1k", 10);
		runAnalysis("view_exp_10k", Integer.parseInt(args[0]));
		runAnalysis("view_exp_50k", Integer.parseInt(args[0]));
		runAnalysis("view_exp_100k", Integer.parseInt(args[0]));
	}

	public static void runAnalysis(String tableName, int testTimes) throws IOException {
		
		OSMDBImporter importer = new OSMDBImporter(tableName, PATH_GRAPH+tableName);
		GraphBounds graph = importer.execute();
		
		OSMDBImporter importerReverse = new OSMDBImporter(tableName, PATH_GRAPH+tableName+"_reverse");
		GraphBounds graphReverse = importerReverse.execute();
		
		
		RNNBacktrackingSearch rnnDFS = new RNNBacktrackingSearch(graph);
		RNNBreadthFirstSearch rnnBFS = new RNNBreadthFirstSearch(graphReverse);
		
		Date timeout = DateUtils.parseDate(00, 50, 00);
		Date timestamp = DateUtils.parseDate(00, 00, 00);
		
		FileWriter rnnBacktrackingFileCsv = new FileWriter(tableName+"_rnn_baseline.csv");
		FileWriter rnnBFSFileCsv = new FileWriter(tableName+"_rnn_bfs_proposed_solution.csv");
		
		for (int i = 0; i < testTimes; i++) {
			Node customer = getRandomCustomerInGraph(graph);
			runSearchAndWrite(graph, rnnDFS, customer, timeout, timestamp, rnnBacktrackingFileCsv);
			runSearchAndWrite(graph, rnnBFS, customer, timeout, timestamp, rnnBFSFileCsv);
		}
		
		rnnBacktrackingFileCsv.close();
		rnnBFSFileCsv.close();
		
	}

	private static void runSearchAndWrite(GraphBounds graph, IRNNTimeDependent rnn,
			Node customer, Date timeout, Date timestamp, FileWriter fileCsv) throws IOException {
		try {
			
			//List<Integer> listIdProcess = BenchmarkMemory.listIdProcess();
			long numberUseMemoryInit = BenchmarkMemory.getUsedMemory();
			long startTime = System.nanoTime();
			long ioProcessReadInit = 0;
			long ioProcessWriteInit = 0;
			
			/*for (Integer pid : listIdProcess) {
				ioProcessReadInit = ioProcessReadInit + BenchmarkMemory.ioProcess(pid, BenchmarkMemory.PROCESS.READ);
				ioProcessWriteInit = ioProcessWriteInit + BenchmarkMemory.ioProcess(pid, BenchmarkMemory.PROCESS.WRITE);
			}*/
			
			NearestNeighbor solution = null;
			solution = rnn.search(customer, timeout, timestamp);
			
			long ioProcessReadEnd = 0;
			long ioProcessWriteEnd = 0;
			
			/*for (Integer pid : listIdProcess) {
				ioProcessReadEnd =  ioProcessReadEnd + BenchmarkMemory.ioProcess(pid, BenchmarkMemory.PROCESS.READ);
				ioProcessWriteEnd =  ioProcessWriteEnd + BenchmarkMemory.ioProcess(pid, BenchmarkMemory.PROCESS.WRITE);
			}*/
			
			long endTime = System.nanoTime();
			long numberUseMemoryFinal = BenchmarkMemory.getUsedMemory(); 
			
			long time = endTime - startTime;
			long numerUseMemory = (numberUseMemoryFinal - numberUseMemoryInit) / 1024;
			
			long read = ioProcessReadEnd - ioProcessReadInit;
			long write = ioProcessWriteEnd - ioProcessWriteInit;
			
			System.out.println(" READ "+read);
			System.out.println(" WRITE "+write);

			Long solutionId = null;
			Double travelTime = null;
			Integer nodesSize = null;
			ArrayList<Long> path = null;
			Long externalId = null;
			int numberVisitedNodes = 0;
			if(solution != null && solution.getPath()!=null) {
				solutionId = solution.getId();
				travelTime = solution.getTravelTime();
				nodesSize = solution.getPath().size();
				path = solution.getPath();
				externalId = Integer.valueOf(graph.getNode(solution.getId()).getCategory()).longValue();
				numberVisitedNodes = solution.getNumberVisitedNodes();
				
				String coordinatesCustomer = customer.getLongitude() + "," + customer.getLatitude();
				String gidCustomer = customer.getLabel();
				
				Node nodePoi = graph.getNode(solutionId);
				String poiCoordinate = nodePoi.getLongitude() + "," + nodePoi.getLatitude();
				String gidPoi = nodePoi.getLabel();
				
				String coordinateNodeVisited = "";
				String gidVisited = "";
				for (Long visited : path) {
					Node nodeVisited = graph.getNode(visited);
					coordinateNodeVisited = coordinateNodeVisited + "(" + nodeVisited.getLongitude() + "," + nodeVisited.getLatitude() + ")";
					
					gidVisited = gidVisited + "-" + nodeVisited.getLabel();
				}
				
				String currentLine = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", coordinatesCustomer, 
						poiCoordinate, time, solutionId, externalId, travelTime, 
						nodesSize, path, coordinateNodeVisited, gidCustomer, gidPoi, gidVisited, numberVisitedNodes, numerUseMemory) + "\n";
				
				
				System.out.println(currentLine);
				fileCsv.write(currentLine);
			}
		} catch(PathNotFoundException e) {
			System.err.println(String.format("Customer %s (%s, %s) has no POI in subgraph", customer.getId(), customer.getLatitude(), customer.getLongitude()));
		}
	}

	private static Node getRandomCustomerInGraph(GraphBounds graph) {
		Node node;
		double[] bounds = new double[]{-3.710467, -38.591078, -3.802376, -38.465530};
 		do {
			long id = Double.valueOf(NumberUtils.generatePdseurandom(0, Long.valueOf(graph.getNumberOfNodes()-1).intValue())).longValue();
			node = graph.getNode(id);
		} while(node.getCategory()!=-1 || node.getLatitude()>bounds[0] || node.getLatitude()<bounds[2] || node.getLongitude()<bounds[1] || node.getLongitude()>bounds[3]);
		return node;
	}

}
