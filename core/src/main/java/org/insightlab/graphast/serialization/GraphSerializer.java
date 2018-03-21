/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package org.insightlab.graphast.serialization;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.structure.DefaultGraphStructure;
import org.insightlab.graphast.structure.GraphStructure;

import java.io.FileNotFoundException;

/**
 * The GraphStorage interface. This interface contains declarations of general methods
 * for different GraphStorage's implementations.
 */
public abstract class GraphSerializer {

	public static GraphSerializer getInstance() {
		return KryoSerializer.getInstance();
	}

	/**
	 * Load a graph from the given path and structure.
	 * @param path to search the file that contains the graph.
	 * @param structure that represents the structure of the graph.
	 * @return the graph loaded.
	 */
	
	public abstract Graph load(String path, GraphStructure structure) throws FileNotFoundException;
	
	public Graph load(String path) throws FileNotFoundException {
		return load(path, new DefaultGraphStructure());
	}
	
	/**
	 * Save the graph into the given path.
	 * @param path to save the file that contains the graph.
	 * @param graph that will be saved.
	 */
	public abstract void save(String path, Graph graph);
	
}