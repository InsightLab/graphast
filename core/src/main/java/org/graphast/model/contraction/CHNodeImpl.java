package org.graphast.model.contraction;

import java.util.Comparator;
import java.util.List;

import org.graphast.model.Node;
import org.graphast.model.NodeImpl;

//What about extend NodeImpl?
public class CHNodeImpl extends NodeImpl implements CHNode, Comparable<CHNodeImpl> {

	private int priority;
	private int level;

	public CHNodeImpl(long externalId, double latitude, double longitude) {
		super(externalId, latitude, longitude);
	}
	
	public CHNodeImpl(long externalId, double latitude, double longitude, int category) {
		super(externalId, latitude, longitude, category);
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

//		return Integer.valueOf(np.priority).compareTo(priority);
		
		int test = Integer.valueOf(priority).compareTo(np.priority);
		
		if(test == 0) {
//			return Long.compare(this.getId(), np.getId());
			return Long.compare(this.getExternalId(), np.getExternalId());
		}
		
		return test;

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

class HighPriorityComparator implements Comparator<CHNodeImpl> {
    public int compare(CHNodeImpl task1, CHNodeImpl task2) {
        return task1.compareTo(task2);
    }
}

class IdComparator implements Comparator<CHNodeImpl> {
    public int compare(CHNodeImpl task1, CHNodeImpl task2) {
        int compareResult = 0;
        if (task2.getId() < task1.getId())
            compareResult = 1;
        else
            compareResult = -1;

        return compareResult;
    }
}

class MixAndMatchComparator implements Comparator<CHNodeImpl> {

    List<Comparator<CHNodeImpl>> comparators;

    public MixAndMatchComparator(List<Comparator<CHNodeImpl>> comparators) {
        this.comparators=comparators;
    }

    @Override
    public int compare(CHNodeImpl o1, CHNodeImpl o2) {
        int compareResult = 0;
        for(Comparator<CHNodeImpl> comparator : comparators) {
            if(comparator.compare(o1, o2)!=0) {
                return comparator.compare(o1, o2);
            }
        }
        return compareResult;
    }

}
