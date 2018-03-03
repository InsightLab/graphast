package org.insightlab.graphast.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.insightlab.graphast.model.components.GraphComponent;
import org.insightlab.graphast.structure.GraphStructure;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;

import com.esotericsoftware.kryo.io.Input;

public class KryoStorage extends GraphStorage {
	
	private static GraphStorage instance = null;
	private Kryo kryo;
	
	private KryoStorage() {
		kryo = new Kryo();
		kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
	}
	
	/**
	 * @return the current GraphStorage's instance.
	 */
	public static GraphStorage getInstance() {
		if (instance == null) 
			instance = new KryoStorage();
		
		return instance;
	}

	@Override
	public Graph load(String path, GraphStructure structure) throws FileNotFoundException {
		long startTime = System.currentTimeMillis();
		Graph g = new Graph(structure);
		
		String directory = StorageUtils.ensureDirectory(path);
		Input in;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			
			in = new Input(new FileInputStream(directory + "nodes.bin"));
			while(!in.eof())
				g.addNode(kryo.readObject(in, Node.class));
			in.close();
			
			in = new Input(new FileInputStream(directory + "edges.bin"));
			while(!in.eof())
				g.addEdge(kryo.readObject(in, Edge.class));
			in.close();
			
			in = new Input(new FileInputStream(directory + "graph_components.bin"));
			while(!in.eof())
				g.addComponent(kryo.readObject(in, GraphComponent.class));
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long totalTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Time to load using kryo: " + (totalTime/1000.) + " s");
		
		return g;
	}

	@Override
	public void save(String path, Graph graph) {
		String directory = StorageUtils.ensureDirectory(path);
		Output out;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			out = new Output(new FileOutputStream(directory + "nodes.bin"));
			for (Node n : graph.getNodes())
				kryo.writeObject(out, n);
			out.close();
			out = new Output(new FileOutputStream(directory + "edges.bin"));
			for (Edge e : graph.getEdges())
				kryo.writeObject(out, e);
			out.close();
			out = new Output(new FileOutputStream(directory + "graph_components.bin"));
			for (GraphComponent component : graph.getAllComponents())
				kryo.writeObject(out, component);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
