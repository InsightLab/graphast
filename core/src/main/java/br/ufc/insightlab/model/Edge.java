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

package br.ufc.insightlab.model;

import br.ufc.insightlab.model.components.EdgeComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Edge class. It represents the model of a graph edge.
 * It extends the GraphObject abstract class.
 */
public class Edge extends GraphObject {

	private static Integer nextId = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3414011816257456570L;

	private Map<Class<? extends EdgeComponent>, EdgeComponent> edgeComponent = null;
	
	private long id;
	private long fromNodeId;
	private long toNodeId;
	private double weight;
	private boolean bidirectional;
	
	public Edge() {
		this.weight = 1;
		this.id = nextId++;
	}
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param from the id from the outgoing node.
	 * @param to the id from the incoming node.
	 */
	public Edge(long from, long to) {
		this(from, to, 1);
	}
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param from the id from the outgoing node.
	 * @param to the id from the incoming node.
	 * @param cost the cost value of the edge.
	 */
	public Edge(long from, long to, double cost) {
		this.id = nextId++;
		this.fromNodeId = from;
		this.toNodeId   = to;
		this.weight     = cost;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the id of the node in which the edge is outing from. 
	 *
	 * @return the id from the outing node of the edge.
	 */
	public long getFromNodeId() {
		return fromNodeId;
	}
	
	/**
	 * Gets the id of the node in which the edge is going into.
	 *
	 * @return the id from the incoming node of the edge.
	 */
	public long getToNodeId() {
		return toNodeId;
	}
	
	/**
	 * Gets the cost value of the edge.
	 *
	 * @return the cost of the edge (double).
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Sets the id of the node in which the edge is outing from.
	 *
	 * @param fromNode the new id from the outing node of the edge.
	 */
	public void setFromNodeId(long fromNode) {
		this.fromNodeId = fromNode;
	}
	
	/**
	 * sets the id of the node in which the edge is going into.
	 *
	 * @param toNode the new id from the incoming node of the edge.
	 */
	public void setToNodeId(long toNode) {
		this.toNodeId = toNode;
	}
	
	/**
	 * Sets the weight value of the edge.
	 *
	 * @param weight the new weight of the edge.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}
	
	/**
	 * Checks if the edge is bidirectional, e.g. the 
	 * edge points both in and outward its two nodes.
	 *
	 * @return true, if the edge is bidirectional.
	 */
	public boolean isBidirectional() {
		return bidirectional;
	}
	
	public void invert() {
		long aux = this.fromNodeId;
		this.fromNodeId = this.toNodeId;
		this.toNodeId = aux;
	}
	
	/**
	 * Gets the id from a node adjacent to this edge in the graph, give a node id.
	 * The given id is compared to the value of the incoming node id from this edge.
	 * If the two id values match, the id from the outgoing node of this edge is returned.
	 * Otherwise, the id from the incoming node is returned.
	 *
	 * @param id the id from the node from which we wish to discover its adjacent.
	 * @return the id from the adjacent node, based on the given id.
	 */
	public long getAdjacent(long id) {
		return id == toNodeId ? fromNodeId : toNodeId;
	}

	public void addComponent(EdgeComponent component) {
		addComponent(component.getClass(), component);
	}
	
	public void addComponent(Class<? extends EdgeComponent> key, EdgeComponent component) {
		if (edgeComponent == null)
			edgeComponent = new HashMap<>();
		edgeComponent.put(key, component);
		component.setEdge(this);
	}
	
	public <C extends EdgeComponent> C getComponent(Class<C> componentClass) {
		if (edgeComponent == null || !edgeComponent.containsKey(componentClass))
			return null;
		return componentClass.cast(edgeComponent.get(componentClass));
	}

	public boolean hasComponent(Class<? extends EdgeComponent> key) {
		return getComponent(key) != null;
	}
	
	public Set<Class<? extends EdgeComponent>> getAllComponentClasses() {
		return edgeComponent.keySet();
	}
	
	/**
	 * Equals method. It receives an object and checks if it is an Edge object
	 * and if it has the same incoming node id, the same outcoming node id and
	 * the same cost value. It uses the string representation of the edges to 
	 * calculate this equality.
	 *
	 * @param obj the object to check equality.
	 * @return true, if the received object is an Edge object and it has the same
	 * values for its attributes than the instance calling this function. false, 
	 * otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Edge)) 
			return false;
		
		Edge other = (Edge) obj;
		
		boolean okFromNode = this.fromNodeId == other.fromNodeId;
		boolean okToNode   = this.toNodeId   == other.toNodeId;
		boolean okCost     = this.weight     == other.weight;
		
		return okFromNode && okToNode && okCost;
		
//		return (fromNode + "|" + toNode + "|" + cost)
//				.equals(other.getFromNodeId()+"|"+other.getToNodeId()+"|"+other.getCost());
		
	}
	
	/**
	 * Hash code function of the object (overridden).
	 *
	 * @return the 32-bit signed integer hash value of the Edge's toString 
	 * representation's instance id.
	 */
	@Override
	public int hashCode() {
		return (fromNodeId + "|" + toNodeId + "|" + weight).hashCode();
	}
	
	/**
	 * To string function (overridden).
	 *
	 * @return the string representation of the edge values.
	 */
	@Override
	public String toString() {
		return fromNodeId + "|" + toNodeId + "|" + weight;
	}

}
