package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.config.Configuration;
import org.graphast.enums.GraphBoundsType;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.BoundsKNN;
import org.graphast.query.knn.KNNSearch;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.graphhopper.util.StopWatch;

public class KNNSearchTest {
	
	private static KNNSearch knn, knnMonaco;
	
	private GraphBounds graphPoI;
	private GraphBounds graphMonacoPoI;
	
	@Before
	public void setUp() throws Exception{
		
		graphMonacoPoI = new GraphGenerator().generateMonaco();
		graphMonacoPoI.createBounds();
		
		graphPoI = new GraphGenerator().generateExamplePoI();
		graphPoI.createBounds();
		
		//calculate or load bounds
		BoundsKNN minBoundsMonaco = new BoundsKNN(graphMonacoPoI, GraphBoundsType.LOWER);
		BoundsKNN maxBoundsMonaco = new BoundsKNN(graphMonacoPoI, GraphBoundsType.UPPER);
		
		knnMonaco = new KNNSearch(graphMonacoPoI, minBoundsMonaco, maxBoundsMonaco);
		
		//calculate or load bounds
		BoundsKNN minBounds = new BoundsKNN(graphPoI, GraphBoundsType.LOWER);
		BoundsKNN maxBounds = new BoundsKNN(graphPoI, GraphBoundsType.UPPER);
		
		knn = new KNNSearch(graphPoI, minBounds, maxBounds);
	}
	
	@Test
	public void searchMonaco() throws ParseException {
		
		// TODO Verify if the date is being used
		Date date = DateUtils.parseDate(0, 550, 0);
		
		StopWatch knnSW = new StopWatch();
		knnSW.start();
		ArrayList<NearestNeighbor> nn = knnMonaco.search(graphMonacoPoI.getNode(177), date, 3);
		knnSW.stop();
		
		System.out.println(knnSW.getNanos());
		System.out.println("teste");
		
	}
	
	@Test
	public void search() throws ParseException{
		Date date = DateUtils.parseDate(0, 550, 0);
    	
    	ArrayList<NearestNeighbor> nn = knn.search(graphPoI.getNode(7), date, 2);
    			
    	assertEquals((long)graphPoI.getNode(4).getId(), nn.get(0).getId());
		assertEquals(190, nn.get(0).getDistance());
		assertEquals((long)graphPoI.getNode(9).getId(), nn.get(1).getId());
		assertEquals(260, nn.get(1).getDistance());
				
	}
	
	@AfterClass
	public static void shutdown() throws IOException{
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test/examplePoI");
	}
}	
