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

package org.insightlab.graphast.model;

import org.insightlab.graphast.model.components.NodeComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Node class. It represents the model of a graph node.
 * It extends the GraphObject abstract class.
 */
public class Node extends GraphObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8614831471786755717L;

	private Map<Class<? extends NodeComponent>, NodeComponent> nodeComponents = null;
	
	private long id;
	
	public Node() {}
	
	/**
	 * Instantiates a new node.
	 *
	 * @param id the id of the new node.
	 */
	public Node(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the id from the node.
	 *
	 * @return the id from the node (long).
	 */
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public void addComponent(NodeComponent component) {
		addComponent(component.getClass(), component);
	}
	
	public void addComponent(Class<? extends NodeComponent> key, NodeComponent component) {
		if (nodeComponents == null)
			nodeComponents = new HashMap<>();
		nodeComponents.put(key, component);
		component.setNode(this);
	}
	
	public <C extends NodeComponent> C getComponent(Class<C> componentClass) {
		if (nodeComponents == null || !nodeComponents.containsKey(componentClass))
			return null;
		return componentClass.cast(nodeComponents.get(componentClass));
	}

	public boolean hasComponent(Class<? extends NodeComponent> key) {
		return getComponent(key) != null;
	}
	
	public Set<Class<? extends NodeComponent>> getAllComponentClasses() {
		return nodeComponents.keySet();
	}
	
	/**
	 * Equals method. It receives an object and checks if it is a Node
	 * and if it has the same id than the Node object calling this function.
	 *
	 * @param obj the object to check equality.
	 * @return true, if it is a Node object and has the same id than the Node
	 * instance calling this function. false, otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Node))
			return false;
		
		return ((Node)obj).getId() == id;
		
 	}
	
	/**
	 * Hash code function of the object (overridden).
	 *
	 * @return the 32-bit signed integer hash value of the Node's instance id.
	 */
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

}
