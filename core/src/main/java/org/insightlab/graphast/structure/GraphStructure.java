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

package org.insightlab.graphast.structure;

import java.util.Iterator;
import java.util.Set;

import org.insightlab.graphast.cards.GraphCard;
import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;

/**
 * The GraphStructure interface. This interface contains declarations of general methods
 * for different GraphStructure's implementations.
 */
public interface GraphStructure {
	
	/**
	 * Add a new node into the graph.
	 * @param node the node that will be added.
	 */
	void addNode(Node node);
	
	/**
	 * Add a new edge into the graph.
	 * @param edge the edge that will be added into the graph.
	 */
	void addEdge(Edge edge);
	
	/**
	 * Verify whether the node which has the given id is in the graph or not.
	 * @param id the node's id.
	 */
	boolean containsNode(final long id);
	
	/**
	 * @return an iterator to graph's nodes.
	 */
	Iterator<Node> nodeIterator();
	
	/**
	 * @return an iterator to graph's edges.
	 */
	Iterator<Edge> edgeIterator();
	
	/**
	 * @return the number of graph's nodes.
	 */
	long getNumberOfNodes();
	
	/**
	 * @return the number of graph's edges.
	 */
	long getNumberOfEdges();

	/**
	 * @return the out edges of the node which has the given id.
	 * @param id the node's id.
	 */
	Iterator<Edge> getOutEdges(final long id);
	
	/**
	 * @param id the node's id.
	 * @return the in edges of the node which has the given id.
	 */
	Iterator<Edge> getInEdges(final long id);
	
	
	Set<String> getAllCardNames();
	
	
	GraphCard getCard(String cardName);
	
	
	void setCard(String cardName, GraphCard card);
	
}
