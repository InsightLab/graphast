package org.graphast.app;

import org.graphast.model.GraphBounds;

public class AppGraph {

	private static GraphBounds graph;
	private static String graphDir;


	private AppGraph() {	
		super();
	}

	public static GraphBounds getGraph() {
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
