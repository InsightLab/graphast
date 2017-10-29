package org.graphast.storage;

import org.graphast.model.Graph;
import org.graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph loadGraph(String graphName, GraphStructure structure);
	void saveGraph(String graphName, Graph g);
	
}
