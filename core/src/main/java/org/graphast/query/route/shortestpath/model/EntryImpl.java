package org.graphast.query.route.shortestpath.model;

import org.graphast.query.model.Entry;

/**
 * EntryImpl is a structure to represent an entry of something to evaluate and also has many different types.
 * This class is the basic structure with id and parent. The types starting from entry in Graphast are:
 * 	- DistanceEntry
 *	- TimeEntry
 */
public class EntryImpl implements Entry{

	private long id;
	private long parent;

	public EntryImpl(long id, long parent) {
		this.id = id;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.model.Entry#equals
	 */
	@Override
	public boolean equals(EntryImpl o) {
		return this.id == o.id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.model.Entry#getId()
	 */
	@Override
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.model.Entry#setId(long)
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.model.Entry#getParent()
	 */
	@Override
	public long getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.model.Entry#setParent(long)
	 */
	@Override
	public void setParent(long parent) {
		this.parent = parent;
	}
}
