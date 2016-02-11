package org.graphast.query.route.shortestpath.model;

import org.graphast.query.model.Entry;

public class EntryImpl implements Entry{

	private long id;
	private long parent;

	public EntryImpl(long id, long parent) {
		this.id = id;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#equals(org.graphast.query.route.shortestpath.EntryImpl)
	 */
	@Override
	public boolean equals(EntryImpl o) {
		return this.id == o.id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#getId()
	 */
	@Override
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#setId(long)
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#getParent()
	 */
	@Override
	public long getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#setParent(long)
	 */
	@Override
	public void setParent(long parent) {
		this.parent = parent;
	}
}
