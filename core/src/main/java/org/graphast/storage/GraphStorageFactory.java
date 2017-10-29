package org.graphast.storage;

public class GraphStorageFactory {
	
	public static GraphStorage getOSMGraphStorage() {
		return OSMGraphStorage.getInstance();
	}

}
