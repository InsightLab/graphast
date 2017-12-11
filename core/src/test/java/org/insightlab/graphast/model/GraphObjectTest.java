package org.insightlab.graphast.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class GraphObjectTest {

	@Test
	public void testString() {
		String data = new String("Hello");
		Node n = new Node(0);
		
		n.putData("key", data);
		
		assertEquals(data, n.getData("key"));
	}
	
	@Test
	public void testInteger() {
		Integer data = new Integer(0);
		Node n = new Node(0);
		
		n.putData("key", data);
		
		assertEquals(data, n.getData("key"));
	}
	
	@Test
	public void testFloat() {
		Float data = new Float(0.0);
		Node n = new Node(0);
		
		n.putData("key", data);
		
		assertEquals(data, n.getData("key"));
	}
	
	@Test
	public void testEdge() {
		Edge data = new Edge(0,1);
		Node n = new Node(0);
		
		n.putData("key", data);
		
		assertEquals(data, n.getData("key"));
	}
	
	@Test
	public void testNull() {
		String data = new String("Hello");
		Node n = new Node(0);
		
		n.putData("key", data);
		
		assertEquals(null, n.getData("not the key"));
	}

}
