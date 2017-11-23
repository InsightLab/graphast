package br.ufc.insightlab.graphast.storage;

import java.io.FileNotFoundException;

import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph load(String path, GraphStructure structure) throws FileNotFoundException;
	void save(String path, Graph g);
	
}
