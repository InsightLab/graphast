package org.graphast.util;

import static org.graphast.util.GeoUtils.latLongToDouble;
import static org.graphast.util.GeoUtils.latLongToInt;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class GeoUtilsTest {
	
	@Test
	public void latLongToIntTest() {
		assertEquals(52535927, latLongToInt(52.535926895));
		assertEquals(52.535926d, latLongToDouble(52535926), 0);
	}
	
	@Test
	public void quadTreeTest(){
		
		double epsilon = 1; 
		
		Quadtree quadtree = new Quadtree();
		
        Coordinate[] pgc1 = new Coordinate[5];
        pgc1[0] = new Coordinate(10,10);
        pgc1[1] = new Coordinate(10,20);
        pgc1[2] = new Coordinate(20,20);
        pgc1[3] = new Coordinate(20,10);
        pgc1[4] = new Coordinate(10,10);
        
        Coordinate[] pgc2 = new Coordinate[5];
        pgc2[0] = new Coordinate(20,10);
        pgc2[1] = new Coordinate(20,20);
        pgc2[2] = new Coordinate(30,20);
        pgc2[3] = new Coordinate(30,10);
        pgc2[4] = new Coordinate(20,10);

        GeometryFactory geomFac = new GeometryFactory();
        
        LinearRing rgG1 = geomFac.createLinearRing(pgc1);
        LinearRing rgG2 = geomFac.createLinearRing(pgc2);
		
		Envelope vertexEnvelope1 = rgG1.getEnvelopeInternal();
		Envelope vertexEnvelope2 = rgG2.getEnvelopeInternal();
		
		quadtree.insert(vertexEnvelope1, rgG1);
		
		quadtree.insert(vertexEnvelope2, rgG2);
		
//		Envelope qPoint = new Envelope(new Coordinate(15, 15));
//		
//		System.out.println(quadtree.query(qPoint));
		
		Envelope queryEnv = new Envelope(new Coordinate(15, 15));
	    queryEnv.expandBy(epsilon);
	    QuadTreeVisitor visitor = new QuadTreeVisitor(epsilon, new Coordinate(15, 15));
	    
	    quadtree.query(queryEnv, visitor);
		
		
		
	}
	
	private static class EnvelopeWithIndex { 
        private int index; 
        private Coordinate position; 
        private Set<Integer> sharingTriangles = new HashSet<Integer>(); 
 
        public EnvelopeWithIndex(int index, Coordinate position) { 
            this.index = index; 
            this.position = position; 
        } 
 
        public void addSharingTriangle(int triangleId) { 
            sharingTriangles.add(triangleId); 
        } 
 
        public Set<Integer> getSharingTriangles() { 
            return Collections.unmodifiableSet(sharingTriangles); 
        } 
    } 
	
	private static class QuadTreeVisitor implements ItemVisitor { 
        private EnvelopeWithIndex nearest = null; 
        private double nearestDistance; 
        private final double maxDist; 
        private final Coordinate goal; 
 
        public QuadTreeVisitor(double maxDist, Coordinate goal) { 
            this.maxDist = maxDist; 
            this.goal = goal; 
        } 
 
        public EnvelopeWithIndex getNearest() { 
            return nearest; 
        } 
 
        @Override 
        public void visitItem(Object item) { 
            EnvelopeWithIndex idx = (EnvelopeWithIndex)item; 
            if(goal == idx.position) { 
                nearest = idx; 
                throw new RuntimeException("Found.."); 
            } else { 
                double itemDistance = idx.position.distance(goal); 
                if(itemDistance < maxDist && (nearest == null || nearestDistance > itemDistance)) { 
                    nearest = idx; 
                    nearestDistance = itemDistance; 
                } 
            } 
        } 
    } 
	
	
}
