package org.insightlab.graphast.storage;

public class GraphStorageFactory {
	
	public static GraphStorage getOSMGraphStorage() {
		return OSMGraphStorage.getInstance();
	}

}
