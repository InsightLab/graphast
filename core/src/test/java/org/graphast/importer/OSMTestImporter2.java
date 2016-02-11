package org.graphast.importer;

import static org.junit.Assert.assertEquals;

import org.graphast.config.Configuration;
import org.graphast.model.Graph;
import org.junit.BeforeClass;
import org.junit.Test;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Helper;

public class OSMTestImporter2 {

	private static String graphHopperDir;
	private static String graphastDir;
	private static Graph graph;
	
	@BeforeClass
	public static void setup() {
		graphHopperDir = Configuration.USER_HOME + "/graphhopper/test/example";
		graphastDir = Configuration.USER_HOME + "/graphast/test/example";

		EncodingManager encodingManager = new EncodingManager("car");
	    GraphBuilder gb = new GraphBuilder(encodingManager).setLocation(graphHopperDir).setStore(true);
	    GraphStorage graphStorage = gb.create();
		
		NodeAccess na = graphStorage.getNodeAccess();
        na.setNode(0, 0, 2);
        na.setNode(1, 0, 1);
        na.setNode(2, 0, 4);
        na.setNode(3, 0, 3);
        na.setNode(4, 0, 5);
        na.setNode(5, 1, 1);

        EdgeIteratorState iter1 = graphStorage.edge(1, 5, 2, true);
        iter1.setWayGeometry(Helper.createPointList(3.5, 4.5, 5, 6));
        EdgeIteratorState iter2 = graphStorage.edge(5, 0, 3, false);
        iter2.setWayGeometry(Helper.createPointList(1.5, 1, 2, 3));
        graphStorage.edge(0, 3, 2, true);
        graphStorage.edge(3, 2, 2, false);
        graphStorage.edge(2, 3, 2, false);
        graphStorage.edge(2, 4, 2, true);
        
        iter1.setName("named street1");
        iter2.setName("named street2");
        
        graphStorage.flush();
        graphStorage.close();
        graph = new OSMImporterImpl(null, graphHopperDir, graphastDir).execute();
	}

	@Test
	public void executeTest() {
		assertEquals(6, graph.getNumberOfNodes());
		assertEquals(9, graph.getNumberOfEdges());
	}
	
}
