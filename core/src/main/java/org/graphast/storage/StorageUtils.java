package org.graphast.storage;

import java.io.File;

public class StorageUtils {
	
	public static boolean deleteMMapGraph(String path) {
		if (!path.endsWith("/")) path += "/";
		File f = new File(path);
		boolean ok = true;
		if (f.exists()) {
			ok = new File(path + "nodes.mmap").delete();
			ok = new File(path + "edges.mmap").delete() && ok;
			ok = f.delete() && ok;
		}
		return ok;
	}

}
