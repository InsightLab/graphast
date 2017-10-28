package org.graphast.storage;

import java.io.File;

public class StorageUtils {
	
	public static boolean deleteGraph(String graphName) {
		File f = new File(graphName + "/");
		boolean ok = true;
		if (f.exists()) {
			ok = new File(graphName + "/nodes.mmap").delete();
			ok = new File(graphName + "/edges.mmap").delete() && ok;
			ok = f.delete() && ok;
		}
		return ok;
	}

}
