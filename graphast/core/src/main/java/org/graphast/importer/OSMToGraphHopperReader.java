package org.graphast.importer;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;

public class OSMToGraphHopperReader {

	public static GraphHopper createGraph(String osmFile, String graphDir, boolean enableCHShortcuts, boolean forMobile) {
		GraphHopper hopper;
		if(forMobile){
			hopper = new GraphHopper().forMobile();
		}else{
			hopper = new GraphHopper().forServer();
		}
		
		if(osmFile!=null) {
			
			hopper.setOSMFile(osmFile);
		
		}
		
		
		hopper.setGraphHopperLocation(graphDir);
		hopper.setEncodingManager(new EncodingManager("car"));
		if(enableCHShortcuts){
			hopper.setCHShortcuts("shortest");
		}else{
			hopper.disableCHShortcuts();
		}

		hopper.importOrLoad();

		return hopper;
	}
}
