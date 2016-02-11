package org.graphast.app;

import java.util.Map;

import org.graphast.config.Configuration;
import org.graphast.model.GraphBounds;
import org.graphast.util.POIUtils;

public class AppGraph {

	private static GraphBounds graph;
	private static String graphDir;
	private static GraphService service = new GraphService();
	private static Map<Integer, String> poiCategories;

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

	public static Map<Integer, String> getAllPoiCategories() {
		if (poiCategories == null) {
			poiCategories = POIUtils.readPoICategories(AppGraph.class.getResourceAsStream("/poi_types.csv"));
		}
		return poiCategories;
	}

	public static void setPoiCategories(Map<Integer, String> poiCategories) {
		AppGraph.poiCategories = poiCategories;
	} 

}
