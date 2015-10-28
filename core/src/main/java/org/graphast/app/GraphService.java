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
	
	public GraphInfo create(String appName, String url) {
		String osmFile = FileUtils.download(url, Configuration.GRAPHAST_DIR);

		// Guess a good appName
		if (appName == null) {
			appName = osmFile.substring(osmFile.lastIndexOf('/') + 1);
			int pointPos = appName.indexOf('.');
			if (pointPos >= 0) {
				appName = appName.substring(0, pointPos);
			}
		}
		
		String graphDir = Configuration.GRAPHAST_DIR + "/" + appName;
		Graph graph = new OSMImporterImpl(osmFile, graphDir).execute();
		AppGraph.setGraph(graph);
		Configuration.setProperty("graphast." + appName + ".dir", graphDir);
		Configuration.setProperty("graphast." + appName + ".importer", "osm");
		Configuration.setSelectedApp(appName);
		GraphInfo graphInfo = getGraphInfo(graph, graphDir);
		Configuration.addApp(graphInfo);
		Configuration.save();
		return graphInfo;
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
		Graph graph = new GraphBoundsImpl(graphDir);
		graph.load();
		AppGraph.setGraph(graph);
		GraphInfo graphInfo = getGraphInfo(graph, graphDir);
		Configuration.save(graphInfo);
		return graphInfo;
	}

	private GraphInfo getGraphInfo(Graph graph, String graphDir) {
		GraphInfo graphInfo = new GraphInfo();
		graphInfo.setAppName(Configuration.getSelectedApp());
		graphInfo.setGraphDir(graphDir);
		graphInfo.setNumberOfEdges(graph.getNumberOfEdges());
		graphInfo.setNumberOfNodes(graph.getNumberOfNodes());
		graphInfo.setSize(FileUtils.folderSize(graphDir));
		return graphInfo;
	}
	
}
