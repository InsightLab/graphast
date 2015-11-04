package org.graphast.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.graphast.config.Configuration;
import org.graphast.exception.GraphastException;
import org.graphast.importer.CostGenerator;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.importer.POIImporter;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.query.route.osr.BoundsRoute;
import org.graphast.util.FileUtils;
import org.graphast.util.POIUtils;
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
		
		// Generate the PoIs
		String poisFile = Configuration.GRAPHAST_DIR + "/" + appName + "-pois.csv";
		POIUtils.execute(new String[] { "-of", poisFile, pbfFile });
		
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
		
		// Create a reverse graph for use with OSR
		graph.getReverseGraph();
		
		// Create Route Bounds
		BoundsRoute br = new BoundsRoute(graph, (short)0);
		br.createBounds();
		br.save();
	}

	public GraphInfo load(String app) {
		Configuration.reload();
		if (app != null) {
			Configuration.setSelectedApp(app);
		} else {
			
		}
		String graphDir = Configuration.getProperty(app, "dir");
		AppGraph.setGraphDir(graphDir);
		log.debug("graphDir: {}", graphDir);
		GraphBounds graph = new GraphBoundsImpl(graphDir);
		graph.load();
		AppGraph.setGraph(graph);
		GraphInfo graphInfo = getGraphInfo(graph);
		Configuration.save(graphInfo);
		return graphInfo;
	}

	public void delete(String app) {
		String dir = Configuration.getProperty(app, "dir");
		Properties props = Configuration.getConfig();
		Set<Object> set = props.keySet();
		List<String> removeKeys = new ArrayList<String>();
		for (Object o : set) {
			String key = o.toString();
			// The app key should start with graphast.app.<appName>
			if (key.length() > 13) {
				String appName = key.substring(13,key.indexOf(".", 13));
				if (appName.equals(app)) {
					removeKeys.add(key);
				}
			}
		}
		if (removeKeys.size() == 0) {
			throw new GraphastException("Entries for application " + app + " not found in " + Configuration.CONFIG_FILE);
		}
		for (String s : removeKeys) {
			props.remove(s);
		}
		Configuration.save();
		FileUtils.deleteDir(dir);
	}
	
	private GraphInfo getGraphInfo(Graph graph) {
		GraphInfo graphInfo = Configuration.load(Configuration.getSelectedApp());
		graphInfo.setGraphDir(graph.getDirectory());
		graphInfo.setNumberOfEdges(graph.getNumberOfEdges());
		graphInfo.setNumberOfNodes(graph.getNumberOfNodes());
		graphInfo.setSize(FileUtils.folderSize(graph.getAbsoluteDirectory()));
		graphInfo.setNumberOfPoIs(graph.getPOIs().size());
		graphInfo.setNumberOfPoICategories(graph.getCategories().size());
		return graphInfo;
	}
	
}
