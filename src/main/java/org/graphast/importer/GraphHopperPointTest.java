package org.graphast.importer;

import org.graphast.config.Configuration;

import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;

public class GraphHopperPointTest {
	public static void main(String[] args) {

		String defaultGraphLoc = Configuration.getProperty("berlin.graphhopper.dir");

		EncodingManager encodingManager = new EncodingManager("car");
		GraphBuilder gb = new GraphBuilder(encodingManager).setLocation(defaultGraphLoc).setStore(true);
		GraphStorage graphStorage = gb.create();

		graphStorage = gb.load();
		LocationIndex index = new LocationIndexTree(graphStorage, new RAMDirectory(defaultGraphLoc, true));
		if (!index.loadExisting()){
			index.prepareIndex();
		}

		//oeste leste
		QueryResult fromQR = index.findClosest(52.535926,13.192974, EdgeFilter.ALL_EDGES);
		System.out.println("from:" + fromQR.getQueryPoint().lat + "," + fromQR.getQueryPoint().lon);
		QueryResult toQR = index.findClosest(52.52651,13.493285, EdgeFilter.ALL_EDGES);
		System.out.println("to:" + toQR.getQueryPoint().lat + "," + toQR.getQueryPoint().lon);
	}
}
