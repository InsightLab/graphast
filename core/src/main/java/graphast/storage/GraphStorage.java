package graphast.storage;

import graphast.model.Graph;
import graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph load(String path, GraphStructure structure);
	void save(String path, Graph g);
	
}
