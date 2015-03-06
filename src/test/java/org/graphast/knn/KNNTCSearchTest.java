package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.graphast.enums.GraphBoundsType;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.graphast.query.knn.BoundsKNN;
import org.graphast.query.knn.BoundsKNNTC;
import org.graphast.query.knn.KNNTCSearch;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public abstract class KNNTCSearchTest {
	private static KNNTCSearch knn;
	
	private GraphBounds graphPoI;

	@Before
	public void setUp() throws Exception{
		graphPoI = new GraphGenerator().generateExamplePoI();
		graphPoI.createBounds();
		
		//calculate or load bounds
		BoundsKNNTC minBounds = new BoundsKNNTC(graphPoI, GraphBoundsType.LOWER);
		BoundsKNNTC maxBounds = new BoundsKNNTC(graphPoI, GraphBoundsType.UPPER);
		
		knn = new KNNTCSearch(graphPoI, minBounds, maxBounds);
	}
	
	@Test
	public void search() throws Exception{
		Date date = DateUtils.parseDate(0, 550, 0);
    	
    	List<NearestNeighbor> nn = knn.search(graphPoI.getVertexByOId(1), date, 2);
    			
    	assertEquals(convertToInt(graphPoI.getVertexByOId(2).getId()), (int) nn.get(0).getId());
		assertEquals(900000, nn.get(0).getDistance());
		assertEquals(convertToInt(graphPoI.getVertexByOId(5).getId()), (int) nn.get(1).getId());
		assertEquals(3120000, nn.get(1).getDistance());
				
	}
	
	@Autowired
	@Qualifier("testDir")
	public void setTestDir(String dir){
		testDir = dir;
	}
	
	@After
	public void tearDown(){
		graphPoI.shutdown();
		lbgAdapter.shutdown();
		ubgAdapter.shutdown();
	}
	
	@AfterClass
	public static void shutdown() throws IOException{
		FileUtils.deleteDirectory(new File(testDir));
	}
	
}
