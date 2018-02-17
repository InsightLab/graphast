/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

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
	public void testConstructor1(){
		e = new Edge(0,1);
		assertEquals(0l, e.getFromNodeId());
		assertEquals(1l, e.getToNodeId());
		assertEquals(1l, e.getWeight(),0);
		assertEquals(false, e.isBidirectional());
	}
	
	@Test
	public void testConstructor2(){
		e = new Edge(0,1,3);
		assertEquals(0l, e.getFromNodeId());
		assertEquals(1l, e.getToNodeId());
		assertEquals(3l, e.getWeight(),0);
		assertEquals(false, e.isBidirectional());
	}
	
	@Test
	public void testConstructor3(){
		e = new Edge(0, 1, true); 
		assertEquals(0l, e.getFromNodeId());
		assertEquals(1l, e.getToNodeId());
		assertEquals(1l, e.getWeight(),0);
		assertEquals(true, e.isBidirectional());
	}
	
	@Test
	public void testConstructor4(){ 
		assertEquals(0l, e.getFromNodeId());
		assertEquals(1l, e.getToNodeId());
		assertEquals(3l, e.getWeight(),0);
		assertEquals(true, e.isBidirectional());
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
		assertEquals("Cost is wrong",3.0,e.getWeight(),0);
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
		e.setWeight(100);;
		assertEquals("Cost was not updated",100,e.getWeight(),0);
	}
	
	@Test
	public void testGetAdjacent(){
		assertEquals("From adjacent not right",1l,e.getAdjacent(0));
		assertEquals("To adjacent not right",0l,e.getAdjacent(1));
	}
	
	@Test
	public void testEquals(){
		Edge e1 = new Edge(0,1,3,true);
		Edge e2 = new Edge(0,2,3,true);
		Edge e3 = new Edge(3,1,3,true);
		Edge e4 = new Edge(0,1,4,true);
//		Edge e5 = new Edge(0,1,3,false);
		
//		assertEquals(false, e.equals(new Long(5)));
		
		assertEquals(true, e.equals(e1));
		
		assertEquals(false, e.equals(e2));
		assertEquals(false, e.equals(e3));
		assertEquals(false, e.equals(e4));
//		assertEquals(false, e.equals(e5));
	}
	
	@Test
	public void testToString(){
		assertEquals("0|1|3.0", e.toString());
	}
	
	@Test
	public void testHashCode(){
		assertEquals("0|1|3.0".hashCode(), e.hashCode());
	}
}
