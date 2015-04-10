package org.graphast.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.graphast.util.GeoUtils.latLongToInt;
import static org.graphast.util.GeoUtils.latLongToDouble;

public class GeoUtilsTest {
	
	@Test
	public void latLongToIntTest() {
		assertEquals(52535927, latLongToInt(52.535926895));
		assertEquals(52.535926d, latLongToDouble(52535926), 0);
	}
	
}
