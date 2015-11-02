package org.graphast.app;

import org.graphast.config.Configuration;
import org.graphast.model.GraphBounds;

public class AppGraph {

	private static GraphBounds graph;
	private static String graphDir;
	private static GraphService service = new GraphService();

	private AppGraph() {	
		super();
	}

	public static GraphBounds getGraph() {
		if (graph == null) {
			service.load(Configuration.getSelectedApp());
		}
		return graph;
	}

	public static void setGraph(GraphBounds graph) {
		AppGraph.graph = graph;
	}

	public static String getGraphDir() {
		return graphDir;
	}

	public static void setGraphDir(String graphDir) {
		AppGraph.graphDir = graphDir;
	} 

}
