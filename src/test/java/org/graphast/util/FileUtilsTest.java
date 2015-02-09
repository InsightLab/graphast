package org.graphast.util;

import static org.junit.Assert.assertEquals;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import org.graphast.config.Configuration;
import org.graphast.enums.CompressionType;
import org.junit.Test;

public class FileUtilsTest {

	protected int blockSize = 4096;

	@Test
	public void read() throws Exception{
		String file = this.getClass().getResource("/fileTest.txt").getFile();
		assertEquals("blablabla\nabc\n", FileUtils.read(file));
	}

	@Test
	public void Long2ShortMapTest() throws Exception {
		
		Long2IntMap testMap = new Long2IntOpenHashMap();
		testMap.put(10000L, 100);
		testMap.put(20000L, 200);
		testMap.put(30000L, 300);

		FileUtils.saveLong2IntMap(Configuration.USER_HOME + "/graphast/test/test-file-nocompression", testMap, blockSize, CompressionType.NO_COMPRESSION);
		Long2IntMap loadedMap = FileUtils.loadLong2IntMap(Configuration.USER_HOME + "/graphast/test/test-file-nocompression", blockSize, CompressionType.NO_COMPRESSION);

		assertEquals(100,loadedMap.get(10000L));
		assertEquals(200,loadedMap.get(20000L));
		assertEquals(300,loadedMap.get(30000L));

		FileUtils.saveLong2IntMap(Configuration.USER_HOME + "/graphast/test/test-file-gzipcompression", testMap, blockSize, CompressionType.GZIP_COMPRESSION);
		loadedMap = FileUtils.loadLong2IntMap(Configuration.USER_HOME + "/graphast/test/test-file-gzipcompression", blockSize, CompressionType.GZIP_COMPRESSION);

		assertEquals(100,loadedMap.get(10000L));
		assertEquals(200,loadedMap.get(20000L));
		assertEquals(300,loadedMap.get(30000L));
	
	}
}
