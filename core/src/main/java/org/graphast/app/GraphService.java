package org.graphast.app;

import java.util.List;

import org.graphast.config.Configuration;
import org.graphast.exception.GraphastException;
import org.graphast.importer.CostGenerator;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.importer.POIImporter;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.util.FileUtils;
import org.graphast.util.POIScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphService {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void create(GraphInfo graphInfo) {
		String importer = graphInfo.getImporter();
		if (importer == null || importer.equalsIgnoreCase("osm")) {
			createOSM(graphInfo);
		} else {
			throw new GraphastException(importer + " importer not available");
		}
		Configuration.save(graphInfo);
	}
	
	public void createOSM(GraphInfo graphInfo) {
		if (graphInfo.getImporter() == null) {
			graphInfo.setImporter("osm");
		}
		
		// Download OSM file
		String pbfFile = FileUtils.download(graphInfo.getNetwork(), Configuration.GRAPHAST_DIR);
		
		// Guess a good appName if it does not exist
		String appName = graphInfo.getAppName();
		if (appName == null) {
			appName = pbfFile.substring(pbfFile.lastIndexOf('/') + 1);
			int pointPos = appName.indexOf('.');
			if (pointPos >= 0) {
				appName = appName.substring(0, pointPos);
			}
			graphInfo.setAppName(appName);
		}
		
		//Get diretory where the graph must be saved 
		String graphDir = graphInfo.getGraphDir();
		if (graphDir == null) {
			graphDir = Configuration.GRAPHAST_DIR + "/" + appName;
			graphInfo.setGraphDir(graphDir);
		}
		GraphBounds graph = new OSMImporterImpl(pbfFile, graphDir).execute();
		AppGraph.setGraph(graph);
		graphInfo.setNumberOfNodes(graph.getNumberOfNodes());
		graphInfo.setNumberOfEdges(graph.getNumberOfEdges());
		graphInfo.setSize(FileUtils.folderSize(graph.getDirectory()));
		Configuration.setSelectedApp(appName);
		Configuration.addApp(graphInfo);
		
		// Generate the PoIs
		String poisFile = Configuration.GRAPHAST_DIR + "/" + appName + "-pois.csv";
		POIScanner.execute(new String[] { "-of", poisFile, pbfFile });
		
		// Import the PoIs
		List<Integer> poiCategoryFilter = graphInfo.getPoiCategoryFilter();
		int numberOfPoIs = POIImporter.importPoIList(graph, poisFile, poiCategoryFilter);
		graphInfo.setNumberOfPoIs(numberOfPoIs);
		log.info("{} PoIs imported", numberOfPoIs);
		
		// Generate random costs to all Egdes
		CostGenerator.generateAllSyntheticEdgesCosts(graph);
		log.info("Random costs generated");
		
		// Get number of POI Categories 
		// TODO to this in a better way
		if (graph.getPOICategories() != null) {
			graphInfo.setNumberOfPoICategories(graph.getPOICategories().size());
		}
		
		graph.save();
		Configuration.save(graphInfo);
	}

	public GraphInfo load(String app) {
		Configuration.reload();
		if (app != null) {
			Configuration.setSelectedApp(app);
			Configuration.save();
		}
		String graphDir = Configuration.getProperty("graphast." + Configuration.getSelectedApp() + ".dir");
		AppGraph.setGraphDir(graphDir);
		log.debug("graphDir: {}", graphDir);
		GraphBounds graph = new GraphBoundsImpl(graphDir);
		graph.load();
		AppGraph.setGraph(graph);
		GraphInfo graphInfo = getGraphInfo(graph);
		Configuration.save(graphInfo);
		return graphInfo;
	}

	private GraphInfo getGraphInfo(Graph graph) {
		GraphInfo graphInfo = Configuration.load(Configuration.getSelectedApp());
		graphInfo.setGraphDir(graph.getDirectory());
		graphInfo.setNumberOfEdges(graph.getNumberOfEdges());
		graphInfo.setNumberOfNodes(graph.getNumberOfNodes());
		graphInfo.setSize(FileUtils.folderSize(graph.getDirectory()));
		graphInfo.setNumberOfPoIs(graph.getPOIs().size());
		graphInfo.setNumberOfPoICategories(graph.getCategories().size());
		return graphInfo;
	}
	
	public static void main(String[] args) {
		GraphService service = new GraphService();
		//service.load("monaco");
		GraphInfo gi = new GraphInfo();
		gi.setAppName("monaco-test4");
		gi.setNetwork("http://localhost:8000/monaco-test.osm.pbf");
		service.create(gi);
		//service.create("http://localhost:8000/monaco-test.osm.pbf");
	}
	
}
