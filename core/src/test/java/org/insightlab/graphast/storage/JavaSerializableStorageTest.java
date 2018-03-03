package org.insightlab.graphast.storage;

import org.junit.BeforeClass;

public class JavaSerializableStorageTest extends StorageTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		storage = GraphStorageFactory.getJavaSerializableGraphStorage();
	}

}
