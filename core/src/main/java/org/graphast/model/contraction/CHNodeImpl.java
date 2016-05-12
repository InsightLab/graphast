package org.graphast.model.contraction;

import org.graphast.model.Node;
import org.graphast.model.NodeImpl;

//What about extend NodeImpl?
public class CHNodeImpl extends NodeImpl implements CHNode, Comparable<CHNodeImpl> {

	private int priority;
	private int level;

	public CHNodeImpl(long externalId, double latitude, double longitude) {
		super(externalId, latitude, longitude);
	}

	// TODO Refactor this to copy the whole node at once
	public CHNodeImpl(Node node) {

		this.setId(node.getId());
		this.setExternalId(node.getExternalId());
		this.setCategory(node.getCategory());
		this.setLatitude(node.getLatitude());
		this.setLongitude(node.getLongitude());
		this.setLabel(node.getLabel());
		this.setCosts(node.getCosts());
		this.setLabel(node.getLabel());
		this.setFirstEdge(node.getFirstEdge());
		this.setCostsIndex(node.getCostsIndex());

	}

	// Increasing order of priority. When we call the poll() method, the node
	// with lowest priority will be removed
	@Override
	public int compareTo(CHNodeImpl np) {

		return Integer.valueOf(priority).compareTo(np.priority);

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CHNodeImpl) {
			CHNodeImpl c = (CHNodeImpl) o;
			return this.getId() == c.getId();
		}
		return false;
	}

	public int getPriority() {
		return priority;
	}

	public int getLevel() {
		return level;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return "CHNode [id=" + this.getId() + ", externalId=" + this.getExternalId() + ", latitude=" + this.getLatitude()
				+ ", longitude=" + this.getLongitude() + ", firstEdge=" + this.getFirstEdge() + ", label="+ this.getLabel()
				+ ", priority=" + this.getPriority() + ", level=" +this.getLevel() + "]";
	}

}
