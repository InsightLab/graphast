package org.insightlab.graphast.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;
import org.insightlab.graphast.structure.GraphStructure;

public class JavaSerializationStorage extends GraphStorage {
	
	private static GraphStorage instance = null;
	
	private JavaSerializationStorage() {}
	
	/**
	 * @return the current GraphStorage's instance.
	 */
	public static GraphStorage getInstance() {
		if (instance == null) 
			instance = new JavaSerializationStorage();
		
		return instance;
	}

	@Override
	public Graph load(String path, GraphStructure structure) throws FileNotFoundException {
		Graph g = new Graph(structure);
		
		String directory = StorageUtils.ensureDirectory(path);
		ObjectInputStream oin;
		Object obj;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			
			oin = new ObjectInputStream(new FileInputStream(directory + "nodes.gobj"));
			while((obj = oin.readObject()) != null)
				g.addNode((Node) obj);
			oin.close();
			
			oin = new ObjectInputStream(new FileInputStream(directory + "edges.gobj"));
			while((obj = oin.readObject()) != null)
				g.addEdge((Edge) obj);
			oin.close();
			
			oin = new ObjectInputStream(new FileInputStream(directory + "graph_components.gobj"));
			while((obj = oin.readObject()) != null)
				g.addComponent((GraphComponent) obj);
			oin.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return g;
	}

	@Override
	public void save(String path, Graph graph) {
		
		String directory = StorageUtils.ensureDirectory(path);
		ObjectOutputStream oout;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			oout = new ObjectOutputStream(new FileOutputStream(directory + "nodes.gobj"));
			for (Node n : graph.getNodes())
				oout.writeObject(n);
			oout.writeObject(null);
			oout.close();
			oout = new ObjectOutputStream(new FileOutputStream(directory + "edges.gobj"));
			for (Edge e : graph.getEdges())
				oout.writeObject(e);
			oout.writeObject(null);
			oout.close();
			oout = new ObjectOutputStream(new FileOutputStream(directory + "graph_components.gobj"));
			for (GraphComponent component : graph.getAllComponents())
				oout.writeObject(component);
			oout.writeObject(null);
			oout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
