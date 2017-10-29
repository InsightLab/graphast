package org.graphast.storage;

import java.io.File;

public class StorageUtils {
	
	public static boolean deleteMMapGraph(String graphName) {
		String dir = "graphs/MMap/" + graphName + "/";
		File f = new File(dir);
		boolean ok = true;
		if (f.exists()) {
			ok = new File(dir + "nodes.mmap").delete();
			ok = new File(dir + "edges.mmap").delete() && ok;
			ok = f.delete() && ok;
		}
		return ok;
	}

}
