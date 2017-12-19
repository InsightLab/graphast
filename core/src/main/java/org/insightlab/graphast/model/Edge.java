package org.insightlab.graphast.model;

/**
 * The Edge class. It represents the model of a graph edge.
 * It extends the GraphObject abstract class.
 */
public class Edge extends GraphObject {
	
	private long fromNode;
	private long toNode;
	private double cost;
	private boolean bidirectional;
	
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
		this.fromNode = from;
		this.toNode= to;
		this.cost = cost;
	}
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param from the id from the outgoing node.
	 * @param to the id from the incoming node.
	 * @param bidirectional true, if the edge is bidirectional. false, otherwise.
	 */
	public Edge(long from, long to, boolean bidirectional) {
		this(from, to, 1, bidirectional);
	}
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param from the id from the outgoing node.
	 * @param to the id from the incoming node.
	 * @param cost the cost value of the edge.
	 * @param bidirectional true, if the edge is bidirectional. false, otherwise.
	 */
	public Edge(long from, long to, double cost, boolean bidirectional) {
		this(from, to, cost);
		this.bidirectional = bidirectional;
	}
	
	/**
	 * Gets the id of the node in which the edge is outing from. 
	 *
	 * @return the id from the outing node of the edge.
	 */
	public long getFromNodeId() {
		return fromNode;
	}
	
	/**
	 * Gets the id of the node in which the edge is going into.
	 *
	 * @return the id from the incoming node of the edge.
	 */
	public long getToNodeId() {
		return toNode;
	}
	
	/**
	 * Gets the cost value of the edge.
	 *
	 * @return the cost of the edge (double).
	 */
	public double getCost() {
		return cost;
	}
	
	/**
	 * Sets the id of the node in which the edge is outing from.
	 *
	 * @param fromNode the new id from the outing node of the edge.
	 */
	public void setFromNodeId(long fromNode) {
		this.fromNode = fromNode;
	}
	
	/**
	 * sets the id of the node in which the edge is going into.
	 *
	 * @param toNode the new id from the incoming node of the edge.
	 */
	public void setToNodeId(long toNode) {
		this.toNode = toNode;
	}
	
	/**
	 * Sets the cost value of the edge.
	 *
	 * @param cost the new cost of the edge.
	 */
	public void setCost(double cost) {
		this.cost = cost;
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
		return id == toNode ? fromNode : toNode;
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
		if (!(obj instanceof Edge)) return false;
		Edge other = (Edge) obj;
		return (this.fromNode==other.fromNode && this.toNode==other.toNode &&
				this.cost==other.cost);
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
		return (fromNode + "|" + toNode + "|" + cost).hashCode();
	}
	
	/**
	 * To string function (overridden).
	 *
	 * @return the string representation of the edge values.
	 */
	@Override
	public String toString() {
		return fromNode + "|" + toNode + "|" + cost;
	}

}
