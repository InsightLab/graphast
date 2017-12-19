package org.insightlab.graphast.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {
	
	Node n;
	
	@Before
	public void setUp() throws Exception {
		n = new Node(0); 
	}
	
	@Test
	public void testGetId() {
		assertEquals(0l, n.getId());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new Long(0).hashCode(), n.hashCode());
	}
	
	@Test
	public void testEquals() {
		Node n1 = new Node(0);
		Node n2 = new Node(1);
		
		assertEquals(true, n.equals(n1));
		assertEquals(false, n.equals(n2));
		assertEquals(false, n.equals(new Long(0)));
	}

}
