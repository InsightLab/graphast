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

import java.io.File;

/**
 * This utility class implements a method that delete graph's data.
 *
 */

public class SerializationUtils {
	
	private SerializationUtils() {}
	
	public static String ensureDirectory(String path) {
		if (!path.endsWith("/"))
			return path + "/";
		return path;
	}
	
	/**
	 * Delete a graph from the given path.
	 * @param path to search the file that contains the graph that will be deleted.
	 */
	public static boolean deleteMMapGraph(String path) {
		String directory = ensureDirectory(path);

		File f = new File(directory);
		boolean ok = true;
		
		if (f.exists()) {
			ok = new File(directory + "nodes.mmap").delete();
			ok = new File(directory + "edges.mmap").delete() && ok;
			ok = new File(directory + "treeMap.mmap").delete() && ok;
			ok = f.delete() && ok;
		}
		
		return ok;
	}
	
	
	public static boolean deleteSerializedGraph(String path) {
		String directory = ensureDirectory(path);
		
		File f = new File(directory);
		boolean ok = true;
		
		if (f.exists()) {
			ok = new File(directory + "nodes.phast").delete();
			ok = new File(directory + "edges.phast").delete() && ok;
			ok = new File(directory + "graph_components.phast").delete() && ok;
			ok = f.delete() && ok;
		}
		
		return ok;
	}

}
