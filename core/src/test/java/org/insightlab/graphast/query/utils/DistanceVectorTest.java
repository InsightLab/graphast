package org.insightlab.graphast.query.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DistanceVectorTest {
	DistanceVector vector;
	DistanceElement el;
	
	@Before
	public void setUp(){
		vector = new DistanceVector(0);
		el = vector.getElement(1l);
		el.changeDistance(2);
		el.changePrevious(0l);
		
	}
	
	@Test
	public void testGetElement() {
		assertEquals(el, vector.getElement(1l));		
	}
	
	@Test
	public void testPrint() {
		vector.print();
		vector.print(0l, 1l);
	}
	
	@Test
	public void testGetDistance(){
		assertEquals(2,vector.getDistance(1),0);
	}

}
