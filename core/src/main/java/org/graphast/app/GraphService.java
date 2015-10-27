package org.graphast.app;

import org.graphast.config.Configuration;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.model.Graph;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphService {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void create(String appName, String url) {
		String osmFile = FileUtils.download(url, Configuration.GRAPHAST_DIR);

		// Guess a good appName
		if (appName == null) {
			appName = osmFile.substring(osmFile.lastIndexOf('/') + 1);
			int pointPos = appName.indexOf('.');
			if (pointPos >= 0) {
				appName = appName.substring(0, pointPos);
			}
		}
		
		String graphastDir = Configuration.GRAPHAST_DIR + "/" + appName;
		Graph graph = new OSMImporterImpl(osmFile, graphastDir).execute();
		AppGraph.setGraph(graph);
		Configuration.addApp(appName);
		Configuration.setProperty("graphast." + appName + ".dir", graphastDir);
		Configuration.setProperty("graphast." + appName + ".importer", "osm");
		Configuration.save();
	}

	public void load(String app) {
		Configuration.reload();
		if (app != null) {
			Configuration.setSelectedApp(app);
			Configuration.save();
		}
		String graphDir = Configuration.getProperty("graphast." + Configuration.getSelectedApp() + ".dir");
		AppGraph.setGraphDir(graphDir);
		log.debug("graphDir: {}", graphDir);
		Graph graph = new GraphBoundsImpl(graphDir);
		graph.load();
		AppGraph.setGraph(graph);
	}
	
	public static void main(String[] args) {
		GraphService gs = new GraphService();
		gs.create(null, "http://download.geofabrik.de/europe/monaco-latest.osm.pbf");	
	}
	
}
