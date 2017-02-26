package org.graphast.util;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.index.strtree.STRtree;

import junit.framework.TestCase;

public class IndexUtilsTest extends TestCase {

	
	@Test
	public void read(){
		Quadtree quadtree = new Quadtree();
		
        Coordinate[] pgc1 = new Coordinate[4];
        pgc1[0] = new Coordinate(10,10);
        pgc1[1] = new Coordinate(10,20);
        pgc1[2] = new Coordinate(20,20);
        pgc1[3] = new Coordinate(20,10);
        
        Coordinate[] pgc2 = new Coordinate[4];
        pgc2[0] = new Coordinate(20,10);
        pgc2[1] = new Coordinate(20,20);
        pgc2[2] = new Coordinate(30,20);
        pgc2[3] = new Coordinate(30,10);

        GeometryFactory geomFac = new GeometryFactory();
        
        LinearRing rgG1 = geomFac.createLinearRing(pgc1);
        LinearRing rgG2 = geomFac.createLinearRing(pgc2);
		
		Envelope vertexEnvelope1 = rgG1.getEnvelopeInternal();
		Envelope vertexEnvelope2 = rgG2.getEnvelopeInternal();
		
		quadtree.insert(vertexEnvelope1, rgG1);
		
		quadtree.insert(vertexEnvelope2, rgG2);
		
		Envelope qPoint = new Envelope(new Coordinate(15, 15));
		
		quadtree.query(qPoint);
		
	}
	
//	@Test
//	public void lucene(){
//		Quadtree quadtree = new Quadtree();
//		
//        Coordinate[] pgc = new Coordinate[10];
//        pgc[0] = new Coordinate(7,7);
//        pgc[1] = new Coordinate(6,9);
//        pgc[2] = new Coordinate(6,11);
//        pgc[3] = new Coordinate(7,12);
//        pgc[4] = new Coordinate(9,11);
//        pgc[5] = new Coordinate(11,12);
//        pgc[6] = new Coordinate(13,11);
//        pgc[7] = new Coordinate(13,9);
//        pgc[8] = new Coordinate(11,7);
//        pgc[9] = new Coordinate(7,7);
//        
//        GeometryFactory geomFac = new GeometryFactory();
//        
//        LinearRing rgG = geomFac.createLinearRing(pgc);
//		
//		Envelope vertexEnvelope = rgG.getEnvelopeInternal();
//		
//		quadtree.insert(vertexEnvelope, rgG);
//		
//	}
}
