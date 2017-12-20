package org.insightlab.graphast.storage;

import java.io.FileNotFoundException;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph load(String path, GraphStructure structure) throws FileNotFoundException;
	void save(String path, Graph graph);
	
}