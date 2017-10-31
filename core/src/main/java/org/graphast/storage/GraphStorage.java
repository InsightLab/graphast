package org.graphast.storage;

import org.graphast.model.Graph;
import org.graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph load(String path, GraphStructure structure);
	void save(String path, Graph g);
	
}
