package org.graphast.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BundleTest {
	
	private Bundle b;

	@Before
	public void setUp() throws Exception {
		b = new Bundle();
		b.putInt("int", 777);
		b.putDouble("double", 98.76);
		b.putLong("long", 11111111111111l);
		b.putString("string", "Testãooooo");
		b.putBoolean("bool", true);
		b.putIntArray("arr", new int[] {3, 2, 1, 6, 5, 4, 9});
		b.putDoubleArray("doubleArr", new double[] {1.2, 3.4, 5.6});
	}

	@Test
	public void testGetInt() {
		assertEquals("get int", 777, b.getInt("int", -1), 0);
		assertEquals("get int not found", 321, b.getInt("x", 321), 0);
	}

	@Test
	public void testGetDouble() {
		assertEquals("get double", 98.76, b.getDouble("double", -1), 0);
		assertEquals("get double not found", 321.123, b.getDouble("x", 321.123), 0);
	}

	@Test
	public void testGetLong() {
		assertEquals("get long", 11111111111111l, b.getLong("long", -1), 0);
		assertEquals("get long not found", 321123321123l, b.getLong("x", 321123321123l), 0);
	}

	@Test
	public void testGetBoolean() {
		assertEquals("get bool", true, b.getBoolean("bool", false));
		assertEquals("get bool not found", false, b.getBoolean("x", false));
	}

	@Test
	public void testGetString() {
		assertEquals("get string", "Testãooooo", b.getString("string", "fail"));
		assertEquals("get string not found", "not found", b.getString("x", "not found"));
	}

	@Test
	public void testGetIntArray() {
		assertArrayEquals("Get int array", new int[] {3, 2, 1, 6, 5, 4, 9}, b.getIntArray("arr"));
		assertEquals("get int array not found", null, b.getIntArray("x"));
	}
	
	@Test
	public void testGetDoubleArray() {
		assertArrayEquals("Get double array", new double[] {1.2, 3.4, 5.6}, b.getDoubleArray("doubleArr"), 0);
		assertEquals("get double array not found", null, b.getDoubleArray("x"));
	}

}
