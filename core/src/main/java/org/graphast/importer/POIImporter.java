package org.graphast.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.graphast.exception.GraphastException;
import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;

public class POIImporter {

	// private static Logger log = LoggerFactory.getLogger(POIImporter.class);

	/**
	 * 
	 * @param graph
	 *            The graph that will receive the POIs.
	 * @param path
	 *            The file path from the file containing the POIs.
	 * @param poisFilter
	 *            A list of POI category IDs that must be imported.
	 * @return the number of created PoIs.
	 */
	public static int importPoIList(Graph graph, String path, List<Integer> poisFilter) {

		File f = new File(path);

		if (!(f.exists() && f.isFile())) {
			throw new GraphastException("File " + path + " does not exist or it is not a file.");
		}

		try {
			InputStream is = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String row;
			String[] splittedRow;

			int[] poiCosts = new int[96];

			Random random = new Random();

			int minTime, maxTime;

			minTime = 1;
			maxTime = 300000;
			int numberOfPoIs = 0;

			for (int i = 0; i < 96; i++) {
				poiCosts[i] = random.nextInt(maxTime - minTime) + minTime;
			}

			if (poisFilter != null) {

				while ((row = br.readLine()) != null) {

					splittedRow = row.split("\\|");

					if (poisFilter.contains(Integer.parseInt(splittedRow[0]))) {
						numberOfPoIs++;
						Node n = graph.getNearestNode(Double.parseDouble(splittedRow[2]),
								Double.parseDouble(splittedRow[3]));

						n.setCategory(Integer.parseInt(splittedRow[0]));
						n.setLabel(splittedRow[4]);

						n.setCosts(poiCosts);

						graph.updateNodeInfo(n);
						// System.out.println(graph.getNearestNode(Double.parseDouble(splittedRow[2]),
						// Double.parseDouble(splittedRow[3])).getCategory());
					}
				}

			} else {

				while ((row = br.readLine()) != null) {
					numberOfPoIs++;

					splittedRow = row.split("\\|");

					Node n = graph.getNearestNode(Double.parseDouble(splittedRow[2]),
							Double.parseDouble(splittedRow[3]));

					n.setCategory(Integer.parseInt(splittedRow[0]));
					n.setLabel(splittedRow[4]);

					n.setCosts(poiCosts);

					graph.updateNodeInfo(n);
					// System.out.println(graph.getNearestNode(Double.parseDouble(splittedRow[2]),
					// Double.parseDouble(splittedRow[3])).getCategory());
				}

			}
			br.close();
			return numberOfPoIs;
		} catch (Exception e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param graph
	 *            The CHGraph that will receive the POIs.
	 * @param path
	 *            The file path from the file containing the POIs.
	 * @param poisFilter
	 *            A list of POI category IDs that must be imported.
	 * @return the number of created PoIs.
	 */
	public static int importPoIList(CHGraph graph, String path, List<Integer> poisFilter) {

		File f = new File(path);

		if (!(f.exists() && f.isFile())) {
			throw new GraphastException("File " + path + " does not exist or it is not a file.");
		}

		try {
			InputStream is = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String row;
			String[] splittedRow;

			int[] poiCosts = new int[96];

			Random random = new Random();

			int minTime, maxTime;

			minTime = 1;
			maxTime = 300000;
			int numberOfPoIs = 0;

			for (int i = 0; i < 96; i++) {
				poiCosts[i] = random.nextInt(maxTime - minTime) + minTime;
			}

			if (poisFilter != null) {

				while ((row = br.readLine()) != null) {

					splittedRow = row.split("\\|");

					if (poisFilter.contains(Integer.parseInt(splittedRow[0]))) {
						numberOfPoIs++;
						CHNode n = (CHNode) graph.getNearestNode(Double.parseDouble(splittedRow[2]),
								Double.parseDouble(splittedRow[3]));

						n.setCategory(1);
						n.setLabel(splittedRow[4]);

						n.setCosts(poiCosts);

						graph.updateNodeInfo(n);
						// System.out.println(graph.getNearestNode(Double.parseDouble(splittedRow[2]),
						// Double.parseDouble(splittedRow[3])).getCategory());
					}
				}

			} else {

				while ((row = br.readLine()) != null) {
					numberOfPoIs++;

					splittedRow = row.split("\\|");

					CHNode n = (CHNode) graph.getNearestNode(Double.parseDouble(splittedRow[2]),
							Double.parseDouble(splittedRow[3]));

					n.setCategory(1);
					n.setLabel(splittedRow[4]);

					n.setCosts(poiCosts);

					graph.updateNodeInfo(n);
					// System.out.println(graph.getNearestNode(Double.parseDouble(splittedRow[2]),
					// Double.parseDouble(splittedRow[3])).getCategory());
				}

			}
			br.close();
			return numberOfPoIs;
		} catch (Exception e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void importPoIList(Graph graph, String path) {
		POIImporter.importPoIList(graph, path, null);
	}

	public static void importPoIList(CHGraph graph, String path) {
		POIImporter.importPoIList(graph, path, null);
	}

	public static void generateRandomPoIs(Graph graph, double density) {

		int numberOfPoIsGenerated = (int) ((int) graph.getNumberOfNodes() * (density / 100));

		
		List<Long> nodeIDs = new ArrayList<>();
		for(long i = 0; i<graph.getNumberOfNodes(); i++)
			nodeIDs.add(i);
		
		Collections.shuffle(nodeIDs);
		
		for(int j = 0; j<numberOfPoIsGenerated; j++) {
			Node n = graph.getNode(nodeIDs.get(j));
			n.setCategory(4);
			graph.updateNodeInfo(n);
		}

	}

}
