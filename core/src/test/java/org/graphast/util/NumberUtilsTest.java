package org.graphast.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NumberUtilsTest {
	
	@Test
	public void testRound() {
		
		assertEquals(1, Math.round(1.4));
		assertEquals(1, Math.round(1.4999999));
		assertEquals(2, Math.round(1.5));
		assertEquals(2, Math.round(1.51));
		assertEquals(1.14, NumberUtils.round(1.135, 2), 0);
		assertEquals(1.13, NumberUtils.round(1.134, 2), 0);
		assertEquals(1.13, NumberUtils.round(1.1349999, 2), 0);
		assertEquals(1.135, NumberUtils.round(1.135499999, 3), 0);
		assertEquals(1.136, NumberUtils.round(1.1355d, 3), 0);
		assertEquals(1.12346, NumberUtils.round(1.123455d, 5), 0);
		assertEquals(1.12345, NumberUtils.round(1.123454, 5), 0);
		assertEquals(-1.12346, NumberUtils.round(-1.123455, 5), 0);
		assertEquals(10.0, NumberUtils.round(10.4, 0), 0);
		assertEquals(11.0, NumberUtils.round(10.5, 0), 0);
		assertEquals(-11.0, NumberUtils.round(-10.5, 0), 0);
		assertEquals(11.0, NumberUtils.round(10.51, 0), 0);
		assertEquals(10.5, NumberUtils.round(10.49999999999999, 2), 0);
		assertEquals(2.289, NumberUtils.round(2.2893492007270168, 3), 0);
		assertEquals(2.29, NumberUtils.round(2.2893492007270168, 2), 0);
		assertEquals(0.0002, NumberUtils.round(2.2893492007270168E-4, 4), 0);
		assertEquals(0.00023, NumberUtils.round(2.2893492007270168E-4, 5), 0);
		assertEquals(0.000229, NumberUtils.round(2.2893492007270168E-4, 6), 0);
	
	}
	
	@Test
	public void int2ShortTest(){
		
		int intMax = Integer.MAX_VALUE;
		short segmentMax = NumberUtils.segment(intMax);
		short offsetMax = NumberUtils.displacement(intMax);
		
		assertEquals(32767, segmentMax);
		assertEquals(-1, offsetMax);
		assertEquals(Integer.MAX_VALUE, NumberUtils.index(segmentMax, offsetMax));

		int intMin = Integer.MIN_VALUE;
		short segmentMin = NumberUtils.segment(intMin);
		short offsetMin = NumberUtils.displacement(intMin);
		
		assertEquals(-32768, segmentMin);
		assertEquals(0, offsetMin);
		assertEquals(Integer.MIN_VALUE, NumberUtils.index(segmentMin, offsetMin));
		
	}
	
	@Test
	public void javaRoundingTest(){
		assertEquals(129520, (int)(129.521d * 1000));
	}

}
