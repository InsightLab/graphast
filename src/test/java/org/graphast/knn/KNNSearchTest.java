package org.graphast.knn;

import static org.junit.Assert.assertEquals;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.graphast.config.Configuration;
import org.graphast.enums.GraphBoundsType;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.BoundsKNN;
import org.graphast.query.knn.KNNSearch;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.knn.model.Bound;
import org.graphast.query.knn.model.QueueEntry;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class KNNSearchTest {
	
	private static KNNSearch knn;
	
	private GraphBounds graphPoI;
	
	@Before
	public void setUp() throws Exception{
		graphPoI = new GraphGenerator().generateExamplePoI();
		graphPoI.createBounds();
		
		//calculate or load bounds
		BoundsKNN minBounds = new BoundsKNN(graphPoI, 1, GraphBoundsType.LOWER);
		BoundsKNN maxBounds = new BoundsKNN(graphPoI, 2, GraphBoundsType.UPPER);
		
		knn = new KNNSearch(graphPoI, minBounds, maxBounds);
	}
	
	@Test
	public void search() throws ParseException{
		Date date = DateUtils.parseDate(0, 550, 0);
    	
    	ArrayList<NearestNeighbor> nn = knn.search(graphPoI.getNode(1), date, 2);
    			
    	assertEquals((long)graphPoI.getNode(2).getId(), nn.get(0).getId());
		assertEquals(900000, nn.get(0).getDistance());
		assertEquals((long)graphPoI.getNode(5).getId(), nn.get(1).getId());
		assertEquals(3120000, nn.get(1).getDistance());
				
	}
	
	@AfterClass
	public static void shutdown() throws IOException{
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test/examplePoI");
	}
}	
