package org.insightlab.graphast.storage;

import java.io.File;

public class StorageUtils {
	
	private StorageUtils() {}
	
	public static boolean deleteMMapGraph(String path) {
		String directory = path;
		
		if (!directory.endsWith("/"))
			directory += "/";
		
		File f = new File(directory);
		boolean ok = true;
		
		if (f.exists()) {
			ok = new File(directory + "nodes.mmap").delete();
			ok = new File(directory + "edges.mmap").delete() && ok;
			ok = new File(directory + "treeMap.mmap").delete() && ok;
			ok = f.delete() && ok;
		}
		
		return ok;
	}

}
