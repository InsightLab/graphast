package org.graphast.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void read() throws Exception{
		String file = this.getClass().getResource("/fileTest.txt").getFile();
		assertEquals("blablabla\nabc\n", FileUtils.read(file));
	}
}
