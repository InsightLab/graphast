package org.insightlab.graphast.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EdgeTest {
	Edge e;
	
	@Before
	public void setUp() throws Exception {
		e = new Edge(0, 1, 3, true); 
	}
	
	@Test
	public void testFromNode() {
		assertEquals("From node is wrong",0l,e.getFromNodeId());
	}
	
	@Test
	public void testToNode() {
		assertEquals("To node is wrong",1l,e.getToNodeId());
	}
	
	@Test
	public void testCost() {
		assertEquals("Cost is wrong",3.0,e.getCost(),0);
	}
	
	@Test
	public void testBidirectional() {
		assertEquals("Should be bidirectional",true,e.isBidirectional());
	}
	
	@Test
	public void testSetFromNode(){
		e.setFromNodeId(1);
		assertEquals("From node was not updated",1l,e.getFromNodeId());
	}
	
	@Test
	public void testSetToNode(){
		e.setToNodeId(0);
		assertEquals("To node was not updated",0l,e.getToNodeId());
	}
	
	@Test
	public void testSetCost(){
		e.setCost(100);;
		assertEquals("Cost was not updated",100,e.getCost(),0);
	}
	
	@Test
	public void testGetAdjacent(){
		assertEquals("From adjacent not right",1l,e.getAdjacent(0));
		assertEquals("To adjacent not right",0l,e.getAdjacent(1));
	}
}
