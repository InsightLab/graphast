package br.ufc.insightlab.graphast.storage;

import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.structure.GraphStructure;

public interface GraphStorage {
	
	Graph load(String path, GraphStructure structure);
	void save(String path, Graph g);
	
}
