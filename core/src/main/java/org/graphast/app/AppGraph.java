package org.graphast.app;

import org.graphast.model.Graph;

public class AppGraph {

	private static Graph graph;
	private static String graphDir;


	private AppGraph() {	
		super();
	}

	public static Graph getGraph() {
		return graph;
	}

	public static void setGraph(Graph graph) {
		AppGraph.graph = graph;
	}

	public static String getGraphDir() {
		return graphDir;
	}

	public static void setGraphDir(String graphDir) {
		AppGraph.graphDir = graphDir;
	} 

}
